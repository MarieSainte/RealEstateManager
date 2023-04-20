package com.example.realestatemanager.services.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.example.realestatemanager.models.HousingEntity
import com.example.realestatemanager.services.room.AppDatabase
import com.example.realestatemanager.services.room.AppDatabase.Companion.getInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HouseContentProvider : ContentProvider() {

    companion object{
        @JvmStatic
        val instance = HouseContentProvider()
    }
    val AUTHORITY = "com.example.realestatemanager.services.provider"
    val TABLE_NAME: String = HousingEntity::class.java.simpleName
    val URI_HOUSING: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")

    val housing = HousingEntity()

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        if (context != null) {
            val housingId = ContentUris.parseId(uri).toString()
            val cursor = getInstance(context!!).housingDao().getHousingWithCursor(housingId)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }

        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.housing/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        if (context != null && values != null) {

            ioScope.launch {
                AppDatabase.Companion.getInstance(context!!).housingDao().insert(housing)
            }

            context!!.contentResolver.notifyChange(uri, null)
            return ContentUris.withAppendedId(uri, 1)

        }

        throw java.lang.IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }



}