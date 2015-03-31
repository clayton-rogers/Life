package com.gmail.claytonrogers53.life.Physics;

import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.Vector2D;

/**
 * A struct to hold to state of a physics object.
 *
 * Created by Clayton on 19/03/2015.
 */
public class State {
    public State() {}
//    public State(State other) {
//        this.angle           = other.angle;
//        this.angularVelocity = other.angularVelocity;
//        this.position        = new Vector2D(other.position);
//        this.velocity        = new Vector2D(other.velocity);
//    }
    public Vector2D position = new Vector2D();
    public Vector2D velocity = new Vector2D();
    public double angle = 0.0;
    public double angularVelocity = 0.0;

    /**
     * Converts a position in local object coordinates to world position coordinates using the
     * object's state.
     *
     * @param localPosition
     *        The position in local coordinates (m)
     *
     * @param state
     *        The state of the object (contains position and angle data).
     *
     * @return A new vector containing the position in world coordinates.
     */
    public static Vector2D convertToWorldCoordinates(Vector2D localPosition, State state) {
        if (state == null){
            String errorString = "Tried to convert a vector using a null state.";
            Log.error(errorString);
            throw new IllegalStateException(errorString);
        }
        // This finds the direction of the local vector in world coordinates.
        double localVectorDirection = localPosition.getDirection() + state.angle;
        return state.position
                .add(
                        Vector2D.getVector2DMagnitudeAndDirection(localPosition.getMag(), localVectorDirection)
                );

    }

    /**
     * Converts a world position into a local position given a state.
     *
     * @param worldPosition
     *        A position in world coordinates (m).
     *
     * @param state
     *        The state of the object to which we wish to convert the coordinate system.
     *
     * @return A new vector containing the position in local coordinates.
     */
    public static Vector2D convertToLocalCoordinates(Vector2D worldPosition, State state) {

        Vector2D offsetFromPosition = worldPosition.sub(state.position);

        double localAngle = offsetFromPosition.getDirection() - state.angle;

        return Vector2D.getVector2DMagnitudeAndDirection(offsetFromPosition.getMag(), localAngle);
    }
}
