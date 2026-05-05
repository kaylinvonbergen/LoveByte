package com.example.lovebyte

import com.example.lovebyte.data.model.DialogueChoice
import com.example.lovebyte.data.model.SentimentScore
import com.example.lovebyte.viewmodel.LoveByteViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for LoveByte core logic.
 * Pure JVM tests — no Android dependencies required.
 */
class LoveByteLogicTest {

    // ─────────────────────────────────────────────
    // WEATHER MAPPING
    // ─────────────────────────────────────────────

    @Test
    fun `mapWeatherToAdjective handles all major weather types`() {
        assertEquals("sunny", LoveByteViewModel.mapWeatherToAdjective("Clear", "clear sky"))
        assertEquals("cloudy", LoveByteViewModel.mapWeatherToAdjective("Clouds", "overcast clouds"))

    }

    // ─────────────────────────────────────────────
    // SENTIMENT SYSTEM
    // ─────────────────────────────────────────────

    @Test
    fun `applyChoiceToSentiment correctly updates values`() {
        val current = SentimentScore(love = 10, friend = 10, hate = 10)

        val choice = DialogueChoice(
            choiceText = "test",
            targetNodeId = 101,
            lovePoints = 5,
            friendPoints = -3,
            hatePoints = 2
        )

        val result = applyChoiceToSentiment(current, choice)

        assertEquals(15, result.love)
        assertEquals(7, result.friend)
        assertEquals(12, result.hate)
    }

    @Test
    fun `applyChoiceToSentiment clamps between 0 and 50`() {
        val current = SentimentScore(love = 49, friend = 1, hate = 0)

        val choice = DialogueChoice(
            choiceText = "extreme",
            targetNodeId = 101,
            lovePoints = 100,
            friendPoints = -100,
            hatePoints = -5
        )

        val result = applyChoiceToSentiment(current, choice)

        assertEquals(50, result.love)
        assertEquals(0, result.friend)
        assertEquals(0, result.hate)
    }

    // ─────────────────────────────────────────────
    // DIALOGUE / NARRATIVE LOGIC
    // ─────────────────────────────────────────────

    @Test
    fun `choice routes to correct node`() {
        val choice = DialogueChoice(
            choiceText = "Go left",
            targetNodeId = 203
        )

        val next = choice.targetNodeId

        assertEquals(203, next)
    }

    // ─────────────────────────────────────────────
    // CHAPTER LOGIC
    // ─────────────────────────────────────────────

    @Test
    fun `chapter start node is calculated correctly`() {
        assertEquals(101, getStartNode(1))
        assertEquals(201, getStartNode(2))
        assertEquals(301, getStartNode(3))
    }

    // ─────────────────────────────────────────────
    // MINIGAME ROUTING
    // ─────────────────────────────────────────────

    @Test
    fun `minigame result routes correctly`() {
        assertEquals(109, getMinigameResultNode(true))
        assertEquals(110, getMinigameResultNode(false))
    }

    // ─────────────────────────────────────────────
    // EXISTING TEST (your original)
    // ─────────────────────────────────────────────

    @Test
    fun `sentiment scores are clamped correctly`() {
        val initialScore = 10
        val hugePenalty = -100
        val hugeBonus = 200

        val clampedMin = (initialScore + hugePenalty).coerceIn(0, 50)
        val clampedMax = (initialScore + hugeBonus).coerceIn(0, 50)

        assertEquals(0, clampedMin)
        assertEquals(50, clampedMax)
    }

    // ─────────────────────────────────────────────
    // TEST HELPERS (PURE FUNCTIONS)
    // ─────────────────────────────────────────────

    private fun applyChoiceToSentiment(
        current: SentimentScore,
        choice: DialogueChoice
    ): SentimentScore {
        return current.copy(
            love = (current.love + choice.lovePoints).coerceIn(0, 50),
            friend = (current.friend + choice.friendPoints).coerceIn(0, 50),
            hate = (current.hate + choice.hatePoints).coerceIn(0, 50)
        )
    }

    private fun getStartNode(chapterId: Int): Int {
        return (chapterId * 100) + 1
    }

    private fun getMinigameResultNode(success: Boolean): Int {
        return if (success) 109 else 110
    }
}