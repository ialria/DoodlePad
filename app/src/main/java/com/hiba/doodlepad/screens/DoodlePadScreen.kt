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
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.positionChanged
import kotlin.math.pow

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

    fun distanceBetween(first: Offset, second: Offset): Float {
        return (first - second).getDistance()
    }

    fun safeDivider(value: Offset, divisor: Float): Offset {
        return if (kotlin.math.abs(divisor) < 0.0001f) {
            Offset.Zero
        } else {
            value / divisor
        }
    }

//    fun resamplePoints(points:List<Offset>, spacing: Float=4f):List<Offset>{
//        if(points.size<2)
//        {
//            return points
//        }
//
////        to store result
//        val result=mutableListOf(points.first())
//
//        var previousPoint=points.first()
////        var distanceFromLastSample=0f
//
//        for(i in 1 until points.size) {
//            val currentPoint = points[i]
//            var segment = currentPoint - previousPoint
//            var segmentLength = segment.getDistance()
//
////            if(segmentLength==0f)
////            {
////                continue
////            }
////            var remainingDistance=segmentLength
////            while(distanceFromLastSample+remainingDistance>=spacing){
////                val distanceToSample = spacing - distanceFromLastSample
////                val ratio = distanceToSample / remainingDistance
////
////                val samplePoint = previousPoint + segment * ratio
////
////                result.add(samplePoint)
////
////                previousPoint = samplePoint
////                remainingDistance -= distanceToSample
////                distanceFromLastSample = 0f
////            }
//
//            while (segmentLength >= spacing) {
//
//                val direction = segment / segmentLength
//                val samplePoint = previousPoint + direction * spacing
//
//                result.add(samplePoint)
//
//                previousPoint = samplePoint
//
//                segment = currentPoint - previousPoint
//                segmentLength = segment.getDistance()
//            }
//            previousPoint = currentPoint
//        }
//        if (result.last() != points.last()) {
//            result.add(points.last())
//        }
//
////            distanceFromLastSample += remainingDistance
////            previousPoint = currentPoint
////        }
////        if (result.last() != points.last()) {
////            result.add(points.last())
//
//
//        return result
//
//    }

    fun smoothPoints(points: List<Offset>): List<Offset> {
        if (points.size < 3) {
            return points
        }

        val result = mutableListOf<Offset>()

        result.add(points.first())

        for (i in 1 until points.lastIndex) {
            val previous = points[i - 1]
            val current = points[i]
            val next = points[i + 1]

            val smoothed = previous * 0.25f +
                    current * 0.5f +
                    next * 0.25f

            result.add(smoothed)
        }

        result.add(points.last())

        return result
    }

    fun createPath(points: List<Offset>): Path {
        val path = Path()
        if (points.isEmpty()) {
            return path
        }
//val resampledPoints=resamplePoints(points)
        val smoothedPoints=smoothPoints(points)

        if (smoothedPoints.size == 2) {
            path.moveTo(smoothedPoints[0].x, smoothedPoints[0].y)
            path.lineTo(smoothedPoints[1].x, smoothedPoints[1].y)
            return path
        }

//        points.drop(1).forEach { point ->
        val extendedPoints = listOf(smoothedPoints.first()) + smoothedPoints+ listOf(smoothedPoints.last())
        path.moveTo(extendedPoints[1].x, extendedPoints[1].y)

//            path.lineTo(point.x, point.y)
//        for until - last value is excluded
        for (i in 1 until extendedPoints.size - 2) {
//            points.size-2 because we will access i+2 and so that it remains  valid too
            val p0 = extendedPoints[i - 1]
            val p1 = extendedPoints[i]
            val p2 = extendedPoints[i + 1]
            val p3 = extendedPoints[i + 2]

            val t0 = 0f
            val t1 = t0 + distanceBetween(p0, p1).pow(0.5f)
            val t2 = t1 + distanceBetween(p1, p2).pow(0.5f)
            val t3 = t2 + distanceBetween(p2, p3).pow(0.5f)

            val m1 = (safeDivider(p1 - p0, t1 - t0)
                    - safeDivider(p2 - p0, t2 - t0)
                    + safeDivider(p2 - p1, t2 - t1)) * (t2 - t1)

            val m2 = (safeDivider(p2 - p1, t2 - t1)
                    - safeDivider(p3 - p1, t3 - t1)
                    + safeDivider(p3 - p2, t3 - t2)) * (t2 - t1)

//                start is wherever I currently am
            val control1 = p1+m1/3f
//                previous=control point
//            val control2 = p2 - (p3 - p1) / 6f
            val control2 =p2-m2/3f
//                 p2 - safeDivider(
//                    p3 - p1,t3 - t1)*((t2 - t1) / 3f)

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
                                    radius = 4f,
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
