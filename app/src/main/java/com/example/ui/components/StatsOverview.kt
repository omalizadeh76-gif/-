package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InventoryFilter
import com.example.data.model.InventoryStats
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
fun StatsOverview(
    stats: InventoryStats,
    onFilterClick: (InventoryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp)
    ) {
        // Section 1: Compact Horizontal Category Quick-Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // JumpBag Medicines
            CategoryMiniChip(
                title = "داروی جامبگ",
                count = stats.jumpBagMedicineCount,
                icon = Icons.Default.Work,
                containerColor = MedTealContainer.copy(alpha = 0.85f),
                contentColor = MedTealPrimary,
                testTag = "card_jumpbag_meds",
                onClick = { onFilterClick(InventoryFilter.JUMPBAG_MEDICINES) }
            )

            // Cabinet Medicines
            CategoryMiniChip(
                title = "کمد دارو",
                count = stats.cabinetMedicineCount,
                icon = Icons.Default.Medication,
                containerColor = MedTealContainer.copy(alpha = 0.5f),
                contentColor = MedTealPrimary,
                testTag = "card_cabinet_meds",
                onClick = { onFilterClick(InventoryFilter.CABINET_MEDICINES) }
            )

            // JumpBag Equipment
            CategoryMiniChip(
                title = "تجهیزات جامبگ",
                count = stats.jumpBagEquipmentCount,
                icon = Icons.Default.Work,
                containerColor = MedBlueContainer.copy(alpha = 0.85f),
                contentColor = MedBlueSecondary,
                testTag = "card_jumpbag_eq",
                onClick = { onFilterClick(InventoryFilter.JUMPBAG_EQUIPMENT) }
            )

            // Cabinet Equipment
            CategoryMiniChip(
                title = "کمد تجهیزات",
                count = stats.cabinetEquipmentCount,
                icon = Icons.Default.MedicalServices,
                containerColor = MedBlueContainer.copy(alpha = 0.5f),
                contentColor = MedBlueSecondary,
                testTag = "card_cabinet_eq",
                onClick = { onFilterClick(InventoryFilter.CABINET_EQUIPMENT) }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Section 2: Compact 3-Column Status Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Expiring Soon (< 30 days)
            CompactStatPill(
                title = "نزدیک انقضا",
                count = stats.expiringSoonCount,
                icon = Icons.Default.NotificationsActive,
                containerColor = ExpiryWarningContainer.copy(alpha = 0.8f),
                contentColor = ExpiryWarningOrange,
                testTag = "stat_card_expiring_soon",
                modifier = Modifier.weight(1f),
                onClick = { onFilterClick(InventoryFilter.EXPIRING_SOON_ONLY) }
            )

            // Expired
            CompactStatPill(
                title = "منقضی شده",
                count = stats.expiredCount,
                icon = Icons.Default.ReportProblem,
                containerColor = ExpiryExpiredContainer.copy(alpha = 0.8f),
                contentColor = ExpiryExpiredRed,
                testTag = "stat_card_expired",
                modifier = Modifier.weight(1f),
                onClick = { onFilterClick(InventoryFilter.EXPIRED_ONLY) }
            )

            // Safe / Total
            CompactStatPill(
                title = "کل موجودی",
                count = stats.totalItems,
                icon = Icons.Default.Inventory2,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                contentColor = MaterialTheme.colorScheme.primary,
                testTag = "stat_card_total",
                modifier = Modifier.weight(1f),
                onClick = { onFilterClick(InventoryFilter.ALL) }
            )
        }
    }
}

@Composable
private fun CategoryMiniChip(
    title: String,
    count: Int,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = contentColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = DateUtils.toPersianDigits(count.toString()),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun CompactStatPill(
    title: String,
    count: Int,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            Text(
                text = DateUtils.toPersianDigits(count.toString()),
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor
            )
        }
    }
}
