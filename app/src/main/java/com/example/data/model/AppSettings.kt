package com.example.data.model

enum class AppThemeMode(val titleFa: String, val descriptionFa: String) {
    SYSTEM("پیرو سیستم", "تنظیم خودکار با پوسته گوشی"),
    LIGHT("حالت روز (روشن) ☀️", "پس‌زمینه روشن و خوانا"),
    DARK("حالت شب (تاریک) 🌙", "کاهش خستگی چشم در شب")
}

enum class AlarmSoundType(val titleFa: String, val descriptionFa: String) {
    ALARM("آلارم و هشدار فوری 🚨", "صدای رسا و توجه‌برانگیز آلارم گوشی"),
    NOTIFICATION("صدای پیش‌فرض اعلان 🔔", "صدای استاندارد نوتیفیکیشن‌های سیستم"),
    RINGTONE("ملودی زنگ تماس 🎵", "صدای ملودی زنگ تماس")
}
