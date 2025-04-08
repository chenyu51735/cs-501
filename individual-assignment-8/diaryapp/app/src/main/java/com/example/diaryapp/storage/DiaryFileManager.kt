package com.example.diaryapp.storage

import android.content.Context
import java.io.File

class DiaryFileManager {
    fun saveEntry(context: Context, date: String, content: String) {
        val file = File(context.filesDir, "$date.txt")
        file.writeText(content)
    }

    fun readEntry(context: Context, date: String): String {
        val file = File(context.filesDir, "$date.txt")
        return if (file.exists()) file.readText() else ""
    }
}
