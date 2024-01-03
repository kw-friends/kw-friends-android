package hello.kwfriends.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import hello.kwfriends.R

object AppFont {
    val defaultFontFamily = FontFamily(
        Font(R.font.notosans_100, FontWeight.Thin),
        Font(R.font.notosans_200, FontWeight.ExtraLight),
        Font(R.font.notosans_300, FontWeight.Light),
        Font(R.font.notosans_400, FontWeight.Normal),
        Font(R.font.notosans_500, FontWeight.Medium),
        Font(R.font.notosans_600, FontWeight.SemiBold),
        Font(R.font.notosans_700, FontWeight.Bold),
        Font(R.font.notosans_800, FontWeight.ExtraBold),
        Font(R.font.notosans_900, FontWeight.Black),
    )
}

private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.defaultFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.defaultFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.defaultFontFamily),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.defaultFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.defaultFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.defaultFontFamily),

    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = AppFont.defaultFontFamily,
        fontSize = 24.sp
    ),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.defaultFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.defaultFontFamily),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.defaultFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.defaultFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.defaultFontFamily),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.defaultFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.defaultFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.defaultFontFamily)
)