package com.example.lovebyte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.ui.screens.HomeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // TEST HELPER
    // reduces repetition when rendering HomeScreen with different states

    private fun setHome(state: LoveByteState,
                        onContinue: () -> Unit = {},
                        onSwap: () -> Unit = {},
                        onSettings: () -> Unit = {},
                        onNext: () -> Unit = {},
                        onPlacement: (Int, Int) -> Unit = { _, _ -> },
                        onFinish: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            HomeScreen(
                state = state,
                onContinueClicked = onContinue,
                onSwapClicked = onSwap,
                onSettingsClicked = onSettings,
                onOnboardingNext = onNext,
                onOnboardingPlacementComplete = onPlacement,
                onOnboardingFinish = onFinish
            )
        }
    }

    // ONBOARDING STEP 1
    // verifies that the welcome screen is displayed correctly

    @Test
    fun onboarding_step1_displays_welcome_and_next() {
        setHome(
            LoveByteState(
                shouldShowOnboarding = true,
                onboardingStep = 1
            )
        )

        // ensure welcome message is visible
        composeTestRule.onNodeWithText("Welcome to LoveByte!", ignoreCase = true)
            .assertIsDisplayed()

        // ensure user can proceed
        composeTestRule.onNodeWithText("Next")
            .assertIsDisplayed()
    }

    // ONBOARDING STEP 2
    // verifies language proficiency selection UI is shown

    @Test
    fun onboarding_step2_shows_proficiency_rows() {
        setHome(
            LoveByteState(
                shouldShowOnboarding = true,
                onboardingStep = 2
            )
        )

        // python row should be present
        composeTestRule.onNodeWithTag("Python_Row")
            .assertIsDisplayed()

        // Python row should be present
        composeTestRule.onNodeWithTag("Kotlin_Row")
            .assertIsDisplayed()
    }

    // ONBOARDING STEP 3
    // verifies placement results screen and interactions
    @Test
    fun onboarding_step3_shows_placement_dialog() {
        setHome(
            LoveByteState(
                shouldShowOnboarding = true,
                onboardingStep = 3,
                progressMap = mapOf(
                    ProgrammingLanguage.PYTHON to 2,
                    ProgrammingLanguage.KOTLIN to 3
                )
            )
        )

        // placement summary dialog should appear
        composeTestRule.onNodeWithText("Starting Placement")
            .assertIsDisplayed()
    }

    @Test
    fun onboarding_step3_next_button_triggers_callback() {
        var nextCalled = false

        setHome(
            LoveByteState(
                shouldShowOnboarding = true,
                onboardingStep = 3,
                progressMap = mapOf(
                    ProgrammingLanguage.PYTHON to 2,
                    ProgrammingLanguage.KOTLIN to 3
                )
            ),
            onNext = { nextCalled = true }
        )

        // user confirms placement and proceeds
        composeTestRule.onNodeWithText("Next").performClick()

        assert(nextCalled)
    }

    // ONBOARDING STEP 4
    // verifies sensor explanation screen and final onboarding action

    @Test
    fun onboarding_step4_shows_sensor_info() {
        setHome(
            LoveByteState(
                shouldShowOnboarding = true,
                onboardingStep = 4
            )
        )

        // ensure sensor explanation dialog is shown
        composeTestRule.onNodeWithText("Mini-Game Sensors")
            .assertIsDisplayed()
    }

    @Test
    fun onboarding_step4_finish_triggers_callback() {
        var finished = false

        setHome(
            LoveByteState(
                shouldShowOnboarding = true,
                onboardingStep = 4
            ),
            onFinish = { finished = true }
        )

        // user completes onboarding
        composeTestRule.onNodeWithText("Okay").performClick()

        assert(finished)
    }

    // MAIN DASHBOARD STATES
    // tests correct UI depending on user progress state
    @Test
    fun dashboard_shows_continue_when_language_selected() {
        setHome(
            LoveByteState(
                shouldShowOnboarding = false,
                currentLanguage = ProgrammingLanguage.PYTHON
            )
        )

        // if progress, cont button appears
        composeTestRule.onNodeWithText("CONTINUE")
            .assertIsDisplayed()
    }

    @Test
    fun dashboard_shows_start_when_no_language_selected() {
        setHome(
            LoveByteState(
                shouldShowOnboarding = false,
                currentLanguage = ProgrammingLanguage.NONE
            )
        )

        // if no progress, start button
        composeTestRule.onNodeWithText("START")
            .assertIsDisplayed()
    }

    @Test
    fun dashboard_shows_python_sprite_and_progress_text() {
        setHome(
            LoveByteState(
                shouldShowOnboarding = false,
                currentLanguage = ProgrammingLanguage.PYTHON,
                progressMap = mapOf(ProgrammingLanguage.PYTHON to 3)
            )
        )

        // char sprite should render
        composeTestRule.onNodeWithContentDescription("Python Sprite")
            .assertIsDisplayed()

        // progress text should match chapter state
        composeTestRule.onNodeWithText("PYTHON: CHAPTER 3", ignoreCase = true)
            .assertIsDisplayed()
    }

    // BUTTON CALLBACKS
    // verifies UI buttons correctly trigger navigation/actions

    @Test
    fun continue_button_triggers_callback() {
        var clicked = false

        setHome(
            LoveByteState(
                shouldShowOnboarding = false,
                currentLanguage = ProgrammingLanguage.PYTHON
            ),
            onContinue = { clicked = true }
        )

        composeTestRule.onNodeWithText("CONTINUE").performClick()

        assert(clicked)
    }

    @Test
    fun swap_button_triggers_callback() {
        var swapped = false

        setHome(
            LoveByteState(
                shouldShowOnboarding = false,
                currentLanguage = ProgrammingLanguage.PYTHON
            ),
            onSwap = { swapped = true }
        )

        composeTestRule.onNodeWithText("SWAP ROUTES").performClick()

        assert(swapped)
    }

    @Test
    fun settings_button_is_visible() {
        setHome(
            LoveByteState(
                shouldShowOnboarding = false,
                currentLanguage = ProgrammingLanguage.PYTHON
            )
        )

        // settings icon should always exist on dashboard
        composeTestRule.onNodeWithContentDescription("Settings")
            .assertIsDisplayed()
    }

    @Test
    fun settings_button_triggers_callback() {
        var settingsClicked = false

        setHome(
            LoveByteState(
                shouldShowOnboarding = false,
                currentLanguage = ProgrammingLanguage.PYTHON
            ),
            onSettings = { settingsClicked = true }
        )

        composeTestRule.onNodeWithContentDescription("Settings")
            .performClick()

        assert(settingsClicked)
    }
}