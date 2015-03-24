package com.gmail.claytonrogers53.life.Physics;

import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.Vector2D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a physical point on a 2D plane. The point can have mass/linear momentum, but does not have a rotation or
 * any other angular properties.
 *
 * Created by Clayton on 8/12/2014.
 */
public abstract class PhysicsObject implements Collidable {
    protected double mass;
    protected double momentOfInertia;
    protected double collisionRadius = 0.0;

    protected State state     = new State();
    protected State nextState = new State();
    private boolean isNextStateValid = false;
    protected Collection<Vector2D> collisionPoints = new ArrayList<>();

    protected boolean isCollidable = false;
    protected boolean isCollisionResolutionEnabled = true;

    /**
     * Creates a new PhysicsObject. This abstract class has everything required to track and propagate linear and
     * angular momentum.
     *
     * @param mass
     *        The mass (linear inertia) of the point object in kg.
     *
     * @param momentOfInertia
     *        The moment of inertia of the object around the axis normal to the plane of the screen in kg*m.
     */
    public PhysicsObject(double mass, double momentOfInertia) {
        this.mass             = mass;
        this.momentOfInertia  = momentOfInertia;
    }
    
    @Override
    public abstract boolean isIntersecting(Vector2D vertex);

    @Override
    public Collection<Vector2D> getCollisionPoints() {
        return collisionPoints;
    }

    @Override
    public void applyNextState() {

        if (!isNextStateValid) {
            String errorString = "Tried to apply the next state when it is not valid.";
            Log.error(errorString);
            throw new IllegalStateException(errorString);
        }
        // Simply swap the current and next state since the next
        State temp = state;
        state = nextState;
        nextState = temp;
        isNextStateValid = false;
    }

    @Override
    public void calculateNextState(double time) {
        nextState.position = state.position.add(state.velocity.scalarMultiply(time));
        nextState.velocity.set(state.velocity);
        nextState.angle = state.angle + state.angularVelocity * time;
        nextState.angularVelocity = state.angularVelocity;
        isNextStateValid = true;
    }

    @Override
    public double getCollisionRadius() {
        return collisionRadius;
    }

    @Override
    public State getNextState() {
        if (!isNextStateValid) {
            String errorString = "Tried to get the next state when it was not valid.";
            Log.error(errorString);
            throw new IllegalStateException(errorString);
        }
        return nextState;
    }

    @Override
    public boolean isCollisionsEnabled() {
        return isCollidable;
    }

    @Override
    public boolean isCollisionResolutionEnabled() {
        return isCollisionResolutionEnabled;
    }
}
