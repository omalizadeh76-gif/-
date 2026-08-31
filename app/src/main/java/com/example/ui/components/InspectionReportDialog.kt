package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InspectionRecord
import com.example.ui.theme.ExpiryExpiredContainer
import com.example.ui.theme.ExpiryExpiredRed
import com.example.ui.theme.ExpirySafeContainer
import com.example.ui.theme.ExpirySafeGreen
import com.example.ui.theme.ExpiryWarningContainer
import com.example.ui.theme.ExpiryWarningOrange
import com.example.ui.theme.MedBlueContainer
import com.example.ui.theme.MedBlueSecondary
import com.example.ui.theme.MedTealContainer
import com.example.ui.theme.MedTealPrimary
import com.example.ui.util.DateUtils

@Composable
fun InspectionReportDialog(
    record: InspectionRecord,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("inspection_report_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FactCheck,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "نتیجه بررسی خودکار داروها و تجهیزات",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ثبت شده در تاریخچه سیستم",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date and Time Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "زمان دقیق ثبت بررسی:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = DateUtils.formatShamsiDateTime(record.timestamp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "${DateUtils.toPersianDigits(record.totalChecked.toString())} قلم بررسی شد",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Message Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        record.expiredCount > 0 -> ExpiryExpiredContainer
                        record.expiringSoonCount > 0 -> ExpiryWarningContainer
                        else -> ExpirySafeContainer
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                record.expiredCount > 0 -> Icons.Default.ReportProblem
                                record.expiringSoonCount > 0 -> Icons.Default.NotificationsActive
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = when {
                                record.expiredCount > 0 -> ExpiryExpiredRed
                                record.expiringSoonCount > 0 -> ExpiryWarningOrange
                                else -> ExpirySafeGreen
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = record.statusNote,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                record.expiredCount > 0 -> ExpiryExpiredRed
                                record.expiringSoonCount > 0 -> ExpiryWarningOrange
                                else -> ExpirySafeGreen
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown Matrix Grid
                Text(
                    text = "آمار دسته‌بندی و وضعیت ورودی‌ها:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Expiry Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniStatBadge(
                        label = "منقضی شده 🚨",
                        count = record.expiredCount,
                        color = ExpiryExpiredRed,
                        container = ExpiryExpiredContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatBadge(
                        label = "در آستانه انقضا ⚠️",
                        count = record.expiringSoonCount,
                        color = ExpiryWarningOrange,
                        container = ExpiryWarningContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatBadge(
                        label = "دارای اعتبار ✅",
                        count = record.safeCount,
                        color = ExpirySafeGreen,
                        container = ExpirySafeContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sections Stats Row (JumpBag vs Cabinets)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🎒 داروی جامبگ: ${DateUtils.toPersianDigits(record.jumpBagMedicineCount.toString())} مورد", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("🚪 کمد دارویی: ${DateUtils.toPersianDigits(record.medicineCabinetCount.toString())} مورد", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🎒 تجهیزات جامبگ: ${DateUtils.toPersianDigits(record.jumpBagEquipmentCount.toString())} مورد", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("🚪 کمد تجهیزات: ${DateUtils.toPersianDigits(record.equipmentCabinetCount.toString())} مورد", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                if (record.detailsSummary.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "جزئیات اقلام نیازمند توجه:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = record.detailsSummary,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_inspection_dialog_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("متوجه شدم و تایید می‌کنم", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MiniStatBadge(
    label: String,
    count: Int,
    color: Color,
    container: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = container,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = DateUtils.toPersianDigits(count.toString()),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
