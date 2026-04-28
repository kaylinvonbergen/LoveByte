package com.example.lovebyte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.ui.screens.CharSelectScreen

@RunWith(AndroidJUnit4::class)
class CharSelectScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun characterSelection_updatesState_andDisplaysCorrectSprite() {
        // 1. setup mock state
        val mockState = LoveByteState(
            cityName = "Boston",
            weatherDescription = "Sunny",
            currentLanguage = ProgrammingLanguage.PYTHON
        )

        // 2. set UI Content
        composeTestRule.setContent {
            CharSelectScreen(
                state = mockState,
                onCharacterSelected = {},
                onBackPressed = {}
            )
        }


        // 3. verify initial state (weather greeting :3")
        composeTestRule
            .onNodeWithText("It's sunny in Boston! Do some Python!", ignoreCase = true)
            .assertIsDisplayed()

        // 4. interaction: click the "next" arrow to see Kotlin
        composeTestRule
            .onNodeWithContentDescription("Next")
            .performClick()

        // 5. Assertion: Check if the Sprite box updated
        composeTestRule
            .onNodeWithText("Kotlin Sprite", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun characterSelection_canCycleBackToPreviousLanguage() {
        // 1. start with Python
        val mockState = LoveByteState(
            cityName = "Boston",
            weatherDescription = "Sunny",
            currentLanguage = ProgrammingLanguage.PYTHON
        )

        composeTestRule.setContent {
            CharSelectScreen(state = mockState, onCharacterSelected = {}, onBackPressed = {})
        }

        // 2. move forward to Kotlin
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.waitForIdle()

        // verify we are actually on Kotlin before testing the "Back" button
        composeTestRule.onNodeWithText("Kotlin Sprite", ignoreCase = true).assertIsDisplayed()

        // 3. move backward to Python!
        composeTestRule.onNodeWithContentDescription("Prev").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Python Sprite", ignoreCase = true).assertIsDisplayed()
    }
}