package com.hiba.doodlepad.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

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

    var tapPosition by remember {
        mutableStateOf<Offset?>(null)
    }

    fun createPath(points: List<Offset>): Path {
        val path = Path()
        if (points.isEmpty()) {
            return path
        }

        if (points.size == 2) {
            path.moveTo(points[0].x, points[0].y)
            path.lineTo(points[1].x, points[1].y)
            return path
        }

//        points.drop(1).forEach { point ->
        val extendedPoints = listOf(points.first()) + points + listOf(points.last())
        path.moveTo(extendedPoints[1].x, extendedPoints[1].y)

//            path.lineTo(point.x, point.y)
//        for until - last value is excluded
        for (i in 1 until extendedPoints.size - 2) {
//            points.size-2 because we will access i+2 and so that it remains  valid too
            val p0 = extendedPoints[i - 1]
            val p1 = extendedPoints[i]
            val p2 = extendedPoints[i + 1]
            val p3 = extendedPoints[i + 2]
//                start is wherever I currently am
            val control1 = p1 + (p2 - p0) / 6f
//                previous=control point
            val control2 = p2 - (p3 - p1) / 6f

//                end=middle point
            val end = p2

            path.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)


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
                                currentStrokes.add(offset)
                            },
                            onDrag = { change, _ ->
                                //                        use + operator -creates a new list with all the old elements + new elements
//                            change tell use about finger pointer events-hold info about it--dragAmount- how much the finger moved since the previous drag event
                                val newPoints =
                                    change.historical.map { it.position } + change.position
                                currentStrokes.addAll(newPoints)

                            },
                            onDragEnd = {
                                strokes = strokes + listOf(currentStrokes.toList())
                                currentStrokes.clear()
                            },
                            onDragCancel = {
                                currentStrokes.clear()
                            }
                        )

                    }
            ) {


                if (strokes.isNotEmpty()) {
                    strokes.forEach { stroke ->
                        val path = createPath(stroke)
                        when (stroke.size) {
                            1 -> {
                                drawCircle(
                                    color = buttonColor,
                                    radius = 2f,
                                    center = stroke.first()
                                )
                            }

                            else -> {
                                drawPath(
                                    color = buttonColor, path = path, style = Stroke(
                                        4f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }


//                        if(stroke.size==2){
//                            drawLine(color = buttonColor, stroke=4f, start=stroke.first(), end=stroke.last())
//                        }


                        }

                    }
                    if (currentStrokes.isNotEmpty()) {
                        val path = createPath(currentStrokes)


                        drawPath(
                            path = path, color = buttonColor, style = Stroke(
                                4f,
                                join = StrokeJoin.Round,
                                cap = StrokeCap.Round
                            )
                        )


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
