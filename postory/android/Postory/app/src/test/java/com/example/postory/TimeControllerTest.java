package com.example.postory;
import com.example.postory.utils.TimeController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.sql.Time;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author niyaziulke */

/*
    ID            : TC_A_3
    Description   : Tests whether TimeController works correctly
 */
public class TimeControllerTest {
    TimeController t1 = new TimeController(1999,2000);
    TimeController t2 = new TimeController(1999,1999,1,1);
    TimeController t3 = new TimeController(1999,1992,2,1,3,7);
    TimeController t4 = new TimeController(1945,1965,1,2,13,15,20,21,12,13);

    /**
     * Check if precision levels are correctly initialized.
     */
    @Test
    public void checkPrecision(){
        assertEquals(t1.getPrecision(), TimeController.YEAR_PRECISION);
        assertEquals(t2.getPrecision(), TimeController.MONTH_PRECISION);
        assertEquals(t3.getPrecision(), TimeController.DAY_PRECISION);
        assertEquals(t4.getPrecision(), TimeController.TIME_PRECISION);
    }

    /**
     * Check if precision levels are unique.
     */
    @Test
    public void checkUniquePrecisionLevels(){
        assertNotEquals(TimeController.YEAR_PRECISION, TimeController.MONTH_PRECISION);
        assertNotEquals(TimeController.YEAR_PRECISION, TimeController.DAY_PRECISION);
        assertNotEquals(TimeController.YEAR_PRECISION, TimeController.TIME_PRECISION);

        assertNotEquals(TimeController.MONTH_PRECISION, TimeController.DAY_PRECISION);
        assertNotEquals(TimeController.MONTH_PRECISION, TimeController.TIME_PRECISION);

        assertNotEquals(TimeController.DAY_PRECISION, TimeController.TIME_PRECISION);
    }

    /**
     * Check if validity function works correctly.
     */
    @Test
    public void checkValidity(){
        t1.createDate();
        t2.createDate();
        t3.createDate();
        t4.createDate();

        assertTrue(t1.checkValidity());
        assertTrue(t2.checkValidity());
        assertFalse(t3.checkValidity());
        assertTrue(t4.checkValidity());
    }

}