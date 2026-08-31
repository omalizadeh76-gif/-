package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AdminMessage
import com.example.data.model.AlarmSoundType
import com.example.data.model.AppThemeMode
import com.example.data.model.AppUpdateInfo
import com.example.data.model.ExpiryStatus
import com.example.data.model.InspectionRecord
import com.example.data.model.InventoryFilter
import com.example.data.model.InventorySort
import com.example.data.model.InventoryStats
import com.example.data.model.ItemCategory
import com.example.data.model.MedicalItem
import com.example.data.model.UpdateCheckState
import com.example.data.repository.MedicalItemRepository
import com.example.notification.AlarmScheduler
import com.example.notification.NotificationHelper
import com.example.ui.util.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicalViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val ADMIN_PASSWORD = "15137677"
    }

    private val repository: MedicalItemRepository
    private val prefs = application.getSharedPreferences("medical_app_prefs", Context.MODE_PRIVATE)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(InventoryFilter.ALL)
    val selectedFilter: StateFlow<InventoryFilter> = _selectedFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(InventorySort.EXPIRY_ASC)
    val selectedSort: StateFlow<InventorySort> = _selectedSort.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    private val _isAddEditDialogOpen = MutableStateFlow(false)
    val isAddEditDialogOpen: StateFlow<Boolean> = _isAddEditDialogOpen.asStateFlow()

    private val _editingItem = MutableStateFlow<MedicalItem?>(null)
    val editingItem: StateFlow<MedicalItem?> = _editingItem.asStateFlow()

    private val _themeMode = MutableStateFlow(loadSavedThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _alarmSoundType = MutableStateFlow(NotificationHelper.getSavedSoundType(application))
    val alarmSoundType: StateFlow<AlarmSoundType> = _alarmSoundType.asStateFlow()

    // Inspection Dialog / Result State
    private val _lastInspectionResult = MutableStateFlow<InspectionRecord?>(null)
    val lastInspectionResult: StateFlow<InspectionRecord?> = _lastInspectionResult.asStateFlow()

    private val _showInspectionDialog = MutableStateFlow(false)
    val showInspectionDialog: StateFlow<Boolean> = _showInspectionDialog.asStateFlow()

    // Admin authentication state in session
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    // In-App Update States
    private val _appUpdateInfo = MutableStateFlow(AppUpdateInfo())
    val appUpdateInfo: StateFlow<AppUpdateInfo> = _appUpdateInfo.asStateFlow()

    private val _updateCheckState = MutableStateFlow(UpdateCheckState.IDLE)
    val updateCheckState: StateFlow<UpdateCheckState> = _updateCheckState.asStateFlow()

    private val _showInAppUpdatePrompt = MutableStateFlow(false)
    val showInAppUpdatePrompt: StateFlow<Boolean> = _showInAppUpdatePrompt.asStateFlow()

    private val _updateDownloadProgress = MutableStateFlow(0f)
    val updateDownloadProgress: StateFlow<Float> = _updateDownloadProgress.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = MedicalItemRepository(
            db.medicalItemDao(),
            db.inspectionDao(),
            db.adminMessageDao()
        )

        // Schedule daily alarm check
        viewModelScope.launch {
            AlarmScheduler.scheduleDailyCheck(application)
        }
    }

    val inspectionHistory: StateFlow<List<InspectionRecord>> = repository.allInspections.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val adminMessages: StateFlow<List<AdminMessage>> = repository.allAdminMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val latestUnreadAdminMessage: StateFlow<AdminMessage?> = repository.latestUnreadAdminMessage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private fun loadSavedThemeMode(): AppThemeMode {
        val name = prefs.getString("app_theme_mode", AppThemeMode.SYSTEM.name)
        return try {
            AppThemeMode.valueOf(name ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("app_theme_mode", mode.name).apply()
    }

    fun setAlarmSoundType(type: AlarmSoundType) {
        _alarmSoundType.value = type
        NotificationHelper.setSavedSoundType(getApplication(), type)
    }

    // Combine raw items with search query, filter category/status/location, and sort
    val filteredItems: StateFlow<List<MedicalItem>> = combine(
        repository.allItems,
        _searchQuery,
        _selectedFilter,
        _selectedSort
    ) { rawItems, query, filter, sort ->
        val now = System.currentTimeMillis()

        // 1. Filter by search query
        val searched = if (query.isBlank()) {
            rawItems
        } else {
            val q = query.trim().lowercase()
            rawItems.filter {
                it.name.lowercase().contains(q) ||
                    it.location.lowercase().contains(q) ||
                    it.notes.lowercase().contains(q) ||
                    it.unit.lowercase().contains(q)
            }
        }

        // 2. Filter by category, location, or expiry status
        val filtered = when (filter) {
            InventoryFilter.ALL -> searched
            InventoryFilter.JUMPBAG_MEDICINES -> searched.filter {
                it.category == ItemCategory.MEDICINE && it.location.contains("جامبگ", ignoreCase = true)
            }
            InventoryFilter.CABINET_MEDICINES -> searched.filter {
                it.category == ItemCategory.MEDICINE && !it.location.contains("جامبگ", ignoreCase = true)
            }
            InventoryFilter.JUMPBAG_EQUIPMENT -> searched.filter {
                it.category == ItemCategory.EQUIPMENT && it.location.contains("جامبگ", ignoreCase = true)
            }
            InventoryFilter.CABINET_EQUIPMENT -> searched.filter {
                it.category == ItemCategory.EQUIPMENT && !it.location.contains("جامبگ", ignoreCase = true)
            }
            InventoryFilter.MEDICINES_ONLY -> searched.filter { it.category == ItemCategory.MEDICINE }
            InventoryFilter.EQUIPMENT_ONLY -> searched.filter { it.category == ItemCategory.EQUIPMENT }
            InventoryFilter.EXPIRING_SOON_ONLY -> searched.filter {
                it.hasExpiryDate && it.getExpiryStatus(now) == ExpiryStatus.EXPIRING_SOON
            }
            InventoryFilter.EXPIRED_ONLY -> searched.filter {
                it.hasExpiryDate && it.getExpiryStatus(now) == ExpiryStatus.EXPIRED
            }
            InventoryFilter.SAFE_ONLY -> searched.filter {
                !it.hasExpiryDate || it.getExpiryStatus(now) == ExpiryStatus.SAFE
            }
        }

        // 3. Sort items
        when (sort) {
            InventorySort.EXPIRY_ASC -> filtered.sortedWith(
                compareBy<MedicalItem> { !it.hasExpiryDate }
                    .thenBy { it.expiryTimestamp }
            )
            InventorySort.EXPIRY_DESC -> filtered.sortedWith(
                compareBy<MedicalItem> { !it.hasExpiryDate }
                    .thenByDescending { it.expiryTimestamp }
            )
            InventorySort.NAME_ASC -> filtered.sortedBy { it.name }
            InventorySort.NAME_DESC -> filtered.sortedByDescending { it.name }
            InventorySort.LOCATION_ASC -> filtered.sortedBy { it.location }
            InventorySort.CREATED_DESC -> filtered.sortedByDescending { it.createdAt }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Calculate dynamic inventory stats
    val stats: StateFlow<InventoryStats> = repository.allItems.combine(_searchQuery) { items, _ ->
        val now = System.currentTimeMillis()
        var medicines = 0
        var equipment = 0
        var expiringSoon = 0
        var expired = 0
        var safe = 0
        var jumpBagMed = 0
        var cabinetMed = 0
        var jumpBagEq = 0
        var cabinetEq = 0

        items.forEach { item ->
            val isJumpBag = item.location.contains("جامبگ", ignoreCase = true)
            if (item.category == ItemCategory.MEDICINE) {
                medicines++
                if (isJumpBag) jumpBagMed++ else cabinetMed++
            } else {
                equipment++
                if (isJumpBag) jumpBagEq++ else cabinetEq++
            }

            if (item.hasExpiryDate) {
                when (item.getExpiryStatus(now)) {
                    ExpiryStatus.EXPIRED -> expired++
                    ExpiryStatus.EXPIRING_SOON -> expiringSoon++
                    ExpiryStatus.SAFE -> safe++
                    ExpiryStatus.NO_EXPIRY -> safe++
                }
            } else {
                safe++
            }
        }

        InventoryStats(
            totalItems = items.size,
            medicineCount = medicines,
            equipmentCount = equipment,
            expiringSoonCount = expiringSoon,
            expiredCount = expired,
            safeCount = safe,
            jumpBagMedicineCount = jumpBagMed,
            cabinetMedicineCount = cabinetMed,
            jumpBagEquipmentCount = jumpBagEq,
            cabinetEquipmentCount = cabinetEq
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryStats()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterSelect(filter: InventoryFilter) {
        _selectedFilter.value = filter
    }

    fun onSortSelect(sort: InventorySort) {
        _selectedSort.value = sort
    }

    fun openAddDialog(item: MedicalItem? = null) {
        _editingItem.value = item
        _isAddEditDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddEditDialogOpen.value = false
        _editingItem.value = null
    }

    fun saveMedicalItem(item: MedicalItem) {
        viewModelScope.launch {
            if (item.id == 0) {
                repository.insertItem(item)
                _feedbackMessage.value = "«${item.name}» با موفقیت اضافه شد."
            } else {
                repository.updateItem(item)
                _feedbackMessage.value = "تغییرات «${item.name}» ذخیره شد."
            }
            closeAddDialog()
        }
    }

    fun deleteMedicalItem(item: MedicalItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
            _feedbackMessage.value = "«${item.name}» حذف شد."
        }
    }

    fun clearFeedbackMessage() {
        _feedbackMessage.value = null
    }

    /**
     * Executes automated inspection of all items, calculates breakdown,
     * triggers notifications if necessary, records to history database,
     * and shows an inspection report dialog.
     */
    fun performFullInventoryInspection() {
        viewModelScope.launch {
            val items = repository.getAllItemsList()
            val now = System.currentTimeMillis()

            var expired = 0
            var expiringSoon = 0
            var safe = 0
            var jumpBagMed = 0
            var cabinetMed = 0
            var jumpBagEq = 0
            var cabinetEq = 0

            val expiredNames = mutableListOf<String>()
            val expiringSoonNames = mutableListOf<String>()

            items.forEach { item ->
                val isJumpBag = item.location.contains("جامبگ", ignoreCase = true)
                if (item.category == ItemCategory.MEDICINE) {
                    if (isJumpBag) jumpBagMed++ else cabinetMed++
                } else {
                    if (isJumpBag) jumpBagEq++ else cabinetEq++
                }

                if (item.hasExpiryDate) {
                    when (item.getExpiryStatus(now)) {
                        ExpiryStatus.EXPIRED -> {
                            expired++
                            expiredNames.add(item.name)
                        }
                        ExpiryStatus.EXPIRING_SOON -> {
                            expiringSoon++
                            expiringSoonNames.add(item.name)
                        }
                        ExpiryStatus.SAFE -> safe++
                        ExpiryStatus.NO_EXPIRY -> safe++
                    }
                } else {
                    safe++
                }
            }

            val summaryDetails = buildString {
                if (expiredNames.isNotEmpty()) {
                    append("منقضی شده (${expiredNames.size}): ")
                    append(expiredNames.take(3).joinToString("، "))
                    if (expiredNames.size > 3) append(" و ...")
                    append("\n")
                }
                if (expiringSoonNames.isNotEmpty()) {
                    append("در آستانه انقضا (${expiringSoonNames.size}): ")
                    append(expiringSoonNames.take(3).joinToString("، "))
                    if (expiringSoonNames.size > 3) append(" و ...")
                }
                if (expiredNames.isEmpty() && expiringSoonNames.isEmpty()) {
                    append("وضعیت تمام اقلام بررسی شده معتبر و بدون مشکل است.")
                }
            }

            val statusNote = if (items.isEmpty()) {
                "هیچ قلم دارویی یا تجهیزاتی در سیستم ثبت نشده است."
            } else if (expired > 0) {
                "🚨 $expired قلم دارویی یا تجهیزاتی منقضی شده وجود دارد!"
            } else if (expiringSoon > 0) {
                "⚠️ $expiringSoon قلم دارویی یا تجهیزاتی در آستانه انقضا هستند."
            } else {
                "✅ تمام $safe قلم ورودی دارای تاریخ اعتبار معتبر می‌باشند."
            }

            val record = InspectionRecord(
                timestamp = now,
                totalChecked = items.size,
                expiredCount = expired,
                expiringSoonCount = expiringSoon,
                safeCount = safe,
                jumpBagMedicineCount = jumpBagMed,
                medicineCabinetCount = cabinetMed,
                jumpBagEquipmentCount = jumpBagEq,
                equipmentCabinetCount = cabinetEq,
                detailsSummary = summaryDetails.trim(),
                statusNote = statusNote
            )

            // Save record in database
            repository.insertInspection(record)

            _lastInspectionResult.value = record
            _showInspectionDialog.value = true

            // Trigger system notification if items need attention
            if (expired > 0 || expiringSoon > 0) {
                NotificationHelper.checkAndNotifyExpiringMedicines(getApplication())
            }

            _feedbackMessage.value = "بررسی خودکار انجام و در تاریخچه ثبت شد."
        }
    }

    fun dismissInspectionDialog() {
        _showInspectionDialog.value = false
    }

    fun deleteInspectionRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteInspection(id)
            _feedbackMessage.value = "رکورد تاریخچه حذف شد."
        }
    }

    fun clearAllInspectionHistory() {
        viewModelScope.launch {
            repository.clearInspectionHistory()
            _feedbackMessage.value = "کل تاریخچه بررسی پاک شد."
        }
    }

    // Admin & Broadcast Management
    fun verifyAdminPassword(input: String): Boolean {
        // Normalize Persian/Arabic digits to English digits
        val normalized = input.trim().map { ch ->
            when (ch) {
                '۰', '٠' -> '0'
                '۱', '١' -> '1'
                '۲', '٢' -> '2'
                '۳', '٣' -> '3'
                '۴', '٤' -> '4'
                '۵', '٥' -> '5'
                '۶', '٦' -> '6'
                '۷', '٧' -> '7'
                '۸', '٨' -> '8'
                '۹', '٩' -> '9'
                else -> ch
            }
        }.joinToString("")

        val isMatch = normalized == ADMIN_PASSWORD
        if (isMatch) {
            _isAdminAuthenticated.value = true
        }
        return isMatch
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
    }

    fun broadcastAdminMessage(
        title: String,
        content: String,
        priority: String = "HIGH",
        targetCategory: String = "ALL",
        senderName: String = "مدیریت سامانه"
    ) {
        val trimmedTitle = title.trim()
        val trimmedContent = content.trim()

        if (trimmedTitle.isEmpty() || trimmedContent.isEmpty()) {
            _feedbackMessage.value = "لطفاً عنوان و متن پیام را وارد نمایید."
            return
        }

        viewModelScope.launch {
            val message = AdminMessage(
                title = trimmedTitle,
                content = trimmedContent,
                priority = priority,
                targetCategory = targetCategory,
                senderName = senderName,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )

            repository.insertAdminMessage(message)

            // Trigger real Android notification with sound
            NotificationHelper.sendAdminBroadcastNotification(
                context = getApplication(),
                title = trimmedTitle,
                body = trimmedContent,
                priority = priority
            )

            _feedbackMessage.value = "پیام مدیریت با موفقیت ارسال و اعلان صادر شد."
        }
    }

    fun markAdminMessageAsRead(id: Int) {
        viewModelScope.launch {
            repository.markAdminMessageRead(id)
        }
    }

    fun deleteAdminMessage(id: Int) {
        viewModelScope.launch {
            repository.deleteAdminMessage(id)
            _feedbackMessage.value = "پیام مدیریت حذف شد."
        }
    }

    fun clearAllAdminMessages() {
        viewModelScope.launch {
            repository.clearAdminMessages()
            _feedbackMessage.value = "تمام پیام‌های مدیریت پاک شدند."
        }
    }

    // In-App Update Actions
    fun checkForUpdates(forceSimulateNewVersion: Boolean = false, showNotificationIfNoUpdate: Boolean = true) {
        viewModelScope.launch {
            _updateCheckState.value = UpdateCheckState.CHECKING
            delay(1200) // Realistic check delay

            if (forceSimulateNewVersion || _appUpdateInfo.value.isUpdateAvailable) {
                _updateCheckState.value = UpdateCheckState.UPDATE_AVAILABLE
                _showInAppUpdatePrompt.value = true
                _feedbackMessage.value = "نسخه جدید برنامه یافت شد!"
            } else {
                _updateCheckState.value = UpdateCheckState.UP_TO_DATE
                if (showNotificationIfNoUpdate) {
                    _feedbackMessage.value = "برنامه شما کاملاً به‌روز است (نسخه ${_appUpdateInfo.value.currentVersionName})."
                }
            }
        }
    }

    fun startInAppUpdate(context: Context = getApplication()) {
        viewModelScope.launch {
            _updateCheckState.value = UpdateCheckState.DOWNLOADING
            _updateDownloadProgress.value = 0f

            // Progress smoothly from 0 to 100%
            for (i in 1..10) {
                delay(180)
                _updateDownloadProgress.value = i / 10f
            }

            _updateCheckState.value = UpdateCheckState.READY_TO_INSTALL
            delay(400)

            // Update app update info to reflect latest installed version
            val latest = _appUpdateInfo.value.latestVersionName
            val latestCode = _appUpdateInfo.value.latestVersionCode
            _appUpdateInfo.value = _appUpdateInfo.value.copy(
                currentVersionName = latest,
                currentVersionCode = latestCode,
                isUpdateAvailable = false
            )

            _updateCheckState.value = UpdateCheckState.UP_TO_DATE
            _showInAppUpdatePrompt.value = false
            _feedbackMessage.value = "برنامه با موفقیت به نسخه $latest به‌روزرسانی شد. تمام اطلاعات و تنظیمات داروها حفظ شدند."
        }
    }

    fun dismissUpdatePrompt() {
        _showInAppUpdatePrompt.value = false
    }

    fun openUpdateLink(context: Context, url: String) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            _feedbackMessage.value = "امکان باز کردن پیوند دانلود وجود ندارد."
        }
    }

    fun openUpdateDownloadLink(url: String, context: Context = getApplication()) {
        openUpdateLink(context, url)
    }

    fun publishNewVersionRelease(
        newVersionName: String,
        newVersionCode: Int,
        releaseNotes: String,
        isMandatory: Boolean = false
    ) {
        adminPublishNewVersion(newVersionName, newVersionCode, releaseNotes, isMandatory)
    }

    fun adminPublishNewVersion(
        newVersionName: String,
        newVersionCode: Int,
        releaseNotes: String,
        isMandatory: Boolean = false
    ) {
        val vName = newVersionName.trim().ifEmpty { "2.3.0" }
        val notesList = if (releaseNotes.isNotBlank()) {
            releaseNotes.lines().map { it.trim().removePrefix("-").removePrefix("•").trim() }.filter { it.isNotEmpty() }
        } else {
            listOf("بهبود سرعت و کارایی سیستم", "بهینه‌سازی دریافت اعلانات و رفع خطاهای جزئی")
        }

        val shamsiToday = DateUtils.formatShamsiDate(System.currentTimeMillis())

        val updatedInfo = AppUpdateInfo(
            currentVersionName = _appUpdateInfo.value.currentVersionName,
            currentVersionCode = _appUpdateInfo.value.currentVersionCode,
            latestVersionName = vName,
            latestVersionCode = newVersionCode,
            releaseDateShamsi = shamsiToday,
            changelog = notesList,
            isUpdateAvailable = true,
            isMandatory = isMandatory,
            lastCheckedTimestamp = System.currentTimeMillis()
        )

        _appUpdateInfo.value = updatedInfo
        _updateCheckState.value = UpdateCheckState.UPDATE_AVAILABLE
        _showInAppUpdatePrompt.value = true

        // Broadcast to notifications as well
        viewModelScope.launch {
            val title = "🚀 نسخه جدید $vName منتشر شد!"
            val body = "تغییرات: ${notesList.firstOrNull() ?: "بهبودهای عملکردی و ظاهری"}. برای به‌روزرسانی بدون از دست رفتن داده‌ها کلیک کنید."
            
            val message = AdminMessage(
                title = title,
                content = body,
                priority = "HIGH",
                targetCategory = "ALL",
                senderName = "سیستم به‌روزرسانی هوشمند",
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
            repository.insertAdminMessage(message)

            NotificationHelper.sendAdminBroadcastNotification(
                context = getApplication(),
                title = title,
                body = body,
                priority = "HIGH"
            )

            _feedbackMessage.value = "نسخه $vName با موفقیت منتشر و اعلان سراسری به پرسنل ارسال شد."
        }
    }
}
