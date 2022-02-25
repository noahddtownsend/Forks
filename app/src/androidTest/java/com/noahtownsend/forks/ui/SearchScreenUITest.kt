package com.noahtownsend.forks.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.noahtownsend.forks.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchScreenUITest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun searchUiIsDisplayed() {
        onView(withId(R.id.search_box)).check(matches(isDisplayed()))
        onView(withId(R.id.search_button)).check(matches(isDisplayed()))
    }

    @Test
    fun searchUiSearches() {
        val searchBox = onView(withId(R.id.search_box))
        searchBox.perform(click())
        searchBox.perform(replaceText("nytimes"))

        onView(withId(R.id.search_button)).perform(click())

        onView(withId(R.id.message)).check(matches(isDisplayed()))
    }
}