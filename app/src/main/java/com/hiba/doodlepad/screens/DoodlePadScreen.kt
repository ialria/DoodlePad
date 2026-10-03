package com.hiba.doodlepad.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hiba.doodlepad.R
import androidx.compose.ui.graphics.Path

@Composable
fun DoodlePadScreen() {
    val buttonColor = Color(0xFFE7decd)

    var strokes by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }

//    currentStrokes-state belonging to screen
//    remember-keep value across recompositions
//    var not val-mutable state
    var currentStrokes by remember {
        mutableStateOf<List<Offset>>(emptyList())
    }

    fun drawStrokes(points: List<Offset>): Path {
        val path = Path()

        path.moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { point ->
            path.lineTo(point.x, point.y)
        }
        return path

    }

    Scaffold(modifier = Modifier.fillMaxSize())
    { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .background(color = Color.Black)
        ) {
//            not a container - provides drawing surface
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .pointerInput(
                        Unit
                    ) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStrokes = listOf(offset)
                            },
                            onDrag = { change, dragAmount ->
                                //                        use + operator -creates a new list with all the old elements + new elements
//                            change tell use about finger pointer events-hold info about it

                                currentStrokes = currentStrokes + change.position
                            },
                            onDragEnd = {
                                strokes = strokes + listOf(currentStrokes)
                                currentStrokes = emptyList()
                            },
                            onDragCancel = {
                                currentStrokes=emptyList()
                            }
                        )
                    }
            ) {

                if (strokes.isNotEmpty()) {
                    strokes.forEach { stroke ->
                        val path = drawStrokes(stroke)


                        drawPath(
                            path = path, color = buttonColor,
                            style = Stroke(width = 4f)
                        )
                    }
                }
                if (currentStrokes.isNotEmpty()) {
                    val path = drawStrokes(currentStrokes)
                    drawPath(
                        path = path, color = buttonColor,
                        style = Stroke(width = 4f)
                    )
                }

            }


            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
strokes=emptyList()
                    currentStrokes=emptyList()
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = buttonColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}
