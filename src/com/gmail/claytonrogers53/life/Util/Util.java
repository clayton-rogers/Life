package com.gmail.claytonrogers53.life.Util;

/**
 * Util class for random functions that don't go anywhere else.
 *
 * Created by Clayton on 22/03/2015.
 */
public final class Util {

    private Util() {
    }

    /**
     * Takes any angle and converts it to the range 0 - 2PI.
     *
     * @param angle
     *        An angle in radians.
     *
     * @return The same angle in radians but in the specified range.
     */
    public static double normaliseAngle (double angle) {

        double returnAngle = angle;

        while (returnAngle > Math.PI * 2.0) {
            returnAngle -= Math.PI * 2.0;
        }
        while (returnAngle < 0.0) {
            returnAngle += Math.PI * 2.0;
        }

        return returnAngle;
    }
}
