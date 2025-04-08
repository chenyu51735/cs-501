package com.example.diaryapp.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryapp.viewModel.DiaryViewModel
import java.util.*

@Composable
fun DiaryScreen(viewModel: DiaryViewModel) {
    val date = viewModel.selectedDate.collectAsState().value
    val content = viewModel.entryContent.collectAsState().value
    val fontSize = viewModel.fontSize.collectAsState().value
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Date: $date", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val calendar = Calendar.getInstance()
            val (year, month, day) = date.split("-").map { it.toInt() }
            DatePickerDialog(
                context,
                { _, y, m, d ->
                    val newDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                    viewModel.setDate(newDate)
                }, year, month - 1, day
            ).show()
        }) {
            Text("Pick Date")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = viewModel::updateContent,
            label = { Text("Your diary entry") },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            textStyle = TextStyle(fontSize = fontSize.sp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.saveEntry() }) {
            Text("Save Entry")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Font size: ", modifier = Modifier.padding(end = 8.dp))
            Slider(
                value = fontSize.toFloat(),
                onValueChange = { viewModel.setFontSize(it.toInt()) },
                valueRange = 12f..32f,
                steps = 4,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
