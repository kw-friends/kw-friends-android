package hello.kwfriends.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagChip(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        onClick = { onClick() },
        label = {
            Text(text = "#$text", style = MaterialTheme.typography.labelMedium)
        },
        shape = RoundedCornerShape(20.dp),
        selected = selected,
    )
}

@Preview
@Composable
fun TagChipPreview(){
    TagChip(text = "프리뷰", selected = false, onClick = {})
}
