package com.example.data.model

enum class InventoryFilter(val titleFa: String) {
    ALL("همه اقلام"),
    JUMPBAG_MEDICINES("داروهای جامبگ 🎒"),
    CABINET_MEDICINES("کمد دارویی 🚪"),
    JUMPBAG_EQUIPMENT("تجهیزات جامبگ 🎒"),
    CABINET_EQUIPMENT("کمد تجهیزات 🩺"),
    EXPIRING_SOON_ONLY("در آستانه انقضا ⚠️"),
    EXPIRED_ONLY("منقضی شده 🚨"),
    SAFE_ONLY("دارای اعتبار ✅"),
    MEDICINES_ONLY("همه داروها 💊"),
    EQUIPMENT_ONLY("همه تجهیزات 🩺")
}

enum class InventorySort(val titleFa: String) {
    EXPIRY_ASC("نزدیک‌ترین به انقضا"),
    EXPIRY_DESC("دورترین به انقضا"),
    NAME_ASC("نام (الف - ی)"),
    NAME_DESC("نام (ی - الف)"),
    LOCATION_ASC("محل نگهداری"),
    CREATED_DESC("جدیدترین ثبت‌شده")
}

data class InventoryStats(
    val totalItems: Int = 0,
    val medicineCount: Int = 0,
    val equipmentCount: Int = 0,
    val expiringSoonCount: Int = 0,
    val expiredCount: Int = 0,
    val safeCount: Int = 0,
    // Section specifics
    val jumpBagMedicineCount: Int = 0,
    val cabinetMedicineCount: Int = 0,
    val jumpBagEquipmentCount: Int = 0,
    val cabinetEquipmentCount: Int = 0
)
