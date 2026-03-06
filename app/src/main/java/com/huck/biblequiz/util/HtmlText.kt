package com.huck.biblequiz.util

import android.graphics.Typeface
import android.text.style.StyleSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.core.text.HtmlCompat

fun String.htmlToAnnotatedString(): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    return buildAnnotatedString {
        append(spanned.toString())
        spanned.getSpans(0, spanned.length, StyleSpan::class.java).forEach { span ->
            if (span.style == Typeface.ITALIC) {
                addStyle(
                    SpanStyle(fontStyle = FontStyle.Italic),
                    spanned.getSpanStart(span),
                    spanned.getSpanEnd(span)
                )
            }
        }
    }
}
