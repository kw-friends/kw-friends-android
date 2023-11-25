package hello.kwfriends.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable

fun NoSearchResult(searchKeyword: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.weight(6f),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = " \"${searchKeyword}\"에 대한 검색 결과가 없습니다",
                style = MaterialTheme.typography.titleMedium,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 20.dp),
                maxLines = 100
            )
        }
    }
}