package com.example.realestatemanager.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation



data class HousingWithPhoto(
    @Embedded val housing: HousingEntity,
    @Relation(
        parentColumn = "roomId",
        entityColumn = "photoId",
        associateBy = Junction(PhotoEntity::class)
    )
    val photosInHousing: List<PhotoEntity>
)