package com.example.ui.util

import com.example.data.model.MedicalItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.US)
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)
    private val dateTimeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US)

    fun formatDate(timestamp: Long): String {
        if (timestamp <= 0L) return "بدون تاریخ انقضا"
        return dateFormatter.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormatter.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormatter.format(Date(timestamp))
    }

    /**
     * Converts a timestamp to a Solar Hijri (Shamsi) Persian date string (e.g. ۱۴۰۴/۰۱/۰۱)
     */
    fun formatShamsiDate(timestamp: Long): String {
        if (timestamp <= 0L) return "بدون تاریخ انقضا"
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)

        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val jmStr = if (jm < 10) "0$jm" else "$jm"
        val jdStr = if (jd < 10) "0$jd" else "$jd"
        val shamsiNum = "$jy/$jmStr/$jdStr"
        return toPersianDigits(shamsiNum)
    }

    /**
     * Formats full Shamsi date + time (e.g. ۱۴۰۴/۰۶/۱۰ ساعت ۱۶:۴۵)
     */
    fun formatShamsiDateTime(timestamp: Long): String {
        if (timestamp <= 0L) return "نامشخص"
        val shamsiDate = formatShamsiDate(timestamp)
        val timeStr = toPersianDigits(formatTime(timestamp))
        return "$shamsiDate ساعت $timeStr"
    }

    fun toPersianDigits(text: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val builder = StringBuilder()
        for (ch in text) {
            if (ch in '0'..'9') {
                builder.append(persianDigits[ch - '0'])
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = gy - 1600
        val gm2 = gm - 1
        val gd2 = gd - 1

        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400

        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..11) {
            val daysInMonth = if (i == 11 && isLeapJalali(jy)) 30 else jDaysInMonth[i]
            if (jDayNo < daysInMonth) {
                jm = i + 1
                break
            }
            jDayNo -= daysInMonth
        }
        val jd = jDayNo + 1
        return Triple(jy, jm, jd)
    }

    private fun isLeapJalali(jy: Int): Boolean {
        val rem = (((jy - 474) % 2820 + 474 + 38) * 682) % 2816
        return rem < 682
    }

    /**
     * Returns human readable countdown in Persian.
     */
    fun getReadableRemainingTime(item: MedicalItem, currentTimeMs: Long = System.currentTimeMillis()): String {
        if (!item.hasExpiryDate || item.expiryTimestamp <= 0L) {
            return "بدون انقضا (دائمی)"
        }

        val days = item.getRemainingDays(currentTimeMs)

        return when {
            days < 0 -> {
                val absDays = -days
                "منقضی شده (${toPersianDigits(absDays.toString())} روز پیش)"
            }
            days == 0L -> "امروز منقضی می‌شود!"
            days == 1L -> "فردا منقضی می‌شود (۱ روز)"
            days <= 30L -> "${toPersianDigits(days.toString())} روز باقی‌مانده (کمتر از ۱ ماه ⚠️)"
            days < 60L -> "${toPersianDigits(days.toString())} روز باقی‌مانده (~۱ ماه دیگر)"
            days < 365L -> {
                val months = days / 30
                val remDays = days % 30
                if (remDays > 0) "${toPersianDigits(months.toString())} ماه و ${toPersianDigits(remDays.toString())} روز اعتبار" else "${toPersianDigits(months.toString())} ماه اعتبار"
            }
            else -> {
                val years = days / 365
                val remMonths = (days % 365) / 30
                if (remMonths > 0) "${toPersianDigits(years.toString())} سال و ${toPersianDigits(remMonths.toString())} ماه اعتبار" else "${toPersianDigits(years.toString())} سال اعتبار"
            }
        }
    }

    fun getPresetTimestamp(daysFromNow: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysFromNow)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
