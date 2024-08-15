package co.touchlab.kampkit

import co.touchlab.kampkit.data.remote.UserApiImpl
import co.touchlab.kampkit.domain.model.User
import co.touchlab.kampkit.domain.model.UserDetails
import co.touchlab.kampkit.network.createHttpClient
import co.touchlab.kampkit.network.createJson
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.Severity
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserApiTest {
    private val emptyLogger = Logger(
        config = object : LoggerConfig {
            override val logWriterList: List<LogWriter> = emptyList()
            override val minSeverity: Severity = Severity.Assert
        },
        tag = ""
    )

    @Test
    fun getUsersSuccess() = runTest {
        val json = """
                    [{
    "login": "mojombo",
    "id": 1,
    "node_id": "MDQ6VXNlcjE=",
    "avatar_url": "https://avatars.githubusercontent.com/u/1?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/mojombo",
    "html_url": "https://github.com/mojombo",
    "followers_url": "https://api.github.com/users/mojombo/followers",
    "following_url": "https://api.github.com/users/mojombo/following{/other_user}",
    "gists_url": "https://api.github.com/users/mojombo/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/mojombo/subscriptions",
    "organizations_url": "https://api.github.com/users/mojombo/orgs",
    "repos_url": "https://api.github.com/users/mojombo/repos",
    "events_url": "https://api.github.com/users/mojombo/events{/privacy}",
    "received_events_url": "https://api.github.com/users/mojombo/received_events",
    "type": "User",
    "site_admin": false
  }]
                """.trimIndent()

        val mockUsers = listOf(
            User(
                "mojombo",
                "https://avatars.githubusercontent.com/u/1?v=4",
                "https://github.com/mojombo"
            )
        )

        val engine = MockEngine {
            respond(
                content = json,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }

        val httpClient = createHttpClient(
            engine,
            createJson(),
            emptyLogger
        )

        val userApi = UserApiImpl(
            Url("mock.com"),
            emptyLogger,
            httpClient
        )

        val result = userApi.getUsers(0)
        assertEquals(
            mockUsers,
            result
        )
    }

    @Test
    fun getUserDetailsSuccess() = runTest {
        val json = """
                    {
                      "login": "mojombo",
                      "id": 1,
                      "node_id": "MDQ6VXNlcjE=",
                      "avatar_url": "https://avatars.githubusercontent.com/u/1?v=4",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/mojombo",
                      "html_url": "https://github.com/mojombo",
                      "followers_url": "https://api.github.com/users/mojombo/followers",
                      "following_url": "https://api.github.com/users/mojombo/following{/other_user}",
                      "gists_url": "https://api.github.com/users/mojombo/gists{/gist_id}",
                      "starred_url": "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
                      "subscriptions_url": "https://api.github.com/users/mojombo/subscriptions",
                      "organizations_url": "https://api.github.com/users/mojombo/orgs",
                      "repos_url": "https://api.github.com/users/mojombo/repos",
                      "events_url": "https://api.github.com/users/mojombo/events{/privacy}",
                      "received_events_url": "https://api.github.com/users/mojombo/received_events",
                      "type": "User",
                      "site_admin": false,
                      "name": "Tom Preston-Werner",
                      "company": "@chatterbugapp, @redwoodjs, @preston-werner-ventures ",
                      "blog": "http://tom.preston-werner.com",
                      "location": "San Francisco",
                      "email": null,
                      "hireable": null,
                      "bio": null,
                      "twitter_username": "mojombo",
                      "public_repos": 66,
                      "public_gists": 62,
                      "followers": 23983,
                      "following": 11,
                      "created_at": "2007-10-20T05:24:19Z",
                      "updated_at": "2024-07-14T20:45:58Z"
                    }
                """.trimIndent()

        val mockUserDetails = UserDetails(
            "mojombo",
            "https://avatars.githubusercontent.com/u/1?v=4",
            "https://github.com/mojombo",
            "San Francisco",
            23983,
            11
        )

        val engine = MockEngine {
            respond(
                content = json,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }

        val httpClient = createHttpClient(
            engine,
            createJson(),
            emptyLogger
        )

        val userApi = UserApiImpl(
            Url("mock.com"),
            emptyLogger,
            httpClient
        )

        val result = userApi.getUserDetails("")
        assertEquals(
            mockUserDetails,
            result
        )
    }

    @Test
    fun failure() = runTest {
        val engine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }

        val httpClient = createHttpClient(
            engine,
            createJson(),
            emptyLogger
        )

        val userApi = UserApiImpl(
            Url("mock.com"),
            emptyLogger,
            httpClient
        )

        assertFailsWith<ClientRequestException> {
            userApi.getUsers(1)
        }

        assertFailsWith<ClientRequestException> {
            userApi.getUserDetails("")
        }
    }
}
