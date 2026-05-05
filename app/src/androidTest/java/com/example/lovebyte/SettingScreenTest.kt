package com.example.lovebyte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.ui.screens.SettingsScreen
import com.example.lovebyte.ui.theme.LoveByteTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    // TEST HELPER
    // creates a default state for settings tests
    private fun baseState(privateMode: Boolean = false) =
        LoveByteState(
            privateModeDefault = privateMode
        )

    // BASIC UI RENDERING
    // ensures core UI elements are visible on screen load

    @Test
    fun settingsScreen_displaysTitle() {
        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = {},
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }

        // verify screen title appears
        composeTestRule
            .onNodeWithText("SETTINGS")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysPrivateModeSection() {
        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = {},
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }

        // verify private mode header
        composeTestRule
            .onNodeWithText("Private Mode by Default")
            .assertIsDisplayed()

        // verify explanation text for accessibility and clarity
        composeTestRule
            .onNodeWithText("Use alternate controls instead of sensors in mini-games.")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysButtons() {
        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = {},
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }

        // ensure settings action buttons are visible
        composeTestRule
            .onNodeWithText("CHANGE STARTING PROFICIENCY")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("REPLAY ONBOARDING")
            .assertIsDisplayed()
    }

    // SWITCH STATE BEHAVIOR
    // ensures UI correctly reflects state of private mode toggle

    @Test
    fun privateModeSwitch_reflectsState_true() {
        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(privateMode = true),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = {},
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }

        // switch should be on when state is true
        composeTestRule
            .onNodeWithTag("private_mode_switch")
            .assertIsOn()
    }

    @Test
    fun privateModeSwitch_reflectsState_false() {
        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(privateMode = false),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = {},
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }

        // should be off when false
        composeTestRule
            .onNodeWithTag("private_mode_switch")
            .assertIsOff()
    }

    @Test
    fun privateModeSwitch_toggle_invokesCallback() {
        var toggledValue: Boolean? = null

        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(false),
                    onPrivateModeChanged = { toggledValue = it },
                    onReplayOnboarding = {},
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }

        // user toggles
        composeTestRule
            .onNodeWithTag("private_mode_switch")
            .performClick()

        // make sure change actually received
        assertTrue(toggledValue == true)
    }

    // BUTTON ACTIONS
    // ensures settings buttons correctly trigger callbacks

    @Test
    fun changeProficiencyButton_invokesCallback() {
        var clicked = false

        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = {},
                    onChangeProficiency = { clicked = true },
                    onBackClicked = {}
                )
            }
        }

        // user clicks change proficiency
        composeTestRule
            .onNodeWithTag("change_proficiency_button")
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun replayOnboardingButton_invokesCallback() {
        var clicked = false

        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = { clicked = true },
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }

        // clicks replay onboarding
        composeTestRule
            .onNodeWithTag("replay_onboarding_button")
            .performClick()

        assertTrue(clicked)
    }

    // PERMISSIONS SECTION
    // ensures informational UI is visible to the user

    @Test
    fun settingsScreen_displaysPermissionLabels() {
        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = {},
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }


        // verify headers
        composeTestRule
            .onNodeWithText("Location Permission")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Activity Sensor Permission")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysPermissionExplanation() {
        composeTestRule.setContent {
            LoveByteTheme {
                SettingsScreen(
                    state = baseState(),
                    onPrivateModeChanged = {},
                    onReplayOnboarding = {},
                    onChangeProficiency = {},
                    onBackClicked = {}
                )
            }
        }

        // verify explanation section
        composeTestRule
            .onNodeWithTag("permission_explanation")
            .assertIsDisplayed()
    }
}