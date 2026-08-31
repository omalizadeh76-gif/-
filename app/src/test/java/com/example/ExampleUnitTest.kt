package com.example

import com.example.data.model.AdminMessage
import com.example.data.model.InspectionRecord
import com.example.ui.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGregorianToJalaliConversion() {
        // 2025-03-21 -> 1404-01-01 (Nowruz)
        val (jy, jm, jd) = DateUtils.gregorianToJalali(2025, 3, 21)
        assertEquals(1404, jy)
        assertEquals(1, jm)
        assertEquals(1, jd)
    }

    @Test
    fun testPersianDigits() {
        val persian = DateUtils.toPersianDigits("1403/06/10")
        assertEquals("۱۴۰۳/۰۶/۱۰", persian)
    }

    @Test
    fun testShamsiDateTimeFormat() {
        val testTimestamp = 1742544000000L // 2025-03-21 approx
        val formatted = DateUtils.formatShamsiDateTime(testTimestamp)
        assertTrue(formatted.contains("ساعت"))
        assertTrue(formatted.contains("۱۴۰۴") || formatted.contains("۱۴۰۳"))
    }

    @Test
    fun testInspectionRecordModel() {
        val record = InspectionRecord(
            timestamp = System.currentTimeMillis(),
            totalChecked = 10,
            expiredCount = 1,
            expiringSoonCount = 2,
            safeCount = 7,
            jumpBagMedicineCount = 4,
            medicineCabinetCount = 3,
            jumpBagEquipmentCount = 2,
            equipmentCabinetCount = 1,
            detailsSummary = "تست خلاصه",
            statusNote = "بررسی موفق"
        )
        assertEquals(10, record.totalChecked)
        assertEquals(4, record.jumpBagMedicineCount)
        assertEquals(3, record.medicineCabinetCount)
        assertEquals(2, record.jumpBagEquipmentCount)
        assertEquals(1, record.equipmentCabinetCount)
    }

    @Test
    fun testAdminMessageModel() {
        val msg = AdminMessage(
            title = "هشدار دارویی",
            content = "بررسی فوری آمپول‌های آتروپین",
            priority = "CRITICAL",
            targetCategory = "JUMPBAG_MEDICINES",
            timestamp = System.currentTimeMillis()
        )
        assertEquals("هشدار دارویی", msg.title)
        assertEquals("CRITICAL", msg.priority)
        assertEquals("JUMPBAG_MEDICINES", msg.targetCategory)
        assertFalse(msg.isRead)
    }
}
