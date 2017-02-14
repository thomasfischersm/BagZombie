package com.playposse.heavybagzombie.activity;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link ManualFightSetupActivity}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ManualFightSetupActivityTest {

    @Test
    public void getValidComboString() {
        // Verify empty strings.
        assertValidCombo("", "");
        assertValidCombo(" ", "");
        assertValidCombo("   ", "");
        assertValidCombo("   \t\n", "");
        assertValidCombo("   \t\n;,adsfasdf", "");

        // Verify single character input.
        assertValidCombo("1", "1");
        assertValidCombo("11111", "1");
        assertValidCombo("   1   ", "1");
        assertValidCombo("7", "");
        assertValidCombo("71a;\t\n  ", "1");

        // Verify multi-letter combinations.
        assertValidCombo("1 2 3 4", "1 2 3 4");
        assertValidCombo("1;2;3;4", "1 2 3 4");
        assertValidCombo("1, 2, 3, 4", "1 2 3 4");
        assertValidCombo("1, 2, 3, 4", "1 2 3 4");

        // Random input.
        assertValidCombo("1asdf asdfgghh%^&*(2", "1 2");

        // Test max length
        assertValidCombo("1, 2ASDFADSFADSF, 3, 4, 6, 7", "1 2 3 4");
    }

    private static void assertValidCombo(String input, String expectedOutput) {
        String actualOutput = ManualFightSetupActivity.getValidComboString(input);
        assertEquals(expectedOutput, actualOutput);
    }
}
