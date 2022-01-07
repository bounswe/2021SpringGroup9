package com.example.postory;
import com.example.postory.utils.PasswordController;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author niyaziulke */

/*
    ID            : TC_A_2
    Description   : Tests whether PasswordController detects passwords with insufficient strength.
 */
public class PasswordControllerTest {
    String shortPass = "A1b2_2";
    String noCapital = "prk32lxxx234_aa*";
    String noDigit = "a*B*c*qwerty_javakotlin";
    String noSpecial = "aB12cDefKlmno2341";
    String valid = "tHis_PassWord_iSs_Super_2021";

    PasswordController shortController = new PasswordController(shortPass);
    PasswordController noCapitalController = new PasswordController(noCapital);
    PasswordController noDigitController = new PasswordController(noDigit);
    PasswordController noSpecialController = new PasswordController(noSpecial);
    PasswordController validController = new PasswordController(valid);

    @Test
    public void checkLengthControllerFunction(){
        assertFalse(shortController.lengthEnough());
        assertTrue(noCapitalController.lengthEnough());
        assertTrue(noDigitController.lengthEnough());
        assertTrue(noSpecialController.lengthEnough());
        assertTrue(validController.lengthEnough());
    }

    @Test
    public void checkCapitalControllerFunction(){
        assertTrue(shortController.containsCapital());
        assertFalse(noCapitalController.containsCapital());
        assertTrue(noDigitController.containsCapital());
        assertTrue(noSpecialController.containsCapital());
        assertTrue(validController.containsCapital());
    }

    @Test
    public void checkNumberControllerFunction(){
        assertTrue(shortController.containsNumber());
        assertTrue(noCapitalController.containsNumber());
        assertFalse(noDigitController.containsNumber());
        assertTrue(noSpecialController.containsNumber());
        assertTrue(validController.containsNumber());
    }
    @Test
    public void checkSpecialControllerFunction(){
        assertTrue(shortController.containsSpecial());
        assertTrue(noCapitalController.containsSpecial());
        assertTrue(noDigitController.containsSpecial());
        assertFalse(noSpecialController.containsSpecial());
        assertTrue(validController.containsSpecial());
    }
    @Test
    public void checkAllControllerFunction(){
        assertFalse(shortController.allControls());
        assertFalse(noCapitalController.allControls());
        assertFalse(noDigitController.allControls());
        assertFalse(noSpecialController.allControls());
        assertTrue(validController.allControls());
    }
}