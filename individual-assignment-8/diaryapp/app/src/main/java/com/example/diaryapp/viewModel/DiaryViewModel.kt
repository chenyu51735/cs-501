package com.example.diaryapp.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.storage.DiaryFileManager
import com.example.diaryapp.storage.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DiaryViewModel(private val context: Context) : ViewModel() {

    private val _selectedDate = MutableStateFlow(todayDate())
    val selectedDate: StateFlow<String> = _selectedDate

    private val _entryContent = MutableStateFlow("")
    val entryContent: StateFlow<String> = _entryContent

    private val _fontSize = MutableStateFlow(16)
    val fontSize: StateFlow<Int> = _fontSize

    private val fileManager = DiaryFileManager()
    private val prefsManager = UserPreferencesManager(context)

    init {
        loadEntry()
        loadFontSize()
    }

    fun setDate(date: String) {
        _selectedDate.value = date
        loadEntry()
    }

    private fun loadEntry() = viewModelScope.launch {
        _entryContent.value = fileManager.readEntry(context, _selectedDate.value)
    }

    fun updateContent(newText: String) {
        _entryContent.value = newText
    }

    fun saveEntry() = viewModelScope.launch {
        fileManager.saveEntry(context, _selectedDate.value, _entryContent.value)
    }

    fun setFontSize(size: Int) = viewModelScope.launch {
        prefsManager.setFontSize(size)
        _fontSize.value = size
    }

    private fun loadFontSize() = viewModelScope.launch {
        _fontSize.value = prefsManager.getFontSize().first()
    }

    companion object {
        fun todayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}

class DiaryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiaryViewModel(context) as T
    }
}
