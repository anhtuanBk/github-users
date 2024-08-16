import Foundation
import SwiftUI
import shared

struct UserList: View {
  let items: [User_]
  let isLoading: Bool
  let error: Error?
  let hasReachedMax: Bool

  let endOfListReached: () -> Void
  let onRetry: () -> Void

  var body: some View {
    List {
      ForEach(items) { item in
          NavigationLink(destination: LazyView(UserDetailsView(login: item.login))) {
          UserRow(
            item: item
          ).listRowInsets(.init())
        }
      }

      if isLoading {
        HStack(alignment: .center) {
          ProgressView("Loading...")
        }.frame(maxWidth: .infinity)
      } else if let error = error {
        ErrorMessageAndButton(
          error: error,
          onRetry: onRetry,
          font: .subheadline
        )
      } else if !items.isEmpty, !hasReachedMax {
        Rectangle()
          .size(width: 0, height: 0)
          .onAppear(perform: endOfListReached)
      }
    }.listStyle(.plain)
  }
}
