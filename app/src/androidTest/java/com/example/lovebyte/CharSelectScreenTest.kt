package com.example.lovebyte

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
        val initialState = LoveByteState(
            cityName = "Boston",
            weatherDescription = "Sunny",
            currentLanguage = ProgrammingLanguage.PYTHON
        )

        composeTestRule.setContent {
            var testState by remember { mutableStateOf(initialState) }

            CharSelectScreen(
                state = testState,
                onCharacterSelected = { newLang ->
                    testState = testState.copy(currentLanguage = newLang)
                },
                onBackPressed = {}
            )
        }

        // 1. Verify Python starts (Uses ContentDescription from Image)
        composeTestRule
            .onNodeWithContentDescription("Python Sprite", ignoreCase = true)
            .assertIsDisplayed()

        // 2. Click Next
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.waitForIdle()

        // 3. Verify Kotlin (Uses Text because of the 'if' fallback in your code)
        composeTestRule
            .onNodeWithText("KOTLIN", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun characterSelection_canCycleBackToPreviousLanguage() {
        val initialState = LoveByteState(
            cityName = "Boston",
            weatherDescription = "Sunny",
            currentLanguage = ProgrammingLanguage.PYTHON
        )

        composeTestRule.setContent {
            var testState by remember { mutableStateOf(initialState) }

            CharSelectScreen(
                state = testState,
                onCharacterSelected = { testState = testState.copy(currentLanguage = it) },
                onBackPressed = {}
            )
        }

        // Move to Kotlin
        composeTestRule.onNodeWithContentDescription("Next").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("KOTLIN", ignoreCase = true).assertIsDisplayed()

        // Move back to Python
        composeTestRule.onNodeWithContentDescription("Prev").performClick()
        composeTestRule.waitForIdle()

        // Assert on the Python Image's content description
        composeTestRule.onNodeWithContentDescription("Python Sprite", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun charSelect_handlesMissingWeatherDataGracefully() {
        val stateWithNoWeather = LoveByteState(
            cityName = "",
            weatherDescription = "Unknown",
            currentLanguage = ProgrammingLanguage.PYTHON
        )

        composeTestRule.setContent {
            CharSelectScreen(
                state = stateWithNoWeather,
                onCharacterSelected = {},
                onBackPressed = {}
            )
        }

        // We found 2 nodes, so we use onAllNodes and pick the first one (usually the header)
        // This avoids the 'Expected at most 1 node but found 2' crash.
        composeTestRule
            .onAllNodesWithText("Python", ignoreCase = true, substring = true)
            .onFirst()
            .assertIsDisplayed()
    }
}