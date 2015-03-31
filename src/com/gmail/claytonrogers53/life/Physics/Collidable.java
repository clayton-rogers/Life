package com.gmail.claytonrogers53.life.Physics;

import com.gmail.claytonrogers53.life.Util.Vector2D;

import java.util.Collection;

/**
 * All physics objects which can collide with other physics objects must implement this interface.
 *
 * Created by Clayton on 8/2/2015.
 */
public interface Collidable {

    /**
     * Gets the smallest circle, centred on the centre of mass, that the object can fit inside.
     * Used for broad phase collision detection.
     *
     * @return The smallest radius in m which can fully enclose the collidable object.
     */
    public double getCollisionRadius();

    /**
     * Returns the next state of the object.
     *
     * Implementation note: It is a reference to the actual state, not a copy, so it should not be
     * modified, unless that's what you want.
     *
     * @return A reference to the next (proposed) state of the object.
     */
    public State getNextState();

    /**
     * Returns whether collision detection should be applied to this object. If an object is only
     * conditionally collidable then it may wish to occasionally return false from this function to
     * this. Additionally, an object may extend {@link PhysicsObject} in order to have the linear
     * and angular propagation but may not need the collisions.
     *
     * @return True when collision detection and resolution should be applied to this object.
     */
    public boolean isCollisionsEnabled();

    /**
     * Checks whether a particular point is intersecting with the object. Coordinates are in local
     * reference frame.
     *
     * @param vertex
     *        The position of the intruding vertex in local coordinates (m).
     *
     * @return True when the vertex is withing the bounds of this object.
     */
    public boolean isIntersecting(Vector2D vertex);

    /**
     * Gives the points (in local coordinates) which this object can possibly collide with another
     * object.
     *
     * @return The list of collision points.
     */
    public Collection<Vector2D> getCollisionPoints();

    /**
     * Moves the next state into the current state. Called when the final next state is know after
     * all collisions are resolved.
     */
    public void applyNextState();

    /**
     * Updates the next state to be the calculated state 'time' seconds after the current state.
     *
     * @param time
     *        The time (in seconds) past the current state to calculate to.
     */
    public void calculateNextState(double time);

    /**
     * Tells the physics/collision system whether to resolve collisions with this object, or to
     * simply notify it when a collision occurs.
     *
     * @return True when collision system should resolve the collision.
     */
    public boolean isCollisionResolutionEnabled();

    /**
     * The method is always called when the collision system detects that a collision has between
     * two collidable objects that have collisions turned on
     * (i.e. {@link #isCollisionsEnabled()} returns true).
     *
     * @param otherObject
     *        A reference to the other object involved in the collision.
     *
     * @param wasCollisionResolved
     *        True when the collision system has resolved the collision and has updated the state
     *        of both the objects.
     */
    public void notifyCollision(Collidable otherObject, boolean wasCollisionResolved);
}
