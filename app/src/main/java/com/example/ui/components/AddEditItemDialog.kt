package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ItemCategory
import com.example.data.model.MedicalItem
import com.example.ui.theme.ExpiryWarningOrange
import com.example.ui.theme.MedBlueSecondary
import com.example.ui.theme.MedTealPrimary
import com.example.ui.util.DateUtils

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemDialog(
    itemToEdit: MedicalItem?,
    onSave: (MedicalItem) -> Unit,
    onDismiss: () -> Unit
) {
    val isEditing = itemToEdit != null

    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var category by remember { mutableStateOf(itemToEdit?.category ?: ItemCategory.MEDICINE) }
    var hasExpiryDate by remember { mutableStateOf(itemToEdit?.hasExpiryDate ?: true) }
    var expiryTimestamp by remember {
        mutableLongStateOf(
            itemToEdit?.expiryTimestamp ?: DateUtils.getPresetTimestamp(30)
        )
    }
    var quantity by remember { mutableIntStateOf(itemToEdit?.quantity ?: 1) }
    var unit by remember { mutableStateOf(itemToEdit?.unit ?: "بسته") }
    var location by remember { mutableStateOf(itemToEdit?.location ?: "جامبگ") }
    var reminderDaysBefore by remember { mutableIntStateOf(itemToEdit?.reminderDaysBefore ?: 30) }
    var notes by remember { mutableStateOf(itemToEdit?.notes ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val commonUnits = listOf("بسته", "ورق", "جعبه", "عدد", "شیشه", "ویال", "تیوب", "اسپری", "دستگاه", "آمپول", "سرم")

    // Dedicated sections based on user request:
    val medicineLocations = listOf("جامبگ (کیف امداد)", "کمد دارویی", "یخچال داروها", "قفسه داروها")
    val equipmentLocations = listOf("جامبگ (کیف امداد)", "کمد تجهیزات", "کیف احیا", "قفسه تجهیزات")

    fun triggerSave() {
        if (name.isBlank()) {
            nameError = true
            return
        }
        val finalLocation = if (location.isBlank()) {
            if (category == ItemCategory.MEDICINE) "کمد دارویی" else "کمد تجهیزات"
        } else {
            location.trim()
        }

        val item = MedicalItem(
            id = itemToEdit?.id ?: 0,
            name = name.trim(),
            category = category,
            hasExpiryDate = hasExpiryDate,
            expiryTimestamp = if (hasExpiryDate) expiryTimestamp else 0L,
            quantity = quantity.coerceAtLeast(1),
            unit = unit.trim().ifEmpty { "بسته" },
            location = finalLocation,
            notes = notes.trim(),
            reminderDaysBefore = reminderDaysBefore,
            createdAt = itemToEdit?.createdAt ?: System.currentTimeMillis()
        )
        onSave(item)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("add_edit_item_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header with Title and "تایید و ثبت" button at the very top
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "انصراف",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = if (isEditing) "ویرایش اطلاعات" else "ثبت ورودی جدید",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { triggerSave() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("dialog_top_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEditing) "ذخیره" else "تایید و ثبت",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Category Selection: دارو vs تجهیزات
                    Text(
                        text = "نوع قلم ورودی:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategorySelectionCard(
                            title = "دارو",
                            subtitle = "قرص، آمپول، سرم، شربت",
                            icon = Icons.Default.Medication,
                            isSelected = category == ItemCategory.MEDICINE,
                            accentColor = MedTealPrimary,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                category = ItemCategory.MEDICINE
                                if (location.contains("تجهیزات")) {
                                    location = "جامبگ"
                                }
                            }
                        )

                        CategorySelectionCard(
                            title = "تجهیزات",
                            subtitle = "ست پانسمان، ماسک، لوله",
                            icon = Icons.Default.MedicalServices,
                            isSelected = category == ItemCategory.EQUIPMENT,
                            accentColor = MedBlueSecondary,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                category = ItemCategory.EQUIPMENT
                                if (location.contains("دارو")) {
                                    location = "جامبگ"
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Item Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (it.isNotBlank()) nameError = false
                        },
                        label = { Text(if (category == ItemCategory.MEDICINE) "نام دارو *" else "نام تجهیزات پزشکی *") },
                        placeholder = { Text(if (category == ItemCategory.MEDICINE) "مثال: آمپول آتروپین، قرص نیتروگلیسیرین" else "مثال: ست پانسمان، آنژیوکت آبی، ماسک") },
                        isError = nameError,
                        supportingText = {
                            if (nameError) Text("لطفاً نام را وارد کنید", color = MaterialTheme.colorScheme.error)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_name_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location / Section selection (جامبگ vs کمد)
                    Text(
                        text = if (category == ItemCategory.MEDICINE) "محل نگهداری دارو (بخش):" else "محل نگهداری تجهیزات (بخش):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isJumpBagSelected = location.contains("جامبگ", ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isJumpBagSelected) 2.dp else 1.dp,
                                    color = if (isJumpBagSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { location = "جامبگ" }
                                .padding(10.dp),
                            color = if (isJumpBagSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎒 جامبگ (کیف امداد)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("دسترسی سریع عملیاتی", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        val isCabinetSelected = !isJumpBagSelected
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isCabinetSelected) 2.dp else 1.dp,
                                    color = if (isCabinetSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    location = if (category == ItemCategory.MEDICINE) "کمد دارویی" else "کمد تجهیزات"
                                }
                                .padding(10.dp),
                            color = if (isCabinetSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (category == ItemCategory.MEDICINE) "🚪 کمد دارویی" else "🚪 کمد تجهیزات", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("انبار و نگهداری اصلی", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location text field for custom refinement
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("نام دقیق محل قرارگیری") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_location_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expiry Date Section
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "دارای تاریخ انقضا",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (hasExpiryDate) "هشدار انقضا فعال است" else "قلم مصرفی بدون تاریخ انقضا",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Switch(
                                    checked = hasExpiryDate,
                                    onCheckedChange = { hasExpiryDate = it },
                                    modifier = Modifier.testTag("has_expiry_switch")
                                )
                            }

                            if (hasExpiryDate) {
                                Spacer(modifier = Modifier.height(10.dp))

                                // Date Card showing both Gregorian and Shamsi Dates clearly
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { showDatePicker = true }
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp)
                                        .testTag("date_picker_trigger"),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarMonth,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "میلادی: ${DateUtils.formatDate(expiryTimestamp)}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "شمسی: ${DateUtils.formatShamsiDate(expiryTimestamp)}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "تغییر تاریخ",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Preset quick date buttons
                                Text(
                                    text = "انتخاب سریع بازه زمانی انقضا:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "۱۵ روز دیگر" to 15,
                                        "۱ ماه دیگر" to 30,
                                        "۳ ماه دیگر" to 90,
                                        "۶ ماه دیگر" to 180,
                                        "۱ سال دیگر" to 365,
                                        "۲ سال دیگر" to 730
                                    ).forEach { (label, days) ->
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { expiryTimestamp = DateUtils.getPresetTimestamp(days) },
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Reminder days setting
                                Text(
                                    text = "ارسال آلارم هشدار:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "۷ روز مانده" to 7,
                                        "۱۵ روز مانده" to 15,
                                        "۳۰ روز (۱ ماه)" to 30,
                                        "۶۰ روز (۲ ماه)" to 60
                                    ).forEach { (label, days) ->
                                        val isSelected = reminderDaysBefore == days
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { reminderDaysBefore = days },
                                            label = { Text(label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = ExpiryWarningOrange.copy(alpha = 0.2f),
                                                selectedLabelColor = ExpiryWarningOrange
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quantity and Unit Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quantity Stepper
                        Surface(
                            modifier = Modifier
                                .weight(1.2f)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("تعداد / موجودی", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "کاهش", tint = MaterialTheme.colorScheme.primary)
                                    }

                                    Text(
                                        text = DateUtils.toPersianDigits(quantity.toString()),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    IconButton(
                                        onClick = { quantity++ },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "افزایش", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        // Unit input
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("واحد") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("item_unit_input"),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Quick Units chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        commonUnits.forEach { u ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { unit = u },
                                color = if (unit == u) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = u,
                                    fontSize = 11.sp,
                                    color = if (unit == u) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notes / Description
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("توضیحات و نکات تکمیلی (اختیاری)") },
                        placeholder = { Text("دوز مصرفی، شرایط نگهداری، نکات اضطراری...") },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_notes_input"),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("انصراف")
                    }

                    Button(
                        onClick = { triggerSave() },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("dialog_save_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEditing) "ذخیره تغییرات" else "تایید و ثبت نهایی",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Material Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (expiryTimestamp > 0L) expiryTimestamp else System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selected ->
                            expiryTimestamp = selected
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("تایید تاریخ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("انصراف")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun CategorySelectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) accentColor else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
