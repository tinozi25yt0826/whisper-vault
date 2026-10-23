package com.kafkasl.phonewhisper

import android.app.ApplicationExitInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagnosticsTest {

    @Test fun `known exit reasons get readable labels`() {
        assertEquals("killed: low memory", Diagnostics.exitReasonLabel(ApplicationExitInfo.REASON_LOW_MEMORY))
        assertEquals("crash (native)", Diagnostics.exitReasonLabel(ApplicationExitInfo.REASON_CRASH_NATIVE))
    }

    @Test fun `unknown exit reason keeps its number`() {
        assertEquals("other (99)", Diagnostics.exitReasonLabel(99))
    }

    @Test fun `crash description names the exception and where it happened`() {
        val text = Diagnostics.describeCrash(IllegalStateException("boom"))
        assertTrue(text, text.lines().first().endsWith("java.lang.IllegalStateException: boom"))
        assertTrue(text, text.lines()[1].startsWith("  at "))
    }
}
