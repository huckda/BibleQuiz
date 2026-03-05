package com.huck.biblequiz.ui.screens.settings

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

// ── Helpers ──────────────────────────────────────────────────────────────────

internal fun hsvToColor(hue: Float, sat: Float, value: Float): Color =
    Color(AndroidColor.HSVToColor(floatArrayOf(hue, sat, value)))

internal fun colorToHsv(color: Color): FloatArray {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(color.toArgb(), hsv)
    return hsv
}

private fun colorToHex(color: Color): String =
    "#%06X".format(color.toArgb() and 0xFFFFFF)

private fun parseHex(hex: String): Color? {
    val clean = hex.trimStart('#')
    if (clean.length != 6) return null
    return try {
        Color(("FF$clean").toLong(16).toInt())
    } catch (_: NumberFormatException) { null }
}

// ── Dialog ───────────────────────────────────────────────────────────────────

@Composable
fun ColorPickerDialog(
    title: String,
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val initialHsv = remember { colorToHsv(initialColor) }
    var hue   by remember { mutableFloatStateOf(initialHsv[0]) }
    var sat   by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }
    var hexText by remember { mutableStateOf(colorToHex(initialColor)) }

    val currentColor = hsvToColor(hue, sat, value)

    // Keep hex in sync when sliders move
    LaunchedEffect(hue, sat, value) {
        hexText = colorToHex(hsvToColor(hue, sat, value))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // 2-D Saturation / Value panel
                SatValPanel(hue = hue, sat = sat, value = value) { s, v ->
                    sat = s; value = v
                }

                // Hue slider
                HueBar(hue = hue) { hue = it }

                // Preview swatch + hex input
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(currentColor, RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    )
                    OutlinedTextField(
                        value = hexText,
                        onValueChange = { text ->
                            hexText = text
                            parseHex(text)?.let { parsed ->
                                val hsv = colorToHsv(parsed)
                                hue = hsv[0]; sat = hsv[1]; value = hsv[2]
                            }
                        },
                        label = { Text("Hex  (#RRGGBB)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onColorSelected(currentColor); onDismiss() }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ── Saturation / Value Panel ──────────────────────────────────────────────────

@Composable
private fun SatValPanel(
    hue: Float,
    sat: Float,
    value: Float,
    onChanged: (sat: Float, value: Float) -> Unit
) {
    val pureHue = hsvToColor(hue, 1f, 1f)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .pointerInput(hue) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onChanged(
                        (down.position.x / size.width).coerceIn(0f, 1f),
                        1f - (down.position.y / size.height).coerceIn(0f, 1f)
                    )
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { ch ->
                            onChanged(
                                (ch.position.x / size.width).coerceIn(0f, 1f),
                                1f - (ch.position.y / size.height).coerceIn(0f, 1f)
                            )
                            ch.consume()
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            // White → hue color (saturation, left→right)
            drawRect(brush = Brush.horizontalGradient(listOf(Color.White, pureHue)))
            // Transparent → black (value/brightness, top→bottom)
            drawRect(brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            // Thumb
            val cx = sat * size.width
            val cy = (1f - value) * size.height
            val r = 10.dp.toPx()
            drawCircle(Color.White, radius = r, center = Offset(cx, cy))
            drawCircle(Color.Black, radius = r, center = Offset(cx, cy), style = Stroke(2.dp.toPx()))
        }
    }
}

// ── Hue Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun HueBar(hue: Float, onHueChanged: (Float) -> Unit) {
    val spectrum = remember {
        listOf(
            Color(0xFFFF0000), Color(0xFFFFFF00), Color(0xFF00FF00),
            Color(0xFF00FFFF), Color(0xFF0000FF), Color(0xFFFF00FF), Color(0xFFFF0000)
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onHueChanged((down.position.x / size.width).coerceIn(0f, 1f) * 360f)
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { ch ->
                            onHueChanged((ch.position.x / size.width).coerceIn(0f, 1f) * 360f)
                            ch.consume()
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            // Rainbow gradient
            drawRect(brush = Brush.horizontalGradient(spectrum))
            // Thumb
            val cx = (hue / 360f) * size.width
            val cy = size.height / 2f
            val r  = size.height / 2f
            drawCircle(Color.White, radius = r, center = Offset(cx, cy))
            drawCircle(Color.Black, radius = r - 2.dp.toPx(), center = Offset(cx, cy), style = Stroke(2.dp.toPx()))
        }
    }
}
