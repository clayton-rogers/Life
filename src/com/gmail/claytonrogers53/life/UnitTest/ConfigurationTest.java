package com.gmail.claytonrogers53.life.UnitTest;

import com.gmail.claytonrogers53.life.Util.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test code for the {@link com.gmail.claytonrogers53.life.Util.Configuration} package.
 *
 * Created by Clayton on 9/12/2014.
 */
public class ConfigurationTest {

    /** The allowable difference between floats and doubles. */
    public static final float DELTA = 0.001f;

    @Test
    public void alwaysPasses() {
        assertEquals(1, 1);
    }

    @Test
    public void localFile() {
        Configuration.loadConfigurationItems();

        // Does not exist values.
        double actualDouble = Configuration.getValueDouble("DOES_NOT_EXIST", 2.0);
        double expectedDouble = 2.0;
        assertEquals(expectedDouble, actualDouble, DELTA);

        int actualInt = Configuration.getValueInt("DOES_INT_EXIT", 22);
        int expectedInt = 22;
        assertEquals(expectedInt, actualInt);

        // Does exist values.
        actualDouble = Configuration.getValueDouble("PHYSICS_MULTIPLIER", 1.0);
        expectedDouble = 1.0;
        assertEquals(expectedDouble, actualDouble, DELTA);

        actualInt = Configuration.getValueInt("PHYSICS_DT", 154);
        expectedInt = 10;
        assertEquals(expectedInt, actualInt);
    }

    @Test
    public void testDataFile() {
        Configuration.loadConfigurationItems("./testdata/test_norm.conf");

        // Does not exist values.
        double actualDouble = Configuration.getValueDouble("DOES_NOT_EXIST", 2.0);
        double expectedDouble = 2.0;
        assertEquals(expectedDouble, actualDouble, DELTA);

        int actualInt = Configuration.getValueInt("DOES_INT_EXIST", 79);
        int expectedInt = 79;
        assertEquals(expectedInt, actualInt);

        String actualString = Configuration.getValue("STRING_EXITS", "DEFAULT");
        String expectedString = "DEFAULT";
        assertEquals(expectedString, actualString);

        // Does exist values.
        actualDouble = Configuration.getValueDouble("DOUBLE_VALUE", 1.0);
        expectedDouble = 24.6;
        assertEquals(expectedDouble, actualDouble, DELTA);

        actualInt = Configuration.getValueInt("INT_VALUE", 985);
        expectedInt = 27;
        assertEquals(expectedInt, actualInt);

        actualInt = Configuration.getValueInt("NEG_INT", 985);
        expectedInt = -87;
        assertEquals(expectedInt, actualInt);

        actualString = Configuration.getValue("STRING_VALUE", "DEFAULT_B");
        expectedString = "things are here";
        assertEquals(expectedString, actualString);
    }

    @Test
    public void doubleConfigured() {
        Configuration.loadConfigurationItems("./testdata/test_broken.conf");

        // Still one good value.
        int actualInt = Configuration.getValueInt("NEG_INT", 985);
        int expectedInt = -87;
        assertEquals(expectedInt, actualInt);

        // Test that the other ones don't come in.
        actualInt = Configuration.getValueInt("INT_VALUE", 985);
        expectedInt = 985;
        assertEquals(expectedInt, actualInt);
    }

    @Test
    public void fileDoesNotExist() {
        // Load a good one and test that it works.
        Configuration.loadConfigurationItems("./testdata/test_norm.conf");

        double actualDouble = Configuration.getValueDouble("DOUBLE_VALUE", 1.0);
        double expectedDouble = 24.6;
        assertEquals(expectedDouble, actualDouble, DELTA);

        // And test that it doesn't after.
        Configuration.loadConfigurationItems("./testdata/file_does_not_exist.conf");

        actualDouble = Configuration.getValueDouble("DOUBLE_VALUE", 1.0);
        expectedDouble = 1.0;
        assertEquals(expectedDouble, actualDouble, DELTA);
    }

    @Test
    public void badValues() {
        Configuration.loadConfigurationItems("./testdata/test_norm.conf");

        double actualDouble = Configuration.getValueDouble("INT_VALUE", 1.0);
        double expectedDouble = 27.0;
        assertEquals(expectedDouble, actualDouble, DELTA);

        actualDouble = Configuration.getValueDouble("STRING_VALUE", 1.0);
        expectedDouble = 1.0;
        assertEquals(expectedDouble, actualDouble, DELTA);

        int actualInt = Configuration.getValueInt("DOUBLE_VALUE", 985);
        int expectedInt = 985;
        assertEquals(expectedInt, actualInt);

        String actualString = Configuration.getValue("NEG_INT", "DEFAULT_B");
        String expectedString = "-87";
        assertEquals(expectedString, actualString);
    }
}
