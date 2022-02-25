package com.noahtownsend.forks.internet

import com.google.gson.JsonArray
import com.google.gson.JsonParser

class GitHubApi {
    companion object {
        private const val GITHUB_TOKEN = "<REPLACE_WITH_TOKEN>"

        private const val BASE_URL = "https://api.github.com"
        private const val ORGS_ENDPOINT = "orgs"
        private const val REPOS_ENDPOINT = "repos"

        private const val PAGE_NAV_HEADER = "link"

        val AUTH_HEADERS = HashMap<String, String>().apply {
            put("Authorization", "Bearer $GITHUB_TOKEN")
        }

        suspend fun getReposForOrg(orgName: String, pageNum: Int = 1): JsonArray {
            val response = InternetUtils.get(
                "$BASE_URL/$ORGS_ENDPOINT/$orgName/$REPOS_ENDPOINT?per_page=100&page=$pageNum",
                AUTH_HEADERS
            )

            var result = JsonParser.parseString(response.first).asJsonArray

            if (response.second.containsKey(PAGE_NAV_HEADER)) {
                for (link in (response.second[PAGE_NAV_HEADER] as List<String>)[0].split(",")) {
                    if (link.contains("next")) {
                        val nextPageNum = link.split("&page=")[1].split(">")[0].toInt()
                        val nextPage = getReposForOrg(orgName, nextPageNum)

                        nextPage.addAll(result)
                        result = nextPage
                    }
                }
            }

            return result
        }

        suspend fun getOrgInfo(orgName: String): String {
            return InternetUtils.get("$BASE_URL/$ORGS_ENDPOINT/$orgName", AUTH_HEADERS).first
        }
    }
}