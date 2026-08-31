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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.MedBlueSecondary
import com.example.ui.theme.MedEmeraldSafe
import com.example.ui.util.DateUtils
import kotlin.math.roundToInt

enum class CalculatorTab(val title: String, val icon: ImageVector) {
    DOSE_MG_KG("دوز دارویی (mg/kg)", Icons.Default.Vaccines),
    INFUSION_DRIP("قطرات سرم (Drip)", Icons.Default.Opacity),
    CONTINUOUS_INFUSION("انفوزیون مداوم (mcg/kg/min)", Icons.Default.Speed),
    OXYGEN_CYLINDER("سیلندر اکسیژن (مدت زمان)", Icons.Default.Air),
    PEDIATRIC_EMERGENCY("فوریت اطفال بر اساس سن", Icons.Default.FitnessCenter)
}

data class EmergencyPediatricDrug(
    val nameFa: String,
    val nameEn: String,
    val indication: String,
    val formulaText: String,
    val calculatedDose: String,
    val ampouleConcentration: String,
    val administrationGuide: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalCalculatorDialog(
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val clipboardManager = LocalClipboardManager.current
    var copySnackbarText by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(680.dp)
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("medical_calculator_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "محاسبه‌گر دارویی و فوریت پزشکی",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "فرمول‌های ریاضی دوزاژ، قطرات سرم، انفوزیون و اکسیژن",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("calc_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    CalculatorTab.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    tint = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Contents
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (CalculatorTab.entries[selectedTab]) {
                        CalculatorTab.DOSE_MG_KG -> DosePerKgCalculatorTab()
                        CalculatorTab.INFUSION_DRIP -> SerumDripRateCalculatorTab()
                        CalculatorTab.CONTINUOUS_INFUSION -> ContinuousInfusionCalculatorTab()
                        CalculatorTab.OXYGEN_CYLINDER -> OxygenCylinderCalculatorTab()
                        CalculatorTab.PEDIATRIC_EMERGENCY -> PediatricEmergencyCalculatorTab()
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------------------------------------
// 1. Dose mg/kg Calculator
// ------------------------------------------------------------------------------------------------
@Composable
fun DosePerKgCalculatorTab() {
    var patientWeight by remember { mutableStateOf("70") }
    var targetDosePerKg by remember { mutableStateOf("0.5") }
    var vialTotalMg by remember { mutableStateOf("10") }
    var vialTotalMl by remember { mutableStateOf("2") }

    val weightNum = patientWeight.toDoubleOrNull() ?: 0.0
    val dosePerKgNum = targetDosePerKg.toDoubleOrNull() ?: 0.0
    val vialMgNum = vialTotalMg.toDoubleOrNull() ?: 0.0
    val vialMlNum = vialTotalMl.toDoubleOrNull() ?: 0.0

    // Calculations
    val totalDoseMg = weightNum * dosePerKgNum
    val requiredVolumeMl = if (vialMgNum > 0.0) {
        (totalDoseMg * vialMlNum) / vialMgNum
    } else 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FormulaCard(
                formulaTitle = "فرمول محاسبه دوز کل و حجم تزریق (ml)",
                formulaEn = "Total Dose (mg) = Weight (kg) × Dose (mg/kg)\nVolume (ml) = [Total Dose (mg) × Vial Volume (ml)] ÷ Vial Content (mg)",
                descriptionFa = "محاسبه دقیق مقدار داروی مورد نیاز بیمار و تبدیل میلی‌گرم به سی‌سی (میلی‌لیتر) جهت کشیدن در سرنگ."
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = patientWeight,
                    onValueChange = { patientWeight = it },
                    label = { Text("وزن بیمار (kg)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = targetDosePerKg,
                    onValueChange = { targetDosePerKg = it },
                    label = { Text("دوز تجویزی (mg/kg)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = vialTotalMg,
                    onValueChange = { vialTotalMg = it },
                    label = { Text("مقدار آمپول (mg)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = vialTotalMl,
                    onValueChange = { vialTotalMl = it },
                    label = { Text("حجم کل آمپول (ml)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            ResultDisplayCard(
                results = listOf(
                    CalculationResultItem(
                        title = "دوز کل مورد نیاز (mg):",
                        value = "%.2f میلی‌گرم".format(totalDoseMg),
                        highlight = true,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    CalculationResultItem(
                        title = "حجم قابل تزریق در سرنگ (ml / cc):",
                        value = "%.2f سی‌سی (ml)".format(requiredVolumeMl),
                        highlight = true,
                        color = MedEmeraldSafe
                    ),
                    CalculationResultItem(
                        title = "غلظت نهایی هر سی‌سی از ویال:",
                        value = if (vialMlNum > 0) "%.2f mg/ml".format(vialMgNum / vialMlNum) else "۰",
                        highlight = false
                    )
                )
            )
        }

        // Quick emergency presets
        item {
            Text(
                text = "⚡ پیش‌فرض‌های سریع دوزاژ شایع اورژانس:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                QuickPresetButton(title = "میدازولام (0.1 mg/kg)", modifier = Modifier.weight(1f)) {
                    targetDosePerKg = "0.1"
                    vialTotalMg = "5"
                    vialTotalMl = "1"
                }
                QuickPresetButton(title = "مورفین (0.1 mg/kg)", modifier = Modifier.weight(1f)) {
                    targetDosePerKg = "0.1"
                    vialTotalMg = "10"
                    vialTotalMl = "1"
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                QuickPresetButton(title = "کتامین (1.5 mg/kg)", modifier = Modifier.weight(1f)) {
                    targetDosePerKg = "1.5"
                    vialTotalMg = "500"
                    vialTotalMl = "10"
                }
                QuickPresetButton(title = "فنتانیل (1 mcg/kg)", modifier = Modifier.weight(1f)) {
                    targetDosePerKg = "0.001" // converted to mg
                    vialTotalMg = "0.5"
                    vialTotalMl = "10"
                }
            }
        }
    }
}

// ------------------------------------------------------------------------------------------------
// 2. Serum Drip Rate Calculator
// ------------------------------------------------------------------------------------------------
@Composable
fun SerumDripRateCalculatorTab() {
    var serumVolumeMl by remember { mutableStateOf("1000") }
    var infusionTimeHours by remember { mutableStateOf("8") }
    var infusionTimeMinutes by remember { mutableStateOf("0") }
    var dropFactor by remember { mutableStateOf(15) } // 15, 20 or 60 (microdrip)

    val volumeNum = serumVolumeMl.toDoubleOrNull() ?: 0.0
    val hoursNum = infusionTimeHours.toDoubleOrNull() ?: 0.0
    val minutesNum = infusionTimeMinutes.toDoubleOrNull() ?: 0.0

    val totalMinutes = (hoursNum * 60.0) + minutesNum
    val totalHours = if (totalMinutes > 0) totalMinutes / 60.0 else 0.0

    val dropsPerMinute = if (totalMinutes > 0.0) {
        (volumeNum * dropFactor) / totalMinutes
    } else 0.0

    val mlPerHour = if (totalHours > 0.0) {
        volumeNum / totalHours
    } else 0.0

    val secondsPerDrop = if (dropsPerMinute > 0.0) {
        60.0 / dropsPerMinute
    } else 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FormulaCard(
                formulaTitle = "فرمول تعداد قطرات در دقیقه (gtt/min)",
                formulaEn = "Drops / min = [Total Volume (ml) × Drop Factor (gtt/ml)] ÷ Total Time (minutes)",
                descriptionFa = "محاسبه سرعت انفوزیون بر اساس فاکتور قطره ست سرم (ست معمولی ۱۵ یا ۲۰، میکروست ۶۰)."
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = serumVolumeMl,
                    onValueChange = { serumVolumeMl = it },
                    label = { Text("حجم سرم (ml)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = infusionTimeHours,
                    onValueChange = { infusionTimeHours = it },
                    label = { Text("زمان (ساعت)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Text(
                text = "نوع ست تزریق (Drop Factor):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SetTypeChip(
                    title = "ست معمولی (15 gtt/ml)",
                    selected = dropFactor == 15,
                    modifier = Modifier.weight(1f)
                ) { dropFactor = 15 }

                SetTypeChip(
                    title = "ست معمولی (20 gtt/ml)",
                    selected = dropFactor == 20,
                    modifier = Modifier.weight(1f)
                ) { dropFactor = 20 }

                SetTypeChip(
                    title = "میکروست (60 gtt/ml)",
                    selected = dropFactor == 60,
                    modifier = Modifier.weight(1f)
                ) { dropFactor = 60 }
            }
        }

        item {
            ResultDisplayCard(
                results = listOf(
                    CalculationResultItem(
                        title = "تعداد قطرات در دقیقه (gtt/min):",
                        value = "${dropsPerMinute.roundToInt()} قطره در دقیقه",
                        highlight = true,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    CalculationResultItem(
                        title = "فاصله بین هر قطره:",
                        value = "هر %.1f ثانیه یک قطره".format(secondsPerDrop),
                        highlight = false
                    ),
                    CalculationResultItem(
                        title = "نرخ جریان حجمی در ساعت:",
                        value = "%.1f ml / hr (سی‌سی در ساعت)".format(mlPerHour),
                        highlight = true,
                        color = MedEmeraldSafe
                    )
                )
            )
        }
    }
}

// ------------------------------------------------------------------------------------------------
// 3. Continuous Infusion (mcg/kg/min) Calculator (Dopamine, Dobutamine, Norepinephrine)
// ------------------------------------------------------------------------------------------------
@Composable
fun ContinuousInfusionCalculatorTab() {
    var patientWeight by remember { mutableStateOf("70") }
    var targetDoseMcgKgMin by remember { mutableStateOf("5") } // e.g. Dopamine 5 mcg/kg/min
    var drugAmountMg by remember { mutableStateOf("200") } // e.g. 200 mg Dopamine in 1 vial
    var totalDilutionMl by remember { mutableStateOf("100") } // in 100 cc D5W or NS

    val weightNum = patientWeight.toDoubleOrNull() ?: 0.0
    val targetDoseNum = targetDoseMcgKgMin.toDoubleOrNull() ?: 0.0
    val drugMgNum = drugAmountMg.toDoubleOrNull() ?: 0.0
    val dilutionMlNum = totalDilutionMl.toDoubleOrNull() ?: 0.0

    // Concentration in mcg/ml = (drugMg * 1000) / dilutionMl
    val concentrationMcgPerMl = if (dilutionMlNum > 0) (drugMgNum * 1000.0) / dilutionMlNum else 0.0

    // Total required dose per minute (mcg/min) = Dose * Weight
    val totalMcgPerMin = targetDoseNum * weightNum

    // Rate in ml/hr = (Total mcg/min * 60) / Concentration (mcg/ml)
    val rateMlPerHour = if (concentrationMcgPerMl > 0) {
        (totalMcgPerMin * 60.0) / concentrationMcgPerMl
    } else 0.0

    // Drops/min on microdrip (60 gtt/ml) is identical to ml/hr
    val microdripGttPerMin = rateMlPerHour

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FormulaCard(
                formulaTitle = "فرمول انفوزیون قطره‌ای میکروگرم (mcg/kg/min)",
                formulaEn = "Rate (ml/hr) = [Dose (mcg/kg/min) × Weight (kg) × 60] ÷ Concentration (mcg/ml)",
                descriptionFa = "محاسبه سرعت پمپ انفوزیون یا میکروست برای داروهای اینوتروپ و وازوپرسور (دوپامین، دوبوتامین، نوراپی‌نفرین)."
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = patientWeight,
                    onValueChange = { patientWeight = it },
                    label = { Text("وزن بیمار (kg)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = targetDoseMcgKgMin,
                    onValueChange = { targetDoseMcgKgMin = it },
                    label = { Text("دوز (mcg/kg/min)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = drugAmountMg,
                    onValueChange = { drugAmountMg = it },
                    label = { Text("مقدار دارو در سرم (mg)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = totalDilutionMl,
                    onValueChange = { totalDilutionMl = it },
                    label = { Text("حجم سرم رقیق‌کننده (ml)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            ResultDisplayCard(
                results = listOf(
                    CalculationResultItem(
                        title = "سرعت پمپ سرم / پمپ سرنگ (ml/hr):",
                        value = "%.1f سی‌سی در ساعت".format(rateMlPerHour),
                        highlight = true,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    CalculationResultItem(
                        title = "تعداد قطرات در میکروست (gtt/min):",
                        value = "${microdripGttPerMin.roundToInt()} قطره در دقیقه با میکروست",
                        highlight = true,
                        color = MedEmeraldSafe
                    ),
                    CalculationResultItem(
                        title = "غلظت محلول تهیه شده:",
                        value = "%.1f mcg/ml (میکروگرم در سی‌سی)".format(concentrationMcgPerMl),
                        highlight = false
                    )
                )
            )
        }

        item {
            Text(
                text = "⚡ تنظیمات سریع داروهای شایع:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                QuickPresetButton(title = "دوپامین (5 mcg/kg/min)", modifier = Modifier.weight(1f)) {
                    targetDoseMcgKgMin = "5"
                    drugAmountMg = "200"
                    totalDilutionMl = "100"
                }
                QuickPresetButton(title = "دوبوتامین (5 mcg/kg/min)", modifier = Modifier.weight(1f)) {
                    targetDoseMcgKgMin = "5"
                    drugAmountMg = "250"
                    totalDilutionMl = "100"
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                QuickPresetButton(title = "نوراپی‌نفرین (0.1 mcg/kg/min)", modifier = Modifier.weight(1f)) {
                    targetDoseMcgKgMin = "0.1"
                    drugAmountMg = "4"
                    totalDilutionMl = "100"
                }
                QuickPresetButton(title = "نیتروگلیسیرین TNG (10 mcg/min)", modifier = Modifier.weight(1f)) {
                    // TNG calculation approximation for 70kg
                    targetDoseMcgKgMin = "0.14" // ~10 mcg/min for 70kg
                    drugAmountMg = "10"
                    totalDilutionMl = "100"
                }
            }
        }
    }
}

// ------------------------------------------------------------------------------------------------
// 4. Oxygen Cylinder Duration Calculator
// ------------------------------------------------------------------------------------------------
@Composable
fun OxygenCylinderCalculatorTab() {
    var cylinderPressureBar by remember { mutableStateOf("150") } // Bar (or PSI)
    var flowRateLpm by remember { mutableStateOf("6") } // Liter per min (L/min)
    var cylinderVolumeLiters by remember { mutableStateOf(10.0) } // 10L ambulance cylinder, or 2L portable

    val pressureNum = cylinderPressureBar.toDoubleOrNull() ?: 0.0
    val flowNum = flowRateLpm.toDoubleOrNull() ?: 0.0

    // Constant factor for cylinder: Water capacity in liters (e.g. 10L or 2L)
    // Usable Volume in Liters = Pressure (Bar) * Cylinder Volume (Liters)
    val safePressure = if (pressureNum > 20.0) pressureNum - 20.0 else 0.0 // 20 Bar safety margin
    val totalOxygenLiters = safePressure * cylinderVolumeLiters

    val remainingMinutes = if (flowNum > 0.0) {
        totalOxygenLiters / flowNum
    } else 0.0

    val remainingHours = (remainingMinutes / 60).toInt()
    val remainingMinsOnly = (remainingMinutes % 60).toInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FormulaCard(
                formulaTitle = "فرمول محاسبه مدت زمان اکسیژن سیلندر آمبولانس",
                formulaEn = "Duration (min) = [(Pressure (Bar) - Safe Residual 20 Bar) × Cylinder Factor (L)] ÷ Flow Rate (L/min)",
                descriptionFa = "تخمین دقیق زمان باقی‌مانده از کپسول اکسیژن آمبولانس با در نظر گرفتن ۲۰ بار حاشیه امن."
            )
        }

        item {
            Text(
                text = "نوع و ظرفیت کپسول اکسیژن:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SetTypeChip(
                    title = "کپسول بزرگ آمبولانس (10L)",
                    selected = cylinderVolumeLiters == 10.0,
                    modifier = Modifier.weight(1f)
                ) { cylinderVolumeLiters = 10.0 }

                SetTypeChip(
                    title = "کپسول پرتابل/همراه (2L)",
                    selected = cylinderVolumeLiters == 2.0,
                    modifier = Modifier.weight(1f)
                ) { cylinderVolumeLiters = 2.0 }

                SetTypeChip(
                    title = "کپسول متوسط (5L)",
                    selected = cylinderVolumeLiters == 5.0,
                    modifier = Modifier.weight(1f)
                ) { cylinderVolumeLiters = 5.0 }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cylinderPressureBar,
                    onValueChange = { cylinderPressureBar = it },
                    label = { Text("فشار مانومتر (Bar)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = flowRateLpm,
                    onValueChange = { flowRateLpm = it },
                    label = { Text("جریان خروجی (L/min)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            ResultDisplayCard(
                results = listOf(
                    CalculationResultItem(
                        title = "مدت زمان تقریبی قابل استفاده:",
                        value = "$remainingHours ساعت و $remainingMinsOnly دقیقه (${remainingMinutes.roundToInt()} دقیقه)",
                        highlight = true,
                        color = if (remainingMinutes < 30) MaterialTheme.colorScheme.error else MedEmeraldSafe
                    ),
                    CalculationResultItem(
                        title = "حجم گاز اکسیژن خالص قابل تحویل:",
                        value = "${totalOxygenLiters.roundToInt()} لیتر اکسیژن",
                        highlight = false
                    ),
                    CalculationResultItem(
                        title = "وضعیت ایمنی سیلندر:",
                        value = when {
                            pressureNum <= 20 -> "⚠️ اخطار بحرانی: فشار صفر یا در حد حاشیه امن (نیاز به شارژ فوری)"
                            pressureNum < 50 -> "⚠️ هشدار: فشار پایین (کمتر از ۵۰ بار)"
                            pressureNum >= 120 -> "✅ وضعیت پر و آماده ماموریت"
                            else -> "🟡 فشار متوسط (${pressureNum.toInt()} بار)"
                        },
                        highlight = true,
                        color = if (pressureNum < 50) MaterialTheme.colorScheme.error else MedEmeraldSafe
                    )
                )
            )
        }
    }
}

// ------------------------------------------------------------------------------------------------
// 5. Pediatric Emergency Calculator (Weight by Age & Emergency Drugs)
// ------------------------------------------------------------------------------------------------
@Composable
fun PediatricEmergencyCalculatorTab() {
    var ageYears by remember { mutableStateOf("4") }
    var isInfantMonths by remember { mutableStateOf(false) }
    var ageMonths by remember { mutableStateOf("6") }

    val ageNum = ageYears.toDoubleOrNull() ?: 4.0
    val monthsNum = ageMonths.toDoubleOrNull() ?: 6.0

    // Weight estimation formulas (APLS Formula):
    // < 1 year: (Months + 9) / 2
    // 1 to 5 years: (Age × 2) + 8
    // 6 to 12 years: (Age × 3) + 7
    val estimatedWeightKg = if (isInfantMonths) {
        (monthsNum + 9.0) / 2.0
    } else {
        when {
            ageNum <= 5 -> (ageNum * 2.0) + 8.0
            else -> (ageNum * 3.0) + 7.0
        }
    }

    // Pediatric emergency drugs calculations
    val pedsDrugs = remember(estimatedWeightKg) {
        listOf(
            EmergencyPediatricDrug(
                nameFa = "اپی‌نفرین / آدرنالین (احیا)",
                nameEn = "Epinephrine (Cardiac Arrest 1:10,000)",
                indication = "ایست قلبی تنفسی اطفال (IV/IO)",
                formulaText = "0.01 mg/kg (0.1 ml/kg از رقت ۱:۱۰,۰۰۰)",
                calculatedDose = "%.2f ml (سی‌سی)".format(estimatedWeightKg * 0.1),
                ampouleConcentration = "آمپول ۱ میلی‌گرم در ۱۰ سی‌سی رقیق شود",
                administrationGuide = "هر ۳ تا ۵ دقیقه در حین CPR تکرار شود"
            ),
            EmergencyPediatricDrug(
                nameFa = "آتروپین (برادیکاردی)",
                nameEn = "Atropine (Bradycardia)",
                indication = "برادیکاردی همراه با کاهش پرفیوژن",
                formulaText = "0.02 mg/kg (حداقل 0.1 mg / حداکثر 0.5 mg)",
                calculatedDose = "%.2f mg (معادل %.2f ml)".format(
                    (estimatedWeightKg * 0.02).coerceIn(0.1, 0.5),
                    ((estimatedWeightKg * 0.02).coerceIn(0.1, 0.5)) / 0.5 // assuming 0.5mg/ml ampoule
                ),
                ampouleConcentration = "آمپول 0.5 mg/ml",
                administrationGuide = "تزریق سریع وریدی همراه با فلاش نرمال سالین"
            ),
            EmergencyPediatricDrug(
                nameFa = "آمپول دیازپام (تشنج)",
                nameEn = "Diazepam (Seizure)",
                indication = "کنترل تشنج فعال اطفال",
                formulaText = "0.2 to 0.3 mg/kg IV یا 0.5 mg/kg رکتال",
                calculatedDose = "%.2f mg (معادل %.2f ml رقیق شده)".format(
                    estimatedWeightKg * 0.2,
                    (estimatedWeightKg * 0.2) / 5.0 * 2.0 // 10mg/2ml
                ),
                ampouleConcentration = "آمپول 10 mg / 2 ml",
                administrationGuide = "آهسته وریدی طی ۲ دقیقه (پایش تنفس الزامی است)"
            ),
            EmergencyPediatricDrug(
                nameFa = "بیکربنات سدیم 8.4%",
                nameEn = "Sodium Bicarbonate 8.4%",
                indication = "اسیدوز متابولیک شدید و مسمومیت با TCA",
                formulaText = "1 mEq/kg (معادل 1 ml/kg از محلول 8.4%)",
                calculatedDose = "%.1f ml (سی‌سی)".format(estimatedWeightKg * 1.0),
                ampouleConcentration = "ویال 8.4% (1 mEq/ml)",
                administrationGuide = "در نوزادان با آب مقطر به نسبت ۱:۱ رقیق شود (4.2%)"
            ),
            EmergencyPediatricDrug(
                nameFa = "شوک دفیبریلاسیون (VF / pVT)",
                nameEn = "Defibrillation Shock Energy",
                indication = "شوک اول: 2 J/kg | شوک‌های بعدی: 4 J/kg",
                formulaText = "2 to 4 Joules / kg",
                calculatedDose = "شوک اول: %d ژول | شوک بعد: %d ژول".format(
                    (estimatedWeightKg * 2).toInt(),
                    (estimatedWeightKg * 4).toInt()
                ),
                ampouleConcentration = "پدل اطفال برای وزن زیر ۱۰ کیلوگرم",
                administrationGuide = "بلافاصله پس از شوک، ۲ دقیقه CPR ادامه یابد"
            ),
            EmergencyPediatricDrug(
                nameFa = "سرم بولوس نرمال سالین (شوک)",
                nameEn = "Normal Saline Bolus",
                indication = "شوک هیپوولمیک و دهیدراتاسیون شدید",
                formulaText = "20 ml/kg سریع وریدی",
                calculatedDose = "%d ml (سی‌سی)".format((estimatedWeightKg * 20).toInt()),
                ampouleConcentration = "سرم نمکی 0.9% (NS)",
                administrationGuide = "تزریق سریع طی ۱۰ تا ۲۰ دقیقه، سپس ارزیابی مجدد ریه"
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FormulaCard(
                formulaTitle = "فرمول تخمین وزن اطفال بر اساس سن (APLS)",
                formulaEn = "Weight (1-5 yrs) = (Age × 2) + 8 kg | Weight (6-12 yrs) = (Age × 3) + 7 kg",
                descriptionFa = "محاسبه فوری وزن تقریبی کودک و استخراج دوز دقیق داروهای احیا و کنترل تشنج."
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SetTypeChip(
                    title = "بر اساس سن (سال)",
                    selected = !isInfantMonths,
                    modifier = Modifier.weight(1f)
                ) { isInfantMonths = false }

                SetTypeChip(
                    title = "شیرخوار (بر حسب ماه)",
                    selected = isInfantMonths,
                    modifier = Modifier.weight(1f)
                ) { isInfantMonths = true }
            }
        }

        item {
            if (!isInfantMonths) {
                OutlinedTextField(
                    value = ageYears,
                    onValueChange = { ageYears = it },
                    label = { Text("سن کودک (سال)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                OutlinedTextField(
                    value = ageMonths,
                    onValueChange = { ageMonths = it },
                    label = { Text("سن شیرخوار (ماه)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "وزن تخمینی کودک:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "%.1f کیلوگرم (kg)".format(estimatedWeightKg),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Text(
                text = "💉 دوز داروهای حیاتی اورژانس برای وزن فوق:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(pedsDrugs) { drug ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = drug.nameFa,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MedEmeraldSafe.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = drug.calculatedDose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MedEmeraldSafe,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = drug.nameEn,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "فرمول: ${drug.formulaText}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "راهنما: ${drug.administrationGuide}",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Reusable UI Helpers for Calculator
// ------------------------------------------------------------------------------------------------

@Composable
private fun FormulaCard(
    formulaTitle: String,
    formulaEn: String,
    descriptionFa: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "📐 $formulaTitle",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formulaEn,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descriptionFa,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}

data class CalculationResultItem(
    val title: String,
    val value: String,
    val highlight: Boolean = false,
    val color: Color = Color.Unspecified
)

@Composable
private fun ResultDisplayCard(
    results: List<CalculationResultItem>
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            results.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (item.highlight) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.value,
                        fontSize = if (item.highlight) 13.sp else 11.sp,
                        fontWeight = if (item.highlight) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (item.color != Color.Unspecified) item.color else MaterialTheme.colorScheme.onSurface
                    )
                }
                if (index < results.size - 1) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickPresetButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
        )
    }
}

@Composable
private fun SetTypeChip(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
    }
}
