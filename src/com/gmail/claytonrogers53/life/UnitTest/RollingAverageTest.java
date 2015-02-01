package com.gmail.claytonrogers53.life.UnitTest;

import com.gmail.claytonrogers53.life.Util.RollingAverage;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test the functionality in the RollingAverage.
 *
 * Created by Clayton on 31/1/2015.
 */
public class RollingAverageTest {

    /** The allowable difference between floats and doubles. */
    public static final float DELTA = 0.001f;

    @Test
    public void alwaysPasses() {
        assertEquals(1,1);
    }

    @Test
    public void intUsage() {
        RollingAverage<Integer> roll = new RollingAverage<>(5);

        roll.addToPool(1);
        roll.addToPool(1);
        roll.addToPool(2);
        roll.addToPool(3);
        roll.addToPool(3);
        double actual = roll.getAverage();
        double expected = 2.0;
        assertEquals(expected, actual, DELTA);

        roll.addToPool(3);
        roll.addToPool(3);
        roll.addToPool(3);
        actual = roll.getAverage();
        expected = 3.0;
        assertEquals(expected, actual, DELTA);
        actual = roll.getAverage();
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void doubleUsage() {
        RollingAverage<Double> roll = new RollingAverage<>(3);

        roll.addToPool(129.423);
        double actual = roll.getAverage();
        double expected = 129.423;
        assertEquals(expected, actual, DELTA);

        roll.addToPool(232.321);
        actual = roll.getAverage();
        expected = 180.872;
        assertEquals(expected, actual, DELTA);

        roll.addToPool(141.232);
        actual = roll.getAverage();
        expected = 167.65866;
        assertEquals(expected, actual, DELTA);

        roll.addToPool(325.014);
        actual = roll.getAverage();
        expected = 232.8556666;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void invalidSize() {
        int value = 1;
        try {
            RollingAverage<Integer> roll = new RollingAverage<>(1);
        } catch (IllegalArgumentException e) {
            value = 2;
        }
        assertEquals(2, value);
    }
}
