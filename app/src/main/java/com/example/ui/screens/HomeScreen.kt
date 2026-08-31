package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.InventoryFilter
import com.example.data.model.InventorySort
import com.example.data.model.MedicalItem
import com.example.ui.components.AboutDialog
import com.example.ui.components.AddEditItemDialog
import com.example.ui.components.FilterChipsRow
import com.example.ui.components.InAppUpdateDialog
import com.example.ui.components.InspectionReportDialog
import com.example.ui.components.MedicalCalculatorDialog
import com.example.ui.components.MedicalItemCard
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StatsOverview
import com.example.ui.theme.ExpiryExpiredContainer
import com.example.ui.theme.ExpiryExpiredRed
import com.example.ui.theme.ExpiryWarningContainer
import com.example.ui.theme.ExpiryWarningOrange
import com.example.ui.theme.MedBlueContainer
import com.example.ui.theme.MedBlueSecondary
import com.example.ui.util.DateUtils
import com.example.ui.viewmodel.MedicalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MedicalViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.filteredItems.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val isAddEditOpen by viewModel.isAddEditDialogOpen.collectAsStateWithLifecycle()
    val editingItem by viewModel.editingItem.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val currentSoundType by viewModel.alarmSoundType.collectAsStateWithLifecycle()
    val inspectionHistory by viewModel.inspectionHistory.collectAsStateWithLifecycle()
    val showInspectionDialog by viewModel.showInspectionDialog.collectAsStateWithLifecycle()
    val lastInspectionResult by viewModel.lastInspectionResult.collectAsStateWithLifecycle()

    val isAdminAuthenticated by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
    val adminMessages by viewModel.adminMessages.collectAsStateWithLifecycle()
    val latestUnreadAdminMessage by viewModel.latestUnreadAdminMessage.collectAsStateWithLifecycle()

    val appUpdateInfo by viewModel.appUpdateInfo.collectAsStateWithLifecycle()
    val updateCheckState by viewModel.updateCheckState.collectAsStateWithLifecycle()
    val showInAppUpdatePrompt by viewModel.showInAppUpdatePrompt.collectAsStateWithLifecycle()
    val updateDownloadProgress by viewModel.updateDownloadProgress.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var itemToDelete by remember { mutableStateOf<MedicalItem?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCalculatorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearFeedbackMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalInformation,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "مدیریت انقضای دارو و تجهیزات",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "فوریت‌های پزشکی و درمانی",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Sort menu button
                        Box {
                            IconButton(
                                onClick = { showSortMenu = true },
                                modifier = Modifier.size(36.dp).testTag("sort_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "مرتب‌سازی",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                InventorySort.entries.forEach { sort ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = sort.titleFa,
                                                fontWeight = if (sort == selectedSort) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (sort == selectedSort) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            viewModel.onSortSelect(sort)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Calculator Button (محاسبه‌گر دارویی و فوریت پزشکی)
                        IconButton(
                            onClick = { showCalculatorDialog = true },
                            modifier = Modifier.size(36.dp).testTag("top_app_bar_calculator_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "محاسبه‌گر دارویی و فوریت",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Settings Button (شامل مدیریت و آلارم و تاریخچه)
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier.size(36.dp).testTag("top_app_bar_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "تنظیمات و مدیریت",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // About Us Button
                        IconButton(
                            onClick = { showAboutDialog = true },
                            modifier = Modifier.size(36.dp).testTag("top_app_bar_about_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "درباره ما",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.openAddDialog(null) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("ثبت ورودی جدید", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_item_fab")
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // 1. Admin Announcement Banner (if any active admin message exists)
                if (latestUnreadAdminMessage != null) {
                    val adminMsg = latestUnreadAdminMessage!!
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when (adminMsg.priority) {
                                "CRITICAL" -> ExpiryExpiredContainer
                                "HIGH" -> ExpiryWarningContainer
                                else -> MedBlueContainer
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = when (adminMsg.priority) {
                                        "CRITICAL" -> ExpiryExpiredRed
                                        "HIGH" -> ExpiryWarningOrange
                                        else -> MedBlueSecondary
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "پیام مدیریت: ${adminMsg.title}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = adminMsg.content,
                                        fontSize = 10.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.markAdminMessageAsRead(adminMsg.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "بستن",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Compact Search Bar & Quick Inspection Strip
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("جستجوی نام دارو، جامبگ، کمد...", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "جستجو",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.onSearchQueryChange("") },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "پاک کردن",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("search_text_field"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            singleLine = true
                        )

                        // Compact Inspection Action Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .height(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.performFullInventoryInspection() }
                                .testTag("full_inspection_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FactCheck,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "چک خودکار",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // 3. Compact Automated Expiry Warning Banner (If any item is expiring soon / expired)
                if (stats.expiringSoonCount > 0 || stats.expiredCount > 0) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (stats.expiredCount > 0) ExpiryExpiredContainer else ExpiryWarningContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.onFilterSelect(
                                        if (stats.expiredCount > 0) InventoryFilter.EXPIRED_ONLY else InventoryFilter.EXPIRING_SOON_ONLY
                                    )
                                }
                                .testTag("warning_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = if (stats.expiredCount > 0) ExpiryExpiredRed else ExpiryWarningOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (stats.expiredCount > 0) {
                                        "⚠️ ${DateUtils.toPersianDigits(stats.expiredCount.toString())} مورد منقضی شده | لمس برای فیلتر"
                                    } else {
                                        "⏰ ${DateUtils.toPersianDigits(stats.expiringSoonCount.toString())} مورد در آستانه انقضا (<۱ ماه)"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (stats.expiredCount > 0) ExpiryExpiredRed else ExpiryWarningOrange,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // 4. Compact Stats Overview with Section Breakdown (جامبگ / کمد)
                item {
                    StatsOverview(
                        stats = stats,
                        onFilterClick = { viewModel.onFilterSelect(it) }
                    )
                }

                // 5. Compact Filter Chips Row
                item {
                    FilterChipsRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { viewModel.onFilterSelect(it) }
                    )
                }

                // 6. Items Count & Current Sort Status
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اقلام: ${DateUtils.toPersianDigits(items.size.toString())} مورد",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "مرتب‌سازی: ${selectedSort.titleFa}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // 7. Empty State
                if (items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp, horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Medication,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) "موردی با عنوان «$searchQuery» یافت نشد" else "هیچ دارویی یا تجهیزاتی ثبت نشده است",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) "کلمه دیگری را جستجو کنید." else "با دکمه «ثبت ورودی جدید» داروها و تجهیزات را ثبت کنید.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.openAddDialog(null) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("empty_state_add_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ثبت اولین قلم ورودی", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    // 8. Medical Items List
                    items(items, key = { it.id }) { item ->
                        MedicalItemCard(
                            item = item,
                            onEditClick = { viewModel.openAddDialog(it) },
                            onDeleteClick = { itemToDelete = it },
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Add / Edit Item Dialog
        if (isAddEditOpen) {
            AddEditItemDialog(
                itemToEdit = editingItem,
                onSave = { viewModel.saveMedicalItem(it) },
                onDismiss = { viewModel.closeAddDialog() }
            )
        }

        // Inspection Report Dialog
        if (showInspectionDialog && lastInspectionResult != null) {
            InspectionReportDialog(
                record = lastInspectionResult!!,
                onDismiss = { viewModel.dismissInspectionDialog() }
            )
        }

        // Delete Confirmation Dialog
        itemToDelete?.let { item ->
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("حذف قلم ورودی", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                text = {
                    Text("آیا از حذف «${item.name}» مطمئن هستید؟", fontSize = 12.sp)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteMedicalItem(item)
                            itemToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_delete_button")
                    ) {
                        Text("حذف شود", fontSize = 12.sp)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { itemToDelete = null },
                        modifier = Modifier.testTag("cancel_delete_button")
                    ) {
                        Text("انصراف", fontSize = 12.sp)
                    }
                }
            )
        }

        // Settings Dialog (Sound selection, Dark/Light Mode, Inspection History, Admin Management, Version & Update)
        if (showSettingsDialog) {
            SettingsDialog(
                currentThemeMode = currentThemeMode,
                onThemeModeChange = { viewModel.setThemeMode(it) },
                currentSoundType = currentSoundType,
                onSoundTypeChange = { viewModel.setAlarmSoundType(it) },
                inspectionHistory = inspectionHistory,
                onClearHistory = { viewModel.clearAllInspectionHistory() },
                onDeleteHistoryItem = { viewModel.deleteInspectionRecord(it) },
                onPerformInspectionNow = {
                    showSettingsDialog = false
                    viewModel.performFullInventoryInspection()
                },
                isAdminAuthenticated = isAdminAuthenticated,
                onVerifyAdminPassword = { viewModel.verifyAdminPassword(it) },
                onLogoutAdmin = { viewModel.logoutAdmin() },
                adminMessages = adminMessages,
                onBroadcastAdminMessage = { title, content, priority, target ->
                    viewModel.broadcastAdminMessage(title, content, priority, target)
                },
                onDeleteAdminMessage = { viewModel.deleteAdminMessage(it) },
                onClearAllAdminMessages = { viewModel.clearAllAdminMessages() },
                appUpdateInfo = appUpdateInfo,
                updateCheckState = updateCheckState,
                onCheckForUpdates = { viewModel.checkForUpdates(showNotificationIfNoUpdate = true) },
                onPublishNewVersion = { name, code, notes, mandatory ->
                    viewModel.publishNewVersionRelease(name, code, notes, mandatory)
                },
                onDismiss = { showSettingsDialog = false }
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AboutDialog(
                currentVersionName = appUpdateInfo.currentVersionName,
                onCheckForUpdates = { viewModel.checkForUpdates(showNotificationIfNoUpdate = true) },
                onDismiss = { showAboutDialog = false }
            )
        }

        // Medical Calculator Dialog
        if (showCalculatorDialog) {
            MedicalCalculatorDialog(
                onDismiss = { showCalculatorDialog = false }
            )
        }

        // In-App Update Dialog Prompt
        if (showInAppUpdatePrompt) {
            InAppUpdateDialog(
                updateInfo = appUpdateInfo,
                updateState = updateCheckState,
                downloadProgress = updateDownloadProgress,
                onStartUpdate = { viewModel.startInAppUpdate() },
                onOpenDownloadLink = { url -> viewModel.openUpdateDownloadLink(url) },
                onDismiss = { viewModel.dismissUpdatePrompt() }
            )
        }
    }
}
