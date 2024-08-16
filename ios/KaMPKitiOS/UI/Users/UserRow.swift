import Foundation
import SwiftUI
import shared
import Kingfisher
import UIKit

struct UserRow: View {
  let item: User_

  var body: some View {
    HStack {
        KFAnimatedImage(URL(string: item.avatarUrl))
        .configure { view in view.framePreloadCount = 3 }
        .cacheOriginalImage()
        .placeholder { p in ProgressView(p) }
        .fade(duration: 1)
        .forceTransition()
        .aspectRatio(contentMode: .fill)
        .frame(width: 72, height: 72)
        .cornerRadius(20)
        .shadow(radius: 5)
        .frame(width: 92, height: 92)

      VStack(alignment: .leading) {
          Text(item.login)
          .font(.headline)
          .lineLimit(2)
          .truncationMode(.tail)

        Spacer().frame(height: 10)

        Text(item.htmlUrl)
          .font(.subheadline)
          .lineLimit(2)
          .truncationMode(.tail)

        Spacer().frame(height: 10)
      }.padding([.bottom, .trailing, .top])
    }
  }
}
