package hello.kwfriends.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

// ref: https://developer.android.com/jetpack/compose/layouts/pager

const val AUTO_PAGE_CHANGE_DELAY = 3500L

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventCard(
    pageCount: Int
) {
    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })

    val threePagesPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return (availableSpace - 2 * pageSpacing)
        }
    }

    LaunchedEffect(key1 = true) {
        var initialPage = Int.MAX_VALUE / 2

        // 시작 페이지를 0번째 페이지로 설정
        while (initialPage % pageCount != 0) {
            initialPage++
        }

        pagerState.scrollToPage(initialPage)
    }

    // 일정 시간마다 다음 이미지로 변경
    LaunchedEffect(key1 = pagerState.currentPage) {
        launch {
            while (true) {
                delay(AUTO_PAGE_CHANGE_DELAY)
                withContext(NonCancellable) {
                    if (pagerState.currentPage + 1 in 0..Int.MAX_VALUE) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "이벤트",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W600,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 14.dp)
        )
        HorizontalPager(
            state = pagerState,
            pageSize = threePagesPerViewport,
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) { page ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.6f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .height(120.dp)
                    .background(Color(0xFFE7E4E4))
            ) {
                Text(
                    text = "page = ${page % pageCount}",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}