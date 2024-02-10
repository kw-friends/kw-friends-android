package hello.kwfriends.ui.component

import android.util.Log
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun AnnotatedClickableText(text: String, style: TextStyle) {
    val uriHandler = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {
        val pattern =
            Regex("""(https?:\/\/www\.|http?:\/\/www\.|https?:\/\/|http?:\/\/)?[a-zA-Z0-9.-]+(\.[a-zA-Z]{2,})+\/?[a-zA-Z0-9\/_-]*""")
        var lastIndex = 0

        pattern.findAll(text).forEach { matchResult ->
            val url = matchResult.value
            val index = matchResult.range.first


            append(text.substring(lastIndex, index))

            pushStringAnnotation(tag = "URL", annotation = url)
            withStyle(style = SpanStyle(color = Color(0xff1b8cf0))) {
                append(url)
            }
            pop()

            lastIndex = matchResult.range.last + 1
        }

        append(text.substring(lastIndex, text.length))
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(urlAutoComplete(annotation.item))
                    Log.d("ClickableText", "Clicked URL: ${annotation.item}")
                }
        },
        style = style
    )
}

private fun urlAutoComplete(url: String): String {
    return if (!url.startsWith("https://") && !url.startsWith("http://")) {
        "https://$url"
    } else {
        url
    }
}
