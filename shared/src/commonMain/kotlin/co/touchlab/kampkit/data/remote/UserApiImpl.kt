package co.touchlab.kampkit.data.remote

import co.touchlab.kampkit.domain.model.User
import co.touchlab.kampkit.domain.model.UserDetails
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.path

class UserApiImpl(
    private val baseUrl: Url,
    private val log: Logger,
    private val httpClient: HttpClient
) : UserApi {
    override suspend fun getUsers(page: Int, perPage: Int): List<User> = httpClient.get(
        URLBuilder(baseUrl)
            .apply {
                path("users")
                parameters.append("page", page.toString())
                parameters.append("per_page", perPage.toString())
            }
            .build()
    ).body<List<User>>()

    override suspend fun getUserDetails(login: String): UserDetails = httpClient.get(
        URLBuilder(baseUrl)
            .apply {
                path("users/$login")
            }
            .build()
    ).body<UserDetails>()
}
