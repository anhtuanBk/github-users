import SwiftUI
import Kingfisher
import shared

struct UserDetailsView: View {
    let login: String
    
    @State
    var viewModel: UserDetailsViewModel?
    
    @State
    var userDetails: UserDetails_?
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 16) {
                HStack {
                    KFAnimatedImage(URL(string: userDetails?.avatarUrl ?? "google.com"))
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
                        Text(userDetails?.login ?? "")
                            .font(.headline)
                            .lineLimit(2)
                            .truncationMode(.tail)
                        
                        Spacer().frame(height: 10)
                        
                        HStack {
                            Image(systemName: "mappin.and.ellipse")
                                .foregroundColor(.gray)
                            Text(userDetails?.location ?? "")
                                .font(.subheadline)
                                .foregroundColor(.gray)
                        }
                        
                        Spacer().frame(height: 10)
                    }.padding([.bottom, .trailing, .top])
                    Spacer()
                }
                .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/, alignment: .center)
                
                HStack(alignment: .center, spacing: 50) {
                    Spacer()
                    VStack {
                        Image(systemName: "person.2.fill")
                            .font(.system(size: 28))
                            .foregroundColor(.black)
                        Text("\(userDetails?.followers ?? 0)+")
                            .font(.headline)
                        Text("Follower")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                    }
                    
                    VStack {
                        Image(systemName: "person.fill")
                            .font(.system(size: 28))
                            .foregroundColor(.black)
                        Text("\(userDetails?.following ?? 0)+")
                            .font(.headline)
                        Text("Following")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                    }
                    Spacer()
                }
                .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/, alignment: .center)
                .padding(.vertical, 16)
                
                VStack(alignment: .leading) {
                    Text("Blog")
                        .font(.title3)
                        .fontWeight(.semibold)
                        .padding(.bottom, 4)
                    Text(userDetails?.htmlUrl ?? "")
                        .font(.subheadline)
                        .foregroundColor(.blue)
                }
                .padding(.horizontal, 16)
                Spacer()
            }
            Spacer()
        }
        .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/, maxHeight: .infinity)
        .padding()
        .navigationBarTitle("User Details", displayMode: .inline)
        .task {
            let vm = KotlinDependencies.shared.getUserDetailsViewModel()
            await withTaskCancellationHandler {
                self.viewModel = vm
                
                Task {
                    try? await vm.fetchUserDetails(login: login)
                }
                
                for await userDetailss in vm.userDetails.filter({ item in
                    item?.login == self.login
                }) {
                    self.userDetails = userDetailss
                }
            } onCancel: {
                vm.clear()
                self.viewModel = nil
            }
        }
    }
}
