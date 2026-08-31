package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminMessage
import com.example.ui.theme.ExpiryExpiredContainer
import com.example.ui.theme.ExpiryExpiredRed
import com.example.ui.theme.ExpiryWarningContainer
import com.example.ui.theme.ExpiryWarningOrange
import com.example.ui.theme.MedBlueContainer
import com.example.ui.theme.MedBlueSecondary
import com.example.ui.theme.MedTealContainer
import com.example.ui.theme.MedTealPrimary
import com.example.ui.util.DateUtils

@Composable
fun AdminManagementSection(
    isAuthenticated: Boolean,
    onVerifyPassword: (String) -> Boolean,
    onLogout: () -> Unit,
    adminMessages: List<AdminMessage>,
    onBroadcastMessage: (title: String, content: String, priority: String, targetCategory: String) -> Unit,
    onDeleteMessage: (Int) -> Unit,
    onClearAllMessages: () -> Unit,
    onPublishNewVersion: (versionName: String, versionCode: Int, releaseNotes: String, isMandatory: Boolean) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    if (!isAuthenticated) {
        AdminPasswordLockScreen(
            onVerifyPassword = onVerifyPassword,
            modifier = modifier
        )
    } else {
        AdminControlPanel(
            onLogout = onLogout,
            adminMessages = adminMessages,
            onBroadcastMessage = onBroadcastMessage,
            onDeleteMessage = onDeleteMessage,
            onClearAllMessages = onClearAllMessages,
            onPublishNewVersion = onPublishNewVersion,
            modifier = modifier
        )
    }
}

@Composable
private fun AdminPasswordLockScreen(
    onVerifyPassword: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val submitPassword = {
        if (passwordInput.isBlank()) {
            errorMessage = "لطفاً رمز عبور مدیریت را وارد نمایید."
        } else {
            val isSuccess = onVerifyPassword(passwordInput)
            if (!isSuccess) {
                errorMessage = "رمز عبور اشتباه است. لطفاً مجدداً بررسی فرمایید."
            } else {
                errorMessage = null
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ورود به بخش مدیریت و ارسال پیام",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "این بخش مخصوص مدیر سیستم و ارسال اطلاعیه و نوتیفیکیشن به تمام کاربران است.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = {
                passwordInput = it
                errorMessage = null
            },
            label = { Text("رمز عبور مدیریت", fontSize = 12.sp) },
            placeholder = { Text("رمز را وارد کنید", fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "نمایش رمز",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    submitPassword()
                }
            ),
            isError = errorMessage != null,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_password_input")
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage ?: "",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = submitPassword,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("admin_login_button")
        ) {
            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("ورود به پنل مدیریت", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AdminControlPanel(
    onLogout: () -> Unit,
    adminMessages: List<AdminMessage>,
    onBroadcastMessage: (title: String, content: String, priority: String, targetCategory: String) -> Unit,
    onDeleteMessage: (Int) -> Unit,
    onClearAllMessages: () -> Unit,
    onPublishNewVersion: (versionName: String, versionCode: Int, releaseNotes: String, isMandatory: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("HIGH") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showSuccessBanner by remember { mutableStateOf(false) }

    // Version release form state
    var showVersionPublishForm by remember { mutableStateOf(false) }
    var newVersionNameInput by remember { mutableStateOf("2.3.0") }
    var newVersionCodeInput by remember { mutableStateOf("23") }
    var newReleaseNotesInput by remember { mutableStateOf("• بهینه‌سازی سرعت بارگذاری و همگام‌سازی\n• ارتقای رابط کاربری و عملکرد آلارم‌ها") }
    var isMandatoryUpdate by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val priorities = listOf(
        Triple("CRITICAL", "🚨 بحرانی / فوری", ExpiryExpiredRed),
        Triple("HIGH", "⚠️ هشدار دارویی", ExpiryWarningOrange),
        Triple("NORMAL", "ℹ️ اطلاعیه عادی", MedBlueSecondary)
    )

    val categories = listOf(
        Pair("ALL", "🌐 تمام بخش‌ها"),
        Pair("JUMPBAG_MEDICINES", "🎒 داروی جامبگ"),
        Pair("CABINET_MEDICINES", "💊 کمد دارویی"),
        Pair("EQUIPMENT", "🩺 تجهیزات پزشکی")
    )

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Admin Status Header
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MedTealContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = MedTealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "احراز هویت شده (مدیر سیستم)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedTealPrimary
                        )
                    }

                    TextButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("خروج", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Version Release Card (for live in-app update broadcast)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "انتشار و اعلان آپدیت نسخه جدید",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        TextButton(onClick = { showVersionPublishForm = !showVersionPublishForm }) {
                            Text(if (showVersionPublishForm) "بستن فرم" else "باز کردن فرم", fontSize = 11.sp)
                        }
                    }

                    if (showVersionPublishForm) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newVersionNameInput,
                                onValueChange = { newVersionNameInput = it },
                                label = { Text("نام نسخه جدید (مثال: 2.3.0)", fontSize = 10.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.3f)
                            )

                            OutlinedTextField(
                                value = newVersionCodeInput,
                                onValueChange = { newVersionCodeInput = it },
                                label = { Text("کد نسخه (23)", fontSize = 10.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newReleaseNotesInput,
                            onValueChange = { newReleaseNotesInput = it },
                            label = { Text("تغییرات و امکانات نسخه جدید (هر خط یک مورد)", fontSize = 10.sp) },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isMandatoryUpdate = !isMandatoryUpdate }
                        ) {
                            Checkbox(
                                checked = isMandatoryUpdate,
                                onCheckedChange = { isMandatoryUpdate = it }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "به‌روزرسانی اجباری است",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val code = newVersionCodeInput.toIntOrNull() ?: 23
                                onPublishNewVersion(
                                    newVersionNameInput,
                                    code,
                                    newReleaseNotesInput,
                                    isMandatoryUpdate
                                )
                                showVersionPublishForm = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("انتشار نسخه و ارسال اعلان فوری به کاربران", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "با انتشار نسخه جدید، تمام کلاینت‌ها اعلان آپدیت فوری با قابلیت اعمال بدون حذف برنامه دریافت می‌کنند.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Broadcast Compose Form
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ارسال اطلاعیه و نوتیفیکیشن همگانی",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان پیام (مثال: اطلاعیه تعویض داروها)", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_message_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Priority Selector
                    Text("سطح فوریت و هشدار آلارم:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        priorities.forEach { (key, label, color) ->
                            val isSelected = selectedPriority == key
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) color.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, color) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedPriority = key }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Content
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("متن کامل پیام / دستورالعمل", fontSize = 11.sp) },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_message_content_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                focusManager.clearFocus()
                                onBroadcastMessage(title, content, selectedPriority, selectedCategory)
                                title = ""
                                content = ""
                                showSuccessBanner = true
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("broadcast_submit_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ارسال پیام و پخش نوتیفیکیشن", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sent Messages History Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "پیام‌های ارسال شده (${DateUtils.toPersianDigits(adminMessages.size.toString())}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (adminMessages.isNotEmpty()) {
                    TextButton(onClick = onClearAllMessages) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("پاکسازی همه", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        if (adminMessages.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "هنوز پیامی ارسال نشده است.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(adminMessages, key = { it.id }) { msg ->
                val badgeColor = when (msg.priority) {
                    "CRITICAL" -> ExpiryExpiredRed
                    "HIGH" -> ExpiryWarningOrange
                    else -> MedBlueSecondary
                }

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = msg.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = DateUtils.formatShamsiDate(msg.timestamp),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                IconButton(
                                    onClick = { onDeleteMessage(msg.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "حذف پیام",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = msg.content,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
