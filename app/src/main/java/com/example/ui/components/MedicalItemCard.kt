package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpiryStatus
import com.example.data.model.ItemCategory
import com.example.data.model.MedicalItem
import com.example.ui.theme.ExpiryExpiredContainer
import com.example.ui.theme.ExpiryExpiredOnContainer
import com.example.ui.theme.ExpiryExpiredRed
import com.example.ui.theme.ExpirySafeContainer
import com.example.ui.theme.ExpirySafeGreen
import com.example.ui.theme.ExpirySafeOnContainer
import com.example.ui.theme.ExpiryWarningContainer
import com.example.ui.theme.ExpiryWarningOnContainer
import com.example.ui.theme.ExpiryWarningOrange
import com.example.ui.theme.MedBlueContainer
import com.example.ui.theme.MedBlueSecondary
import com.example.ui.theme.MedOnBlueContainer
import com.example.ui.theme.MedTealContainer
import com.example.ui.theme.MedTealPrimary
import com.example.ui.util.DateUtils

@Composable
fun MedicalItemCard(
    item: MedicalItem,
    onEditClick: (MedicalItem) -> Unit,
    onDeleteClick: (MedicalItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val expiryStatus = item.getExpiryStatus()
    val remainingText = DateUtils.getReadableRemainingTime(item)
    var isExpanded by remember { mutableStateOf(false) }

    val (badgeBg, badgeText, badgeBorder) = when (expiryStatus) {
        ExpiryStatus.EXPIRED -> Triple(ExpiryExpiredContainer, ExpiryExpiredOnContainer, ExpiryExpiredRed)
        ExpiryStatus.EXPIRING_SOON -> Triple(ExpiryWarningContainer, ExpiryWarningOnContainer, ExpiryWarningOrange)
        ExpiryStatus.SAFE -> Triple(ExpirySafeContainer, ExpirySafeOnContainer, ExpirySafeGreen)
        ExpiryStatus.NO_EXPIRY -> Triple(MedBlueContainer, MedOnBlueContainer, MedBlueSecondary)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded }
            .testTag("medical_item_card_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Header Row: Compact Icon, Title & Meta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Compact Category Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (item.category == ItemCategory.MEDICINE) MedTealContainer else MedBlueContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.category == ItemCategory.MEDICINE) Icons.Default.Medication else Icons.Default.MedicalServices,
                        contentDescription = item.category.name,
                        tint = if (item.category == ItemCategory.MEDICINE) MedTealPrimary else MedBlueSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Title & Location/Quantity
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = if (isExpanded) 3 else 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (item.category == ItemCategory.MEDICINE) "دارو" else "تجهیزات",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "موجودی: ${DateUtils.toPersianDigits(item.quantity.toString())} ${item.unit}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (item.location.isNotBlank()) {
                            Text(
                                text = "•  ${item.location}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "جزئیات",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Compact Expiry Status Strip (Countdown & Dual Dates)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(badgeBorder)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = remainingText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = badgeText
                        )
                    }

                    if (item.hasExpiryDate) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${DateUtils.formatDate(item.expiryTimestamp)} م",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                color = badgeText.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "${DateUtils.formatShamsiDate(item.expiryTimestamp)} ش",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeText
                            )
                        }
                    }
                }
            }

            // Expanded Details Section
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))

                    Spacer(modifier = Modifier.height(6.dp))

                    if (item.hasExpiryDate) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تاریخ انقضا: ${DateUtils.formatDate(item.expiryTimestamp)} میلادی | ${DateUtils.formatShamsiDate(item.expiryTimestamp)} شمسی",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (item.location.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "محل نگهداری: ${item.location}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (item.hasExpiryDate) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = ExpiryWarningOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "زمان یادآوری: ${DateUtils.toPersianDigits(item.reminderDaysBefore.toString())} روز قبل از انقضا (~۱ ماه)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (item.notes.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "توضیحات: ${item.notes}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { onEditClick(item) },
                            modifier = Modifier.size(30.dp).testTag("edit_button_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "ویرایش",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteClick(item) },
                            modifier = Modifier.size(30.dp).testTag("delete_button_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
