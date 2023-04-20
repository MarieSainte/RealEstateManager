package com.example.realestatemanager.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["roomId", "interestName"])
class HousingInterestCrossRef(

    val roomId: Long,
    val interestName: String,
)

data class HousingWithInterest(
    @Embedded val housing: HousingEntity,
    @Relation(
        parentColumn = "roomId",
        entityColumn = "interestName",
        associateBy = Junction(HousingInterestCrossRef::class)
    )
    val interestsInHousing: List<InterestEntity>
)