package com.example.data.repository

import com.example.data.local.AdminMessageDao
import com.example.data.local.InspectionDao
import com.example.data.local.MedicalItemDao
import com.example.data.model.AdminMessage
import com.example.data.model.InspectionRecord
import com.example.data.model.ItemCategory
import com.example.data.model.MedicalItem
import kotlinx.coroutines.flow.Flow

class MedicalItemRepository(
    private val dao: MedicalItemDao,
    private val inspectionDao: InspectionDao,
    private val adminMessageDao: AdminMessageDao
) {

    val allItems: Flow<List<MedicalItem>> = dao.getAllItems()
    val allInspections: Flow<List<InspectionRecord>> = inspectionDao.getAllInspections()
    val allAdminMessages: Flow<List<AdminMessage>> = adminMessageDao.getAllMessages()
    val latestUnreadAdminMessage: Flow<AdminMessage?> = adminMessageDao.getLatestUnreadMessage()

    fun searchItems(query: String): Flow<List<MedicalItem>> {
        return dao.searchItems(query)
    }

    suspend fun getAllItemsList(): List<MedicalItem> {
        return dao.getAllItemsList()
    }

    suspend fun getItemById(id: Int): MedicalItem? {
        return dao.getItemById(id)
    }

    suspend fun insertItem(item: MedicalItem): Long {
        return dao.insertItem(item)
    }

    suspend fun updateItem(item: MedicalItem) {
        dao.updateItem(item)
    }

    suspend fun deleteItem(item: MedicalItem) {
        dao.deleteItem(item)
    }

    suspend fun deleteItemById(id: Int) {
        dao.deleteItemById(id)
    }

    suspend fun insertInspection(record: InspectionRecord): Long {
        return inspectionDao.insertInspection(record)
    }

    suspend fun deleteInspection(id: Int) {
        inspectionDao.deleteInspection(id)
    }

    suspend fun clearInspectionHistory() {
        inspectionDao.clearAllHistory()
    }

    suspend fun insertAdminMessage(message: AdminMessage): Long {
        return adminMessageDao.insertMessage(message)
    }

    suspend fun markAdminMessageRead(id: Int) {
        adminMessageDao.markAsRead(id)
    }

    suspend fun deleteAdminMessage(id: Int) {
        adminMessageDao.deleteMessageById(id)
    }

    suspend fun clearAdminMessages() {
        adminMessageDao.clearAllMessages()
    }
}
