package com.example.realestatemanager.models

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "housing")
class HousingEntity(
    @PrimaryKey(autoGenerate = true)
    val roomId: Long? = null,
    var description: String= "",
    var type: String = "",
    var address: String = "",
    var surface: Int = -1,
    var room: Int = -1,
    var price: Int = -1,
    var status: String = "",
    var dateOfEntry: Date? = null,
    var dateOfSell: Date? = null,
    var agentName: String = ""
){
    companion object {
        @JvmStatic
        fun fromContentValues(values: ContentValues): HousingEntity {

            val housing = HousingEntity()

            if (values.containsKey("description")) housing.description = (values.getAsString("description"))
            if (values.containsKey("type")) housing.type = (values.getAsString("type"))
            if (values.containsKey("address")) housing.address = (values.getAsString("address"))
            if (values.containsKey("surface")) housing.surface = (values.getAsInteger("surface"))
            if (values.containsKey("room")) housing.room = (values.getAsInteger("room"))
            if (values.containsKey("price")) housing.price = (values.getAsInteger("price"))
            if (values.containsKey("status")) housing.status = (values.getAsString("status"))
            if (values.containsKey("agentName")) housing.agentName = (values.getAsString("userId"))
            return housing
        }
    }

}
