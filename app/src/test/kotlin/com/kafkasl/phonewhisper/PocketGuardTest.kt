package com.kafkasl.phonewhisper

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PocketGuardTest {

    @Test
    fun `hidden when screen turns off even before keyguard locks`() {
        assertFalse(WhisperAccessibilityService.overlayVisible(Intent.ACTION_SCREEN_OFF, keyguardLocked = false))
    }

    @Test
    fun `hidden on lock screen after screen turns on`() {
        assertFalse(WhisperAccessibilityService.overlayVisible(Intent.ACTION_SCREEN_ON, keyguardLocked = true))
    }

    @Test
    fun `shown after unlock`() {
        assertTrue(WhisperAccessibilityService.overlayVisible(Intent.ACTION_USER_PRESENT, keyguardLocked = false))
    }

    @Test
    fun `shown on service start when unlocked, hidden when locked`() {
        assertTrue(WhisperAccessibilityService.overlayVisible(null, keyguardLocked = false))
        assertFalse(WhisperAccessibilityService.overlayVisible(null, keyguardLocked = true))
    }

    @Test
    fun `recording limit is 15 minutes`() {
        assertEquals(15 * 60 * 1000L, WhisperAccessibilityService.MAX_RECORDING_MS)
    }
}
