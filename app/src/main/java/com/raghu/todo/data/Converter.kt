package com.raghu.todo.data

import androidx.room.TypeConverter
import com.raghu.todo.data.models.Priority

class Converter {

    @TypeConverter
    fun fromPriority(priority: Priority) : String{
        return priority.name
    }

    fun toPriority(name:String) : Priority {
        return Priority.valueOf(name)
    }
}