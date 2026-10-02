package com.hiba.doodlepad.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hiba.doodlepad.R

@Composable
fun DoodlePadScreen() {
    val buttonColor:Color=Color(0xFFE7decd)
    Scaffold(modifier = Modifier.fillMaxSize())
    { scaffoldPadding->
        Column(
            modifier = Modifier.fillMaxSize().padding(scaffoldPadding).background(color = Color.Black)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)

            ){
//                Text(text="Hello",
//                    color=Color.Black)
            }

            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center){
                IconButton(onClick = {

                }){
                    Icon(imageVector =Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = buttonColor,
                        modifier = Modifier.size(30.dp)
                        )
                }
            }
        }
    }
}
