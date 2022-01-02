package com.example.postory;
import com.example.postory.utils.HourMinuteHandler;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HourMinuteHandlerTest {
    String hourString1 = "01";
    int hourInt1 = 1;
    String minuteString1 = "04";
    int minuteInt1 = 4;


    String hourString2 = "11";
    int hourInt2 = 11;
    String minuteString2 = "32";
    int minuteInt2 = 32;

    @Test
    public void checkIntTo2DigitString(){
        assertEquals(hourString1, HourMinuteHandler.intToString(hourInt1));
        assertEquals(hourString2,HourMinuteHandler.intToString(hourInt2));
        assertEquals(minuteString1,HourMinuteHandler.intToString(minuteInt1));
        assertEquals(minuteString2,HourMinuteHandler.intToString(minuteInt2));
    }
    @Test
    public void checkCombine(){
        assertEquals("01:04",HourMinuteHandler.combine(hourInt1,minuteInt1));
        assertEquals("01:04",HourMinuteHandler.combine(hourInt1,minuteString1));
        assertEquals("01:04",HourMinuteHandler.combine(hourString1,minuteInt1));
        assertEquals("01:04",HourMinuteHandler.combine(hourString1,minuteString1));

        assertEquals("11:32",HourMinuteHandler.combine(hourInt2,minuteInt2));
        assertEquals("11:32",HourMinuteHandler.combine(hourInt2,minuteString2));
        assertEquals("11:32",HourMinuteHandler.combine(hourString2,minuteInt2));
        assertEquals("11:32",HourMinuteHandler.combine(hourString2,minuteString2));
    }

}
