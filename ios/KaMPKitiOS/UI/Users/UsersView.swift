import SwiftUI
import shared

struct UsersView: View {
    @State
    var viewModel: UsersViewModel?
    
    @State
    var users: [User_] = []
    
    @State
    var isLoading: Bool = false
    
    var body: some View {
        NavigationView {
            UserList(items: users,
                     isLoading: isLoading,
                     error: nil,
                     hasReachedMax: false,
                     endOfListReached: {
                Task {
                    viewModel?.fetchUsers()
                }
            },
                     onRetry: {
                viewModel?.fetchUsers()
            })
            .navigationBarTitle("Github Users", displayMode: .inline)
            .task {
                let vm = KotlinDependencies.shared.getUsersViewModel()
                await withTaskCancellationHandler {
                    self.viewModel = vm
                    vm.fetchUsers()
                    
                    for await userss in vm.users {
                        self.users = userss
                    }
                    
                    for await isLoadingg in vm.isLoading {
                        self.isLoading = isLoadingg.boolValue
                    }
                } onCancel: {
                    vm.clear()
                    self.viewModel = nil
                }

            }
        }
    }
}

#Preview {
    UsersView()
}
