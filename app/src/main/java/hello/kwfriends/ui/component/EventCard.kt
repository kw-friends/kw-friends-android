package hello.kwfriends.ui.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.valentinilk.shimmer.shimmer
import hello.kwfriends.firebase.realtimeDatabase.Events
import hello.kwfriends.image.ImageLoaderFactory
import hello.kwfriends.ui.screens.main.MainViewModel
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
    pageCount: Int,
    mainViewModel: MainViewModel
) {
    val pagerState = rememberPagerState(pageCount = { if (pageCount == 0) 1 else pageCount })

    val context = LocalContext.current
    val imageLoader = ImageLoaderFactory.getInstance(context)


    val threePagesPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return (availableSpace - 2 * pageSpacing)
        }
    }

    LaunchedEffect(key1 = true) {
        val initialPage = 0

        pagerState.scrollToPage(initialPage)
    }

    // 일정 시간마다 다음 이미지로 변경
    LaunchedEffect(key1 = pagerState.currentPage) {
        launch {
            while (!Events.isEventLoading && Events.eventCount != 0) {
                delay(AUTO_PAGE_CHANGE_DELAY)
                withContext(NonCancellable) {
                    if (pagerState.currentPage + 1 in 0..<pageCount) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        pagerState.animateScrollToPage(0)
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
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = if (Events.isEventLoading) Modifier.shimmer() else Modifier
        ) { page ->
            Box(
                Modifier
                    .height(120.dp)
                    .heightIn(min = 120.dp)
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
                    .background(Color(0xFFE7E4E4))
            ) {
                if (Events.eventDetails.isNotEmpty()) {
                    AsyncImage(
                        model = Events.eventDetails[page].eventImageUrl,
                        placeholder = null,
                        imageLoader = imageLoader,
                        contentDescription = "Event Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        onSuccess = {
                            Log.d("AsyncImage.onSuccess", "이미지 로드 완료")
                            Events.eventDetails[page].loaded = true
                            Events.ckeckEventsLoadStatus()
                        }
                    )
                }
                if (!Events.isEventLoading && pageCount == 0) {
                    Text(
                        text = "진행 중인 이벤트가 없습니다.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (Events.isEventLoading) {
                    Text(
                        text = "이벤트 로드 중..",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }
        }
    }
}