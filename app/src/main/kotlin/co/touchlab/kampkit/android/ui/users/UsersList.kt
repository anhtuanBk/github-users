package co.touchlab.kampkit.android.ui.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kampkit.android.ui.core_ui.LoadingIndicator
import co.touchlab.kampkit.android.ui.core_ui.RetryButton
import co.touchlab.kampkit.domain.model.User
import co.touchlab.kermit.Logger
import com.hoc081098.flowext.ThrottleConfiguration
import com.hoc081098.flowext.throttleTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.filter
import kotlin.time.Duration.Companion.milliseconds

private const val UserItemsListLogTag = "UserItemsList"

@Composable
internal fun UsersList(
  items: ImmutableList<User>,
  isLoading: Boolean,
  error: Throwable?,
  hasReachedMax: Boolean,
  onRetry: () -> Unit,
  onLoadNextPage: () -> Unit,
  onItemClick: (User) -> Unit,
  modifier: Modifier = Modifier,
  lazyListState: LazyListState = rememberLazyListState()
) {
  val currentOnLoadNextPage by rememberUpdatedState(onLoadNextPage)
  val currentHasReachedMax by rememberUpdatedState(hasReachedMax)

  LaunchedEffect(lazyListState) {
    snapshotFlow { lazyListState.layoutInfo }
      .throttleTime(
        duration = 300.milliseconds,
        throttleConfiguration = ThrottleConfiguration.LEADING_AND_TRAILING,
      )
      .filter {
        val index = it.visibleItemsInfo.lastOrNull()?.index
        val totalItemsCount = it.totalItemsCount

        Logger.d(
          messageString = "lazyListState: currentHasReachedMax=$currentHasReachedMax " +
            "- lastVisible=$index" +
            " - totalItemsCount=$totalItemsCount",
          tag = UserItemsListLogTag,
        )

        !currentHasReachedMax && index != null &&
          index + 2 >= totalItemsCount
      }
      .collect {
        Logger.d(
          messageString = "load next page",
          tag = UserItemsListLogTag,
        )
        currentOnLoadNextPage()
      }
  }

  LazyColumn(
    modifier = modifier
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    state = lazyListState,
  ) {
    items(
      items = items,
      key = { it.login },
      contentType = { "user-row" },
    ) { item ->
      UserRow(
        modifier = Modifier
          .clickable {
            onItemClick(item)
          }
          .fillParentMaxWidth(),
        item = item,
      )
    }

    when {
      isLoading -> {
        item(contentType = "LoadingIndicator") {
          LoadingIndicator(
            modifier = Modifier.height(128.dp),
          )
        }
      }

      error !== null -> {
        item(contentType = "RetryButton") {
          RetryButton(
            modifier = Modifier.height(128.dp),
            errorMessage = error.message.orEmpty(),
            onRetry = onRetry,
          )
        }
      }

      !hasReachedMax -> {
        item(contentType = "Spacer") {
          Spacer(modifier = Modifier.height(128.dp))
        }
      }
    }
  }
}
