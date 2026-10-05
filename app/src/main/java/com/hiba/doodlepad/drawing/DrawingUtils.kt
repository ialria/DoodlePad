package com.hiba.doodlepad.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.abs
import kotlin.math.pow

private fun distanceBetween(first: Offset, second: Offset): Float {
    return (first - second).getDistance()
}
private fun safeDivider(value: Offset, divisor: Float): Offset {
    return if (abs(divisor) < 0.0001f) {
        Offset.Zero
    } else {
        value / divisor
    }
}

fun createPath(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) {
        return path
    }
//val resampledPoints=resamplePoints(points)
    val smoothedPoints = smoothPoints(points)

    if (smoothedPoints.size == 2) {
        path.moveTo(smoothedPoints[0].x, smoothedPoints[0].y)
        path.lineTo(smoothedPoints[1].x, smoothedPoints[1].y)
        return path
    }

//        points.drop(1).forEach { point ->
    val extendedPoints =
        listOf(smoothedPoints.first()) + smoothedPoints + listOf(smoothedPoints.last())
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
        val control1 = p1 + m1 / 3f
//                previous=control point
//            val control2 = p2 - (p3 - p1) / 6f
        val control2 = p2 - m2 / 3f
//                 p2 - safeDivider(
//                    p3 - p1,t3 - t1)*((t2 - t1) / 3f)

//                end=middle point
        val end = p2

        path.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)


    }
    return path

}

private fun smoothPoints(points: List<Offset>): List<Offset> {
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
////        if (result.last() != points.last()) {
////            result.add(points.last())
//
//
//        return result
//
//    }
