package com.example.lovebyte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.ui.screens.SettingsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun baseState(
        privateMode: Boolean = false
    ) = LoveByteState(
        privateModeDefault = privateMode
    )

    // ─────────────────────────────────────────────
    // BASIC UI
    // ─────────────────────────────────────────────

    @Test
    fun settingsScreen_displaysTitle() {
        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(),
                onPrivateModeChanged = {},
                onReplayOnboarding = {},
                onChangeProficiency = {}
            )
        }

        composeTestRule.onNodeWithText("SETTINGS").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysPrivateModeSection() {
        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(),
                onPrivateModeChanged = {},
                onReplayOnboarding = {},
                onChangeProficiency = {}
            )
        }

        composeTestRule.onNodeWithText("Private Mode by Default").assertIsDisplayed()
        composeTestRule.onNodeWithText("Use alternate controls instead of sensors in mini-games.").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysButtons() {
        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(),
                onPrivateModeChanged = {},
                onReplayOnboarding = {},
                onChangeProficiency = {}
            )
        }

        composeTestRule.onNodeWithText("CHANGE STARTING PROFICIENCY").assertIsDisplayed()
        composeTestRule.onNodeWithText("REPLAY ONBOARDING").assertIsDisplayed()
    }

    // ─────────────────────────────────────────────
    // SWITCH BEHAVIOR
    // ─────────────────────────────────────────────

    @Test
    fun privateModeSwitch_reflectsState_true() {
        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(privateMode = true),
                onPrivateModeChanged = {},
                onReplayOnboarding = {},
                onChangeProficiency = {}
            )
        }

        composeTestRule
            .onNodeWithTag("private_mode_switch")
            .assertIsOn()
    }

    @Test
    fun privateModeSwitch_reflectsState_false() {
        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(privateMode = false),
                onPrivateModeChanged = {},
                onReplayOnboarding = {},
                onChangeProficiency = {}
            )
        }

        composeTestRule
            .onNodeWithTag("private_mode_switch")
            .assertIsOff()
    }

    @Test
    fun privateModeSwitch_toggle_invokesCallback() {
        var toggledValue: Boolean? = null

        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(false),
                onPrivateModeChanged = { toggledValue = it },
                onReplayOnboarding = {},
                onChangeProficiency = {}
            )
        }

        composeTestRule
            .onNodeWithTag("private_mode_switch")
            .performClick()

        assertTrue(toggledValue == true)
    }

    // ─────────────────────────────────────────────
    // BUTTON ACTIONS
    // ─────────────────────────────────────────────

    @Test
    fun changeProficiencyButton_invokesCallback() {
        var clicked = false

        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(),
                onPrivateModeChanged = {},
                onReplayOnboarding = {},
                onChangeProficiency = { clicked = true }
            )
        }

        composeTestRule
            .onNodeWithTag("change_proficiency_button")
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun replayOnboardingButton_invokesCallback() {
        var clicked = false

        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(),
                onPrivateModeChanged = {},
                onReplayOnboarding = { clicked = true },
                onChangeProficiency = {}
            )
        }

        composeTestRule
            .onNodeWithTag("replay_onboarding_button")
            .performClick()

        assertTrue(clicked)
    }

    // ─────────────────────────────────────────────
    // PERMISSIONS TEXT
    // ─────────────────────────────────────────────

    @Test
    fun settingsScreen_displaysPermissionLabels() {
        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(),
                onPrivateModeChanged = {},
                onReplayOnboarding = {},
                onChangeProficiency = {}
            )
        }

        composeTestRule.onNodeWithText("Location Permission").assertIsDisplayed()
        composeTestRule.onNodeWithText("Activity Sensor Permission").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysPermissionExplanation() {
        composeTestRule.setContent {
            SettingsScreen(
                state = baseState(),
                onPrivateModeChanged = {},
                onReplayOnboarding = {},
                onChangeProficiency = {}
            )
        }

        composeTestRule
            .onNodeWithText("LoveByte uses location", substring = true)
            .assertIsDisplayed()
    }
}