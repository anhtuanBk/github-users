package co.touchlab.kampkit.android.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.domain.model.User
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
internal fun UserRow(
  item: User,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  Card(
    modifier = modifier,
    elevation = CardDefaults.elevatedCardElevation(
      defaultElevation = 3.dp,
    ),
    shape = RoundedCornerShape(size = 20.dp),
  ) {
    Row(
      modifier = Modifier.padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AsyncImage(
        modifier = Modifier
          .size(92.dp)
          .clip(RoundedCornerShape(size = 20.dp))
          .background(Color.White),
        model = remember(context, item.avatarUrl) {
          ImageRequest.Builder(context)
            .data(item.avatarUrl)
            .crossfade(true)
            .build()
        },
        placeholder = painterResource(R.drawable.icons8_github_96),
        contentDescription = "Avatar",
        contentScale = ContentScale.FillBounds,
      )

      Spacer(modifier = Modifier.width(8.dp))

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center,
      ) {
        Text(
          item.login,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          item.htmlUrl,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}
