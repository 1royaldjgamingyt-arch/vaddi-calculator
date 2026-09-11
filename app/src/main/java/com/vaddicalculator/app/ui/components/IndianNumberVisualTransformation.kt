package com.vaddicalculator.app.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * VisualTransformation that formats numbers using Indian numbering grouping:
 * ₹1,000, ₹10,000, ₹1,00,000, ₹10,00,000, ₹1,00,00,000
 * Keeps underlying state as raw number string.
 */
class IndianNumberVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val dotIndex = raw.indexOf('.')
        val intPart = if (dotIndex >= 0) raw.substring(0, dotIndex) else raw
        val decPart = if (dotIndex >= 0) raw.substring(dotIndex) else ""

        val formattedInt = buildString {
            val len = intPart.length
            if (len <= 3) {
                append(intPart)
            } else {
                val last3 = intPart.substring(len - 3)
                val rest = intPart.substring(0, len - 3)
                val restBuilder = StringBuilder()
                var count = 0
                for (i in rest.length - 1 downTo 0) {
                    restBuilder.append(rest[i])
                    count++
                    if (count == 2 && i != 0) {
                        restBuilder.append(',')
                        count = 0
                    }
                }
                append(restBuilder.reverse().toString())
                append(',')
                append(last3)
            }
        }

        val transformedString = formattedInt + decPart

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, raw.length)
                if (clamped <= intPart.length) {
                    var commas = 0
                    val len = intPart.length
                    if (len > 3) {
                        for (i in 1..clamped) {
                            val digitsRemainingAfterI = len - i
                            if (digitsRemainingAfterI >= 3 && (digitsRemainingAfterI - 3) % 2 == 0 && digitsRemainingAfterI != 0 && i < len) {
                                commas++
                            }
                        }
                    }
                    return (clamped + commas).coerceIn(0, transformedString.length)
                } else {
                    val commasInInt = formattedInt.length - intPart.length
                    return (clamped + commasInInt).coerceIn(0, transformedString.length)
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, transformedString.length)
                var commas = 0
                for (i in 0 until clamped) {
                    if (i < transformedString.length && transformedString[i] == ',') {
                        commas++
                    }
                }
                return (clamped - commas).coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(transformedString), offsetMapping)
    }
}
