package com.gmail.claytonrogers53.life.UnitTest;

import com.gmail.claytonrogers53.life.Util.Log;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test code for the {@link com.gmail.claytonrogers53.life.Util.Log} class.
 *
 * Created by Clayton on 9/12/2014.
 */
public class LogTest {

    @Test
    public void alwaysPasses() {
        assertEquals(1,1);
    }

    @Test
    public void initAndSetLogLevel() {
        Log.init("unit_test_log.log");

        Log.setLogLevel("DEBUG");
        Log.warning("warning");
        Log.info("does not log");

        Log.setLogLevel("VERBOSE");
        Log.verbose("verbose");

        Log.setLogLevel("INFO");
        Log.verbose("does not log");
        Log.info("info");

        Log.setLogLevel("WARNING");
        Log.info("does not log");
        Log.warning("warning");

        Log.setLogLevel("ERROR");
        Log.warning("does not log");
        Log.error("error");
        Log.debug("debug");

        Log.setLogLevel("UNKNOWN_LOG_LEVEL");
        Log.info("does not log");
        Log.warning("warning");

        Log.setLogLevel(Log.LogLevel.DEBUG);
        Log.warning("warning");
        Log.info("does not log");

        Log.setLogLevel(Log.LogLevel.VERBOSE);
        Log.verbose("verbose");

        Log.setLogLevel(Log.LogLevel.INFO);
        Log.verbose("does not log");
        Log.info("info");

        Log.setLogLevel(Log.LogLevel.WARNING);
        Log.info("does not log");
        Log.warning("warning");

        Log.setLogLevel(Log.LogLevel.ERROR);
        Log.warning("does not log");
        Log.error("error");
        Log.debug("debug");

        Log.init();
    }
}
