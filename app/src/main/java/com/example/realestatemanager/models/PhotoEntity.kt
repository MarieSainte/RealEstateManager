package com.example.realestatemanager.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "photo",
    foreignKeys = [ForeignKey(
    entity = HousingEntity::class,
    childColumns = ["roomId"],
    parentColumns = ["roomId"]
)])
class PhotoEntity (
    @PrimaryKey(autoGenerate = true)
    val photoId: Long? = null,
    var caption:String="",
    var photo: Bitmap,
    var roomId:Long? = null
)