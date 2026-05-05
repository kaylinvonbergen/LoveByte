package com.example.lovebyte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.ui.screens.CharSelectScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharSelectScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    // helpers
    // reusable setup for rendering CharSelectScreen so we don’t repeat boilerplate in every test
    private fun setScreen(
        state: LoveByteState,
        onSelect: (ProgrammingLanguage) -> Unit = {},
        onBack: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            CharSelectScreen(
                state = state,
                onCharacterSelected = onSelect,
                onBackPressed = onBack
            )
        }
    }


    // BASIC RENDERING
    // verifies initial UI state loads correctly



    @Test
    fun initial_python_sprite_is_displayed() {
        setScreen(
            LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON)
        )


        // ensure default language (Python) renders its sprite
        composeTestRule
            .onNodeWithContentDescription("Python Sprite", ignoreCase = true)
            .assertIsDisplayed()
    }


    // PAGER NAVIGATION
    // tests swipe navigation via buttons

    @Test
    fun next_button_switches_to_kotlin() {
        setScreen(
            LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON)
        )

        // move forward in pager
        composeTestRule
            .onNodeWithContentDescription("Next")
            .performClick()

        // kotlin should become visible
        composeTestRule
            .onNodeWithText("KOTLIN", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun prev_button_switches_back_to_python() {
        setScreen(
            LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON)
        )

        // go forward first
        composeTestRule.onNodeWithContentDescription("Next").performClick()

        // then go back
        composeTestRule.onNodeWithContentDescription("Prev").performClick()

        composeTestRule
            .onNodeWithContentDescription("Python Sprite", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun prev_button_is_disabled_on_first_page() {
        setScreen(
            LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON)
        )

        // back nav should be disabled on first page
        composeTestRule
            .onNodeWithContentDescription("Prev")
            .assertIsNotEnabled()
    }

    @Test
    fun next_button_is_disabled_on_last_page() {
        setScreen(
            LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON)
        )

        // navigate to last page (Kotlin is last in our list)
        composeTestRule.onNodeWithContentDescription("Next").performClick()

        composeTestRule
            .onNodeWithContentDescription("Next")
            .assertIsNotEnabled()
    }


    // CALLBACKS
    // ensures UI correctly emits selection events

    @Test
    fun select_button_triggers_character_selection() {
        var selected: ProgrammingLanguage? = null

        setScreen(
            LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
            onSelect = { selected = it }
        )

        // simulate confirming our selection
        composeTestRule
            .onNodeWithTag("select_button")
            .performClick()

        assert(selected != null)
    }

    // INFO DIALOG
    // ensures modal UI opens correctly

    @Test
    fun info_button_opens_dialog() {
        setScreen(
            LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON)
        )

        // open the info modal
        composeTestRule
            .onNodeWithTag("info_button")
            .performClick()

        // verify dialogue content
        composeTestRule
            .onNodeWithText("LOVE")
            .assertIsDisplayed()
    }

    // FALLBACK BEHAVIOR
    // ensures modal UI opens correctly

    @Test
    fun kotlin_page_shows_text_fallback() {
        setScreen(
            LoveByteState(currentLanguage = ProgrammingLanguage.KOTLIN)
        )

        // ensure kotlin uses fallback (since it does not have sprite)
        composeTestRule
            .onNodeWithText("KOTLIN")
            .assertIsDisplayed()
    }


    // EDGE STATE
    // ensures UI does not crash on missing/invalid data

    @Test
    fun handles_missing_weather_data_gracefully() {
        setScreen(
            LoveByteState(
                cityName = "",
                weatherDescription = "Unknown",
                currentLanguage = ProgrammingLanguage.PYTHON
            )
        )

        // UI should still render Python label even if metadata is missing
        composeTestRule
            .onAllNodesWithText("Python", ignoreCase = true, substring = true)
            .onFirst()
            .assertIsDisplayed()
    }
}