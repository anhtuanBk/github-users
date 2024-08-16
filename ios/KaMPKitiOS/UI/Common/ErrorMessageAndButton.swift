import Foundation
import SwiftUI
import shared

struct ErrorMessageAndButton: View {
  let error: Error
  let onRetry: () -> Void
  var font: Font? = nil

  var body: some View {
    VStack(alignment: .center) {
        Text(error.localizedDescription)
        .font(font ?? .title3)
        .multilineTextAlignment(.center)
        .padding(10)

      Button("Retry", action: onRetry)
        .buttonStyle(PlainButtonStyle.plain)

      Spacer().frame(height: 10)
    }.frame(maxWidth: .infinity)
  }
}
