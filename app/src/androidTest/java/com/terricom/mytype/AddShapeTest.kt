package com.terricom.mytype

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddShapeTest {

    private lateinit var stringToBetyped: String

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)

    @Before
    fun initValidString() {
        // Specify a valid string.
        stringToBetyped = "5"
    }

    @Test
    fun testFAB() {

        Thread.sleep(3000)
        onView(withId(R.id.fab_custom_pic))
            .check(matches(isDisplayed()))
            .perform(click())
    }

    @Test
    fun addShape(){

        onView(withId(R.id.fab_custom_pic))
            .check(matches(isDisplayed()))
            .perform(click())

        Thread.sleep(1000)
        onView(withId(R.id.fab2))
            .check(matches(isDisplayed()))
            .perform(click())

        // Type text and then press the button.
        onView(withId(R.id.number_weight))
            .perform(ViewActions.typeText(stringToBetyped), ViewActions.closeSoftKeyboard())

        // Check that the text was changed.
        onView(withId(R.id.number_weight))
            .check(matches(ViewMatchers.withText("$stringToBetyped.0")))

        onView(withId(R.id.number_body_fat))
            .perform(ViewActions.typeText(stringToBetyped), ViewActions.closeSoftKeyboard())

        // Check that the text was changed.
        onView(withId(R.id.number_body_fat))
            .check(matches(ViewMatchers.withText("$stringToBetyped.0")))

        onView(withId(R.id.number_body_water))
            .perform(ViewActions.typeText(stringToBetyped), ViewActions.closeSoftKeyboard())

        // Check that the text was changed.
        onView(withId(R.id.number_body_water))
            .check(matches(ViewMatchers.withText("$stringToBetyped.0")))

        onView(withId(R.id.number_tdee))
            .perform(ViewActions.typeText(stringToBetyped), ViewActions.closeSoftKeyboard())

        // Check that the text was changed.
        onView(withId(R.id.number_tdee))
            .check(matches(ViewMatchers.withText("$stringToBetyped.0")))

        onView(withId(R.id.number_muscle))
            .perform(ViewActions.typeText(stringToBetyped), ViewActions.closeSoftKeyboard())

        // Check that the text was changed.
        onView(withId(R.id.number_muscle))
            .check(matches(ViewMatchers.withText("$stringToBetyped.0")))

        onView(withId(R.id.number_body_age))
            .perform(ViewActions.typeText(stringToBetyped), ViewActions.closeSoftKeyboard())

        // Check that the text was changed.
        onView(withId(R.id.number_body_age))
            .check(matches(ViewMatchers.withText("$stringToBetyped.0")))

        onView(withId(R.id.button_shaperecord_save))
            .perform(click())

        Thread.sleep(2000)
        onView(withId(R.id.layout_message))
            .perform(click())

        Thread.sleep(2000)

    }

}