package hello.kwfriends.ui.component.dateTimePicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private const val guideline =
    """커뮤니티 가이드라인 버전 1
2024년 2월 9일 최종 수정

1. 존중과 예의: 모든 사용자는 서로를 존중하며 예의 바르게 행동해야 합니다. 부적절한 언어 사용, 인신공격, 차별적 발언은 금지됩니다. 건설적이고 긍정적인 대화를 장려하며, 상호간의 이해와 협력을 바탕으로 하는 커뮤니티를 만들어 가야 합니다.
2. 개인정보 보호: 타인의 개인정보를 무단으로 공유하거나, 개인정보를 부적절하게 사용하지 않도록 주의해야 합니다.
3. 저작권 보호: 저작권이 있는 콘텐츠를 무단으로 공유하거나 사용하지 않아야 합니다. 이는 음악, 사진, 글, 소프트웨어 등 모든 형태의 창작물에 적용됩니다.
4. 안전한 환경 유지: 위험, 혹은 법에 위축되거나 폭력적인 언행과 행동은 엄격히 금지됩니다. 모든 사용자는 온라인과 오프라인 모임에서 안전을 최우선으로 고려해야 합니다.
5. 규칙 준수: 위 규칙들을 따르지 않을 때, 서비스 이용에 제한을 받을 수 있습니다.

이외에 문제가 발생할 경우, 설정 > 문의하기 를 통해 관리자에게 알려주세요!


"""

@Composable
fun CommunityGuidelinePopup(
    state: Boolean,
    onDismiss: () -> Unit
) {

    if (state) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(500.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = guideline,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}