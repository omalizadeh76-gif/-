package com.example.ui.components

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AdminMessage
import com.example.data.model.AlarmSoundType
import com.example.data.model.AppThemeMode
import com.example.data.model.AppUpdateInfo
import com.example.data.model.InspectionRecord
import com.example.data.model.UpdateCheckState
import com.example.notification.NotificationHelper
import com.example.ui.theme.ExpiryExpiredContainer
import com.example.ui.theme.ExpiryExpiredRed
import com.example.ui.theme.ExpirySafeContainer
import com.example.ui.theme.ExpirySafeGreen
import com.example.ui.theme.ExpiryWarningContainer
import com.example.ui.theme.ExpiryWarningOrange
import com.example.ui.theme.MedEmeraldSafe
import com.example.ui.util.DateUtils

enum class SettingsSubScreen {
    MAIN_MENU,
    ALARM_SOUND,
    THEME_MODE,
    APP_UPDATE,
    INSPECTION_HISTORY,
    ADMIN_PANEL
}

@Composable
fun SettingsDialog(
    currentThemeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    currentSoundType: AlarmSoundType,
    onSoundTypeChange: (AlarmSoundType) -> Unit,
    inspectionHistory: List<InspectionRecord>,
    onClearHistory: () -> Unit,
    onDeleteHistoryItem: (Int) -> Unit,
    onPerformInspectionNow: () -> Unit,
    isAdminAuthenticated: Boolean,
    onVerifyAdminPassword: (String) -> Boolean,
    onLogoutAdmin: () -> Unit,
    adminMessages: List<AdminMessage>,
    onBroadcastAdminMessage: (title: String, content: String, priority: String, targetCategory: String) -> Unit,
    onDeleteAdminMessage: (Int) -> Unit,
    onClearAllAdminMessages: () -> Unit,
    appUpdateInfo: AppUpdateInfo = AppUpdateInfo(),
    updateCheckState: UpdateCheckState = UpdateCheckState.IDLE,
    onCheckForUpdates: () -> Unit = {},
    onPublishNewVersion: (versionName: String, versionCode: Int, releaseNotes: String, isMandatory: Boolean) -> Unit = { _, _, _, _ -> },
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isPlayingSound by remember { mutableStateOf(false) }
    var currentSubScreen by remember { mutableStateOf(SettingsSubScreen.MAIN_MENU) }

    DisposableEffect(Unit) {
        onDispose {
            NotificationHelper.stopPreviewSound()
        }
    }

    Dialog(
        onDismissRequest = {
            NotificationHelper.stopPreviewSound()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(640.dp)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("settings_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with Back button if in sub-screen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentSubScreen != SettingsSubScreen.MAIN_MENU) {
                            IconButton(
                                onClick = {
                                    NotificationHelper.stopPreviewSound()
                                    isPlayingSound = false
                                    currentSubScreen = SettingsSubScreen.MAIN_MENU
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("settings_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "بازگشت",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Column {
                            Text(
                                text = when (currentSubScreen) {
                                    SettingsSubScreen.MAIN_MENU -> "تنظیمات برنامه"
                                    SettingsSubScreen.ALARM_SOUND -> "صدای آلارم و هشدار انقضا"
                                    SettingsSubScreen.THEME_MODE -> "پوسته و ظاهر برنامه"
                                    SettingsSubScreen.APP_UPDATE -> "نسخه و آپدیت درون‌برنامه‌ای"
                                    SettingsSubScreen.INSPECTION_HISTORY -> "تاریخچه چک داروها"
                                    SettingsSubScreen.ADMIN_PANEL -> "پنل اختصاصی مدیریت"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (currentSubScreen) {
                                    SettingsSubScreen.MAIN_MENU -> "بخش مورد نظر خود را انتخاب نمایید"
                                    SettingsSubScreen.ALARM_SOUND -> "انتخاب و پخش آزمایشی نوتیفیکیشن"
                                    SettingsSubScreen.THEME_MODE -> "حالت روز، شب یا هماهنگ با سیستم"
                                    SettingsSubScreen.APP_UPDATE -> "بررسی نسخه جدید و ارتقای بدون حذف"
                                    SettingsSubScreen.INSPECTION_HISTORY -> "${DateUtils.toPersianDigits(inspectionHistory.size.toString())} رکورد ثبت شده"
                                    SettingsSubScreen.ADMIN_PANEL -> "ارسال اطلاعیه و انتشار نسخه جدید"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            NotificationHelper.stopPreviewSound()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("settings_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content Animated Switcher
                AnimatedContent(
                    targetState = currentSubScreen,
                    transitionSpec = {
                        if (targetState != SettingsSubScreen.MAIN_MENU) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    label = "SettingsScreenTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        SettingsSubScreen.MAIN_MENU -> {
                            // Clean single menu items stacked below each other
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // 1. Alarm Sound Tile
                                item {
                                    SettingsMenuTile(
                                        title = "صدای آلارم و هشدارها",
                                        subtitle = "صدای فعلی: ${currentSoundType.titleFa}",
                                        icon = Icons.Default.VolumeUp,
                                        iconTint = MaterialTheme.colorScheme.primary,
                                        badgeText = currentSoundType.titleFa,
                                        onClick = { currentSubScreen = SettingsSubScreen.ALARM_SOUND }
                                    )
                                }

                                // 2. Theme Mode Tile
                                item {
                                    SettingsMenuTile(
                                        title = "حالت روز و شب (تم برنامه)",
                                        subtitle = "پوسته فعال: ${currentThemeMode.titleFa}",
                                        icon = Icons.Default.Palette,
                                        iconTint = Color(0xFF8B5CF6),
                                        badgeText = currentThemeMode.titleFa,
                                        onClick = { currentSubScreen = SettingsSubScreen.THEME_MODE }
                                    )
                                }

                                // 3. In-App Update & Version Tile
                                item {
                                    SettingsMenuTile(
                                        title = "نسخه و به‌روزرسانی برنامه",
                                        subtitle = "نسخه فعلی: ${appUpdateInfo.currentVersionName} (آپدیت بدون حذف داده)",
                                        icon = Icons.Default.SystemUpdate,
                                        iconTint = MedEmeraldSafe,
                                        badgeText = "v${appUpdateInfo.currentVersionName}",
                                        badgeColor = MedEmeraldSafe.copy(alpha = 0.16f),
                                        badgeTextColor = MedEmeraldSafe,
                                        onClick = { currentSubScreen = SettingsSubScreen.APP_UPDATE }
                                    )
                                }

                                // 4. Inspection History Tile
                                item {
                                    SettingsMenuTile(
                                        title = "تاریخچه چک و بررسی داروها",
                                        subtitle = "${DateUtils.toPersianDigits(inspectionHistory.size.toString())} سابقه بررسی خودکار ثبت شده",
                                        icon = Icons.Default.History,
                                        iconTint = Color(0xFF0284C7),
                                        badgeText = "${DateUtils.toPersianDigits(inspectionHistory.size.toString())} رکورد",
                                        onClick = { currentSubScreen = SettingsSubScreen.INSPECTION_HISTORY }
                                    )
                                }

                                // 5. Admin Panel Tile
                                item {
                                    SettingsMenuTile(
                                        title = "پنل مدیریت و ارسال اطلاعیه",
                                        subtitle = if (isAdminAuthenticated) "ورود موفق (دسترسی مدیر فعال)" else "نیازمند احراز هویت با رمز مدیریت",
                                        icon = Icons.Default.AdminPanelSettings,
                                        iconTint = Color(0xFFEA580C),
                                        badgeText = if (isAdminAuthenticated) "مدیر" else "قفل",
                                        badgeColor = if (isAdminAuthenticated) MedEmeraldSafe.copy(alpha = 0.16f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                        badgeTextColor = if (isAdminAuthenticated) MedEmeraldSafe else MaterialTheme.colorScheme.error,
                                        onClick = { currentSubScreen = SettingsSubScreen.ADMIN_PANEL }
                                    )
                                }

                                // 6. System Notification Permission Settings Tile
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    item {
                                        SettingsMenuTile(
                                            title = "تنظیمات اعلان‌های گوشی",
                                            subtitle = "مدیریت مجوزها و کانال‌های صدای اندروید",
                                            icon = Icons.Default.NotificationsActive,
                                            iconTint = Color(0xFFD97706),
                                            badgeText = "تنظیمات اندروید",
                                            onClick = {
                                                try {
                                                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // Fallback
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        SettingsSubScreen.ALARM_SOUND -> {
                            // Sub-screen: Alarm Sound Selection
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "نوع صدایی که هنگام رسیدن موعد انقضا یا آلارم روزانه پخش می‌شود:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        AlarmSoundType.entries.forEach { soundType ->
                                            val isSelected = currentSoundType == soundType
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable {
                                                        onSoundTypeChange(soundType)
                                                        NotificationHelper.stopPreviewSound()
                                                        isPlayingSound = false
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = {
                                                        onSoundTypeChange(soundType)
                                                        NotificationHelper.stopPreviewSound()
                                                        isPlayingSound = false
                                                    }
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = soundType.titleFa,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = soundType.descriptionFa,
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Preview Play Sound Button
                                        Button(
                                            onClick = {
                                                if (isPlayingSound) {
                                                    NotificationHelper.stopPreviewSound()
                                                    isPlayingSound = false
                                                } else {
                                                    NotificationHelper.playPreviewSound(context, currentSoundType)
                                                    isPlayingSound = true
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isPlayingSound) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = if (isPlayingSound) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isPlayingSound) "توقف پخش صدا" else "پخش آزمایشی صدای انتخاب شده",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        SettingsSubScreen.THEME_MODE -> {
                            // Sub-screen: Theme Mode Selection
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "انتخاب حالت ظاهری و تم رنگی نرم‌افزار:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        AppThemeMode.entries.forEach { mode ->
                                            val isSelected = currentThemeMode == mode
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable { onThemeModeChange(mode) }
                                                    .padding(horizontal = 6.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { onThemeModeChange(mode) }
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = when (mode) {
                                                        AppThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                                        AppThemeMode.LIGHT -> Icons.Default.LightMode
                                                        AppThemeMode.DARK -> Icons.Default.DarkMode
                                                    },
                                                    contentDescription = null,
                                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = mode.titleFa,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = mode.descriptionFa,
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        SettingsSubScreen.APP_UPDATE -> {
                            // Sub-screen: In-App Update
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                                    border = CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.SystemUpdate,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "وضعیت نسخه برنامه:",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MedEmeraldSafe.copy(alpha = 0.16f)
                                            ) {
                                                Text(
                                                    text = "v${appUpdateInfo.currentVersionName}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MedEmeraldSafe,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "سیستم مجهز به ارتقای زنده است. هنگام دریافت نسخه جدید، تمامی اطلاعات ثبت‌شده، جامبگ‌ها و سوابق دارویی بدون نیاز به حذف برنامه کاملاً حفظ می‌شوند.",
                                            fontSize = 11.sp,
                                            lineHeight = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Button(
                                            onClick = onCheckForUpdates,
                                            enabled = updateCheckState != UpdateCheckState.CHECKING && updateCheckState != UpdateCheckState.DOWNLOADING,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(42.dp)
                                                .testTag("settings_check_update_button")
                                        ) {
                                            if (updateCheckState == UpdateCheckState.CHECKING) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(18.dp),
                                                    strokeWidth = 2.dp,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("در حال بررسی نسخه جدید...", fontSize = 12.sp)
                                            } else {
                                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("بررسی و دریافت به‌روزرسانی اکنون", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        SettingsSubScreen.INSPECTION_HISTORY -> {
                            // Sub-screen: Inspection History
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "سوابق بررسی خودکار تاریخ انقضا:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    if (inspectionHistory.isNotEmpty()) {
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onClearHistory() }
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteSweep,
                                                    contentDescription = "پاکسازی",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("پاکسازی کل تاریخچه", fontSize = 10.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (inspectionHistory.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.FactCheck,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.outlineVariant,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "هنوز هیچ بررسی خودکاری ثبت نشده است.",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "با لمس دکمه «بررسی خودکار داروها»، اولین رکورد تاریخچه ثبت می‌شود.",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        items(inspectionHistory, key = { it.id }) { record ->
                                            HistoryRecordItem(
                                                record = record,
                                                onDelete = { onDeleteHistoryItem(record.id) }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = onPerformInspectionNow,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اجرای بررسی خودکار تمام اقلام اکنون", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        SettingsSubScreen.ADMIN_PANEL -> {
                            // Sub-screen: Admin Panel
                            AdminManagementSection(
                                isAuthenticated = isAdminAuthenticated,
                                onVerifyPassword = onVerifyAdminPassword,
                                onLogout = onLogoutAdmin,
                                adminMessages = adminMessages,
                                onBroadcastMessage = onBroadcastAdminMessage,
                                onDeleteMessage = onDeleteAdminMessage,
                                onClearAllMessages = onClearAllAdminMessages,
                                onPublishNewVersion = onPublishNewVersion,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Button
                if (currentSubScreen == SettingsSubScreen.MAIN_MENU) {
                    Button(
                        onClick = {
                            NotificationHelper.stopPreviewSound()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("بستن پنجره", fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            NotificationHelper.stopPreviewSound()
                            isPlayingSound = false
                            currentSubScreen = SettingsSubScreen.MAIN_MENU
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("بازگشت به منوی تنظیمات", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsMenuTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    badgeText: String? = null,
    badgeColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    badgeTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeColor
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun HistoryRecordItem(
    record: InspectionRecord,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📅 ${DateUtils.formatShamsiDateTime(record.timestamp)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${DateUtils.toPersianDigits(record.totalChecked.toString())} قلم چک شد",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف رکورد",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Status Note
            Text(
                text = record.statusNote,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    record.expiredCount > 0 -> ExpiryExpiredRed
                    record.expiringSoonCount > 0 -> ExpiryWarningOrange
                    else -> ExpirySafeGreen
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ExpiryExpiredContainer.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "منقضی: ${DateUtils.toPersianDigits(record.expiredCount.toString())}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpiryExpiredRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ExpiryWarningContainer.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "نزدیک انقضا: ${DateUtils.toPersianDigits(record.expiringSoonCount.toString())}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpiryWarningOrange,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ExpirySafeContainer.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "معتبر: ${DateUtils.toPersianDigits(record.safeCount.toString())}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpirySafeGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Breakdown of JumpBag and Cabinets
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "داروی جامبگ: ${DateUtils.toPersianDigits(record.jumpBagMedicineCount.toString())} | کمد دارو: ${DateUtils.toPersianDigits(record.medicineCabinetCount.toString())} | تجهیزات جامبگ: ${DateUtils.toPersianDigits(record.jumpBagEquipmentCount.toString())} | کمد تجهیزات: ${DateUtils.toPersianDigits(record.equipmentCabinetCount.toString())}",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
