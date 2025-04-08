package com.example.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diaryapp.ui.DiaryScreen
import com.example.diaryapp.viewModel.DiaryViewModel
import com.example.diaryapp.viewModel.DiaryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: DiaryViewModel = viewModel(factory = DiaryViewModelFactory(applicationContext))
            MaterialTheme {
                DiaryScreen(viewModel)
            }
        }
    }
}