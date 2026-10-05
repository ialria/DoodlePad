package com.hiba.doodlepad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hiba.doodlepad.screens.DoodlePadScreen
import com.hiba.doodlepad.ui.theme.DoodlePadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoodlePadTheme {
                DoodlePadScreen()
            }
        }
    }
}


@Preview
@Composable
fun PreviewDoodlePad()
{
    DoodlePadScreen()
}
