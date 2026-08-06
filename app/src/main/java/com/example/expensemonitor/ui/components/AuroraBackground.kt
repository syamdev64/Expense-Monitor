package com.example.expensemonitor.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import kotlin.math.sin

private data class Beam(
    val xFactor: Float,
    val speed: Float,
    val length: Float,
    val width: Float,
    val alpha: Float,
    val delay: Float
)

@Composable
fun AuroraBackground() {

    val transition = rememberInfiniteTransition(label = "")

    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(15000, easing = LinearEasing)
        ),
        label = ""
    )
    val pulse by transition.animateFloat(

        initialValue = .8f,

        targetValue = 1.2f,

        animationSpec = infiniteRepeatable(

            tween(
                2000,
                easing = LinearEasing
            ),

            RepeatMode.Reverse

        ),

        label = ""

    )

    val beams = remember {

        List(40) {

            Beam(

                xFactor = kotlin.random.Random.nextFloat(),

                speed = kotlin.random.Random.nextFloat() * 0.7f + 0.4f,

                length = kotlin.random.Random.nextFloat() * 350f + 180f,

                width = kotlin.random.Random.nextFloat() * 2f + 1f,

                alpha = kotlin.random.Random.nextFloat() * 0.20f + 0.05f,

                delay = kotlin.random.Random.nextFloat()

            )

        }

    }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        // Aurora Gradient
        drawRect(
            color = Color.Black
        )

        // Moving Aurora Glow
//        repeat(4) { i ->
//
//            val x =
//                (size.width * (0.2f + i * 0.22f)) +
//                        sin(progress * 6.28f + i) * 120f
//
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(
//                        Color(0xFF00E5FF).copy(alpha = 0.12f),
//                        Color.Transparent
//                    )
//                ),
//                radius = 300f,
//                center = Offset(x.toFloat(), size.height * 0.4f)
//            )
//        }

        // Moving Diagonal Lines
// Premium Energy Beams
        beams.forEach { beam ->

            val x = beam.xFactor * size.width

            val beamProgress =
                ((progress * beam.speed) + beam.delay) % 1f

            val headY =
                size.height -
                        beamProgress *
                        (size.height + beam.length)

            val tailY =
                headY + beam.length

            // Outer Glow
            drawLine(

                color = Color(0xFF111111).copy(
                    alpha = beam.alpha * pulse                ),

                start = Offset(x, headY),

                end = Offset(x, tailY),

                strokeWidth = beam.width * 1,

                cap = StrokeCap.Butt

            )

            // Middle Glow
            drawLine(

                color = Color(0xFF111111).copy(
                    alpha = beam.alpha * pulse                ),

                start = Offset(x, headY),

                end = Offset(x, tailY),

                strokeWidth = beam.width * 1,

                cap = StrokeCap.Round

            )

            // Core Beam
            drawLine(

                brush = Brush.verticalGradient(

                    colors = listOf(

                        Color(0xFF91FA94),

                        Color(0xFF222222),

                        Color(0xFF111111),

                        Color.Transparent

                    )

                ),

                start = Offset(x, headY),

                end = Offset(x, tailY),

                strokeWidth = beam.width,

                cap = StrokeCap.Round

            )

            // Head Glow
            drawCircle(

                brush = Brush.radialGradient(

                    colors = listOf(

                        Color(0xFFC0FAAB),

                        Color(0xFF111111),

                        Color.Transparent

                    )

                ),

                radius = beam.width * 1,

                center = Offset(x, headY)

            )

            // Bright Head
            drawCircle(

                color = Color(0xFF444444),

                radius = beam.width + 1f,

                center = Offset(x, headY)

            )

        }
        // Floating Particles
        repeat(120) { i ->

            val y =
                size.height -
                        ((progress * 2600f + i * 110f) %
                                (size.height + 200))

            val x =
                ((i * 53f) +
                        sin(progress * 6 + i) * 35f)
                    .toFloat()

            drawCircle(

                color = Color.DarkGray.copy(

                    alpha =
                        if (i % 6 == 0) .15f else .05f

                ),

                radius =
                    if (i % 5 == 0) 2.5f else 1f,

                center = Offset(x, y)

            )

        }

    }
}