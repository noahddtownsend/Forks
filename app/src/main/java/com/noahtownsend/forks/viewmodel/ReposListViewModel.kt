package com.noahtownsend.forks.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.noahtownsend.forks.Repo
import com.noahtownsend.forks.internet.GitHubApi
import com.noahtownsend.forks.internet.InternetUtils
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.FileNotFoundException

class ReposListViewModel : ViewModel() {

    var orgName: String = ""
        set(value) {
            field = value.trim()
            getOrgInfo(value)
        }

    private val _orgAvatar: MutableLiveData<Bitmap> = MutableLiveData()
    val orgAvatar: LiveData<Bitmap>
        get() = _orgAvatar

    private val _fullOrgName: MutableLiveData<String> = MutableLiveData()
    val fullOrgName: LiveData<String>
        get() = _fullOrgName

    private val _orgDescription: MutableLiveData<String> = MutableLiveData()
    val orgDescription: LiveData<String>
        get() = _orgDescription

    private val _isInvalidOrg = MutableLiveData<Boolean>(false)
    val isInvalidOrg: LiveData<Boolean>
        get() = _isInvalidOrg

    private val _isNetworkIssue = MutableLiveData<Boolean>(false)
    val isNetworkIssue: LiveData<Boolean>
        get() = _isNetworkIssue

    private val _orgHasNoRepos = MutableLiveData<Boolean>(false)
    val orgHasNoRepos: LiveData<Boolean>
        get() = _orgHasNoRepos

    fun getReposForOrg(orgName: String): LiveData<List<Repo>> {
        val result = MutableLiveData<List<Repo>>()

        viewModelScope.launch {
            try {
                val reposArray = GitHubApi.getReposForOrg(orgName)

                var firstRepo: Repo? = null
                var secondRepo: Repo? = null
                var thirdRepo: Repo? = null

                for (i in 0 until reposArray.size()) {
                    val repoJson = reposArray[i] as JsonObject
                    val repoStars = repoJson.get("stargazers_count").asInt

                    if (firstRepo == null || repoStars > firstRepo.numStars) {
                        thirdRepo = secondRepo
                        secondRepo = firstRepo
                        firstRepo = repoFromJson(repoJson)
                    } else if (secondRepo == null || repoStars > secondRepo.numStars) {
                        thirdRepo = secondRepo
                        secondRepo = repoFromJson(repoJson)
                    } else if (thirdRepo == null || repoStars > thirdRepo.numStars) {
                        thirdRepo = repoFromJson(repoJson)
                    }
                }

                result.value = ArrayList<Repo>().apply {
                    if (firstRepo != null) add(firstRepo)
                    if (secondRepo != null) add(secondRepo)
                    if (thirdRepo != null) add(thirdRepo)
                }

                if ((result.value as ArrayList<Repo>).isEmpty()) {
                    _orgHasNoRepos.value = true
                }
            } catch (e: FileNotFoundException) {
                Log.e("FileNotFound", e.toString())
                _isInvalidOrg.value = true
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
                _isNetworkIssue.value = true
            }
        }

        return result
    }

    private fun repoFromJson(repoJson: JsonObject): Repo {
        val description =
            if (repoJson.get("description") !is JsonNull) repoJson.get("description").asString else ""
        return Repo(
            repoJson.get("html_url").asString,
            repoJson.get("name").asString,
            description,
            repoJson.get("stargazers_count").asInt
        )
    }

    private fun getOrgInfo(orgName: String) {
        viewModelScope.launch {
            try {
                val orgInfo = JSONObject(GitHubApi.getOrgInfo(orgName))
                _fullOrgName.value = orgInfo.getString("name")
                _orgDescription.value = orgInfo.getString("description")
                _orgAvatar.value = InternetUtils.getBitmapFromURL(orgInfo.getString("avatar_url"))
            } catch (e: FileNotFoundException) {
                _isInvalidOrg.value = true
            } catch (e: Exception) {
                _isNetworkIssue.value = true
            }
        }
    }
}