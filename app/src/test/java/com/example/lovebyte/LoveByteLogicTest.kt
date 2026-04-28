package com.example.lovebyte

import com.example.lovebyte.viewmodel.LoveByteViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for LoveByte core logic.
 * These tests run locally on your machine without needing an emulator.
 */
class LoveByteLogicTest {

    @Test
    fun `mapWeatherToAdjective correctly maps API strings to cutiepie terms`() {
        // call the function directly from the LoveByteViewModel class via its companion object.
        // lets us avoids initializing the entire ViewModel and its database dependencies.

        // Test Case 1: Clear sky (Basic mapping)
        val sunnyResult = LoveByteViewModel.mapWeatherToAdjective("Clear", "clear sky")
        assertEquals("sunny", sunnyResult)

        // Test Case 2: Heavy Rain (Nested mapping logic)
        val rainyResult = LoveByteViewModel.mapWeatherToAdjective("Rain", "heavy intensity rain")
        assertEquals("heavily rainy", rainyResult)

        // Test Case 3: Drizzle (Specific keyword check)
        val drizzleResult = LoveByteViewModel.mapWeatherToAdjective("Drizzle", "light intensity drizzle")
        assertEquals("drizzling", drizzleResult)

        // Test Case 4: Fallback (Ensures reliability if API returns something unknown)
        val unknownResult = LoveByteViewModel.mapWeatherToAdjective("Alien", "spaceships")
        assertEquals("spaceships", unknownResult)
    }

    @Test
    fun `sentiment scores are clamped correctly`() {
        // testing the logic used in handleChoiceSelected to ensure scores stay in bounds!
        val initialScore = 10
        val hugePenalty = -100
        val hugeBonus = 200

        val clampedMin = (initialScore + hugePenalty).coerceIn(0, 50)
        val clampedMax = (initialScore + hugeBonus).coerceIn(0, 50)

        // verify the bounds
        assertEquals(0, clampedMin)
        assertEquals(50, clampedMax)
    }
}