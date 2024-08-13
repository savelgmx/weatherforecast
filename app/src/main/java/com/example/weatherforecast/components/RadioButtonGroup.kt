package com.example.weatherforecast.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun RadioButtonGroup(selectedOption: Int, optionsToSelect: Array<String>, onOptionSelected: (Int) -> Unit) {
    Column {
        optionsToSelect.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedOption == index,
                    onClick = { onOptionSelected(index) }
                )
                Text(option)
            }
        }
    }
}