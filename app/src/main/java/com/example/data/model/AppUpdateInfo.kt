package com.example.data.model

data class AppUpdateInfo(
    val currentVersionName: String = "2.2.0",
    val currentVersionCode: Int = 22,
    val latestVersionName: String = "2.2.0",
    val latestVersionCode: Int = 22,
    val releaseDateShamsi: String = "۱۰ شهریور ۱۴۰۵",
    val changelog: List<String> = listOf(
        "ارتقا به نسخه ۲.۲.۰ همراه با سیستم آپدیت درون‌برنامه‌ای خودکار",
        "حفظ ۱۰۰٪ پایگاه داده داروها و تجهیزات بدون نیاز به حذف و نصب مجدد",
        "بهینه‌سازی و چیدمان سبک و خلوت کارت‌های داروها و تجهیزات",
        "افزودن پنل اختصاصی مدیریت و ارسال اعلان‌های همگانی به پرسنل",
        "پشتیبانی از آلارم‌های دارویی چندگانه و تم‌های روز و شب"
    ),
    val downloadUrl: String = "https://ais-pre-7avqj2uhg5bqejd6kyt3ih-829596571354.asia-southeast1.run.app",
    val apkSize: String = "۸.۵ مگابایت",
    val isUpdateAvailable: Boolean = false,
    val isMandatory: Boolean = false,
    val lastCheckedTimestamp: Long = System.currentTimeMillis()
)

enum class UpdateCheckState {
    IDLE,
    CHECKING,
    UP_TO_DATE,
    UPDATE_AVAILABLE,
    DOWNLOADING,
    READY_TO_INSTALL,
    ERROR
}
