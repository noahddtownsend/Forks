package com.noahtownsend.forks.internet

import com.noahtownsend.forks.constants.*
import io.mockk.coEvery
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.FileNotFoundException

class GitHubApiTest {

    @Before
    fun setup() = runBlocking {
        mockkObject(InternetUtils)
    }

    @Test
    fun getReposForOrg_returnSuccess() = runBlocking {
        coEvery {
            InternetUtils.Companion.get(
                "${REPOS_URL}1",
                any()
            )
        } returns Pair<String, Map<String, List<String>>>(FIRST_PAGE, HashMap())

        val repos = GitHubApi.getReposForOrg(ORG_NAME, 1)

        Assert.assertEquals(
            FIRST_PAGE,
            repos.toString()
        )
    }

    @Test
    fun getReposForOrg_multiPage_returnSuccess() = runBlocking {
        val headersWithNext = HashMap<String, List<String>>().apply {
            put(
                "link",
                ArrayList<String>().apply { add("<https://api.github.com/organizations/221409/repos?per_page=100&page=0>; rel=\"prev\", <https://api.github.com/organizations/221409/repos?per_page=100&page=2>; rel=\"next\", <https://api.github.com/organizations/221409/repos?per_page=100&page=3>; rel=\"last\", <https://api.github.com/organizations/221409/repos?per_page=100&page=0>; rel=\"first\"") }
            )
        }

        coEvery {
            InternetUtils.Companion.get(
                "${REPOS_URL}1",
                any()
            )
        } returns Pair<String, Map<String, List<String>>>(FIRST_PAGE, headersWithNext)

        coEvery {
            InternetUtils.Companion.get(
                "${REPOS_URL}2",
                any()
            )
        } returns Pair<String, Map<String, List<String>>>(SECOND_PAGE, HashMap())

        val repos = GitHubApi.getReposForOrg(ORG_NAME, 1)


        Assert.assertEquals(
            ALL_PAGES_EXPECTED,
            repos.toString()
        )
    }

    @Test(expected = FileNotFoundException::class)
    fun getReposForOrg_throws404(): Unit = runBlocking {
        coEvery {
            InternetUtils.Companion.get(
                "${REPOS_URL}1",
                any()
            )
        } throws FileNotFoundException()

        GitHubApi.getReposForOrg(ORG_NAME, 1)
    }

    @Test
    fun getOrgInfo_returnSuccess() = runBlocking {
        coEvery {
            InternetUtils.Companion.get(
                any(),
                any()
            )
        } returns Pair(ORG_INFO, HashMap())

        val orgInfo = GitHubApi.getOrgInfo(ORG_NAME)
        Assert.assertEquals(ORG_INFO, orgInfo)
    }


}