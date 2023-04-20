package com.example.realestatemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interest")
class InterestEntity(
    @PrimaryKey(autoGenerate = false)
    var interestName:String
) {

}