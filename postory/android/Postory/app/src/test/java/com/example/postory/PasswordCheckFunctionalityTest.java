package com.example.postory;
import com.example.postory.utils.PasswordController;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.postory.activities.RegisterActivity;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PasswordCheckFunctionalityTest {
    String shortPass = "A1b2_2";
    String noCapital = "prk32lxxx234_aa";
    String noDigit = "a*B*c*qwerty_javakotlin";
    String valid = "tHis_PassWord_iSs_Super_2021";

    PasswordController shortController = new PasswordController(shortPass);
    PasswordController noCapitalController = new PasswordController(noCapital);
    PasswordController noDigitController = new PasswordController(noDigit);
    PasswordController validController = new PasswordController(valid);

    @Test
    public void checkLengthControllerFunction(){
        assertFalse(shortController.lengthEnough());
        assertTrue(noCapitalController.lengthEnough());
        assertTrue(noDigitController.lengthEnough());
        assertTrue(validController.lengthEnough());
    }

    @Test
    public void checkCapitalControllerFunction(){
        assertTrue(shortController.containsCapital());
        assertFalse(noCapitalController.containsCapital());
        assertTrue(noDigitController.containsCapital());
        assertTrue(validController.containsCapital());
    }

    @Test
    public void checkNumberControllerFunction(){
        assertTrue(shortController.containsNumber());
        assertTrue(noCapitalController.containsNumber());
        assertFalse(noDigitController.containsNumber());
        assertTrue(validController.containsNumber());
    }

    @Test
    public void checkAllControllerFunction(){
        assertFalse(shortController.allControls());
        assertFalse(noCapitalController.allControls());
        assertFalse(noDigitController.allControls());
        assertTrue(validController.allControls());
    }
}