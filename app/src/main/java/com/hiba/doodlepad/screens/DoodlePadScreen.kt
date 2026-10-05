package com.hiba.doodlepad.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hiba.doodlepad.R
import com.hiba.doodlepad.drawing.createPath

@Composable
fun DoodlePadScreen() {
    val buttonColor = Color(0xFFE7decd)

    var strokes by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }

//    currentStrokes-state belonging to screen
//    remember-keep value across recompositions
//    var not val-mutable state
    val currentStrokes = remember {
        mutableStateListOf<Offset>()
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
                        awaitEachGesture {

                            val start = awaitFirstDown()
                            currentStrokes.add(start.position)
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.first()

                                if (change.positionChanged()) {
                                    currentStrokes.addAll(change.historical.map { it.position })
                                    currentStrokes.add(change.position)
                                }

                                if (change.changedToUp()) {
                                    break

                                }
                            }
                            strokes = strokes + listOf(currentStrokes.toList())
                            currentStrokes.clear()

                        }

//                        detectDragGestures(
//                            onDragStart = { offset ->
//                                currentStrokes.add(offset)
//                            },
//                            onDrag = { change, _ ->
//                                //                        use + operator -creates a new list with all the old elements + new elements
////                            change tell use about finger pointer events-hold info about it--dragAmount- how much the finger moved since the previous drag event
//                                val newPoints =
//                                    change.historical.map { it.position } + change.position
//                                currentStrokes.addAll(newPoints)
//
//                            },
//                            onDragEnd = {
//                                strokes = strokes + listOf(currentStrokes.toList())
//                                currentStrokes.clear()
//                            },
//                            onDragCancel = {
//                                currentStrokes.clear()
//                            }
//                        )
//
                    }
            ) {

                if (strokes.isNotEmpty()) {
                    strokes.forEach { stroke ->
                        when (stroke.size) {
                            1 -> {
                                drawCircle(
                                    color = buttonColor,
                                    radius = 3f,
                                    center = stroke.first()
                                )
                            }

                            else -> {
                                val path = createPath(stroke)

                                drawPath(
                                    color = buttonColor, path = path, style = Stroke(
                                        4f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }


                        }

                    }


                }
                if (currentStrokes.isNotEmpty()) {
                    when (currentStrokes.size) {
                        1 -> {
                            drawCircle(
                                color = buttonColor,
                                radius = 4f,
                                center = currentStrokes.first()
                            )
                        }

                        else -> {
                            val path = createPath(currentStrokes)

                            drawPath(
                                path = path,
                                color = buttonColor,
                                style = Stroke(
                                    4f,
                                    join = StrokeJoin.Round,
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                    }

                }


            }


            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
                    strokes = emptyList()
                    currentStrokes.clear()
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
