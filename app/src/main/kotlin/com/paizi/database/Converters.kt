package com.paizi.database

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromJsonToMap(value: String?): Map<String, Any>? {
        return if (value == null) null else gson.fromJson(value, Map::class.java) as? Map<String, Any>
    }

    @TypeConverter
    fun mapToJson(map: Map<String, Any>?): String? {
        return if (map == null) null else gson.toJson(map)
    }

    @TypeConverter
    fun fromJsonToList(value: String?): List<String>? {
        return if (value == null) null else gson.fromJson(value, List::class.java) as? List<String>
    }

    @TypeConverter
    fun listToJson(list: List<String>?): String? {
        return if (list == null) null else gson.toJson(list)
    }
}
