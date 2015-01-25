package com.gmail.claytonrogers53.life.Physics;

import java.util.ArrayList;
import java.util.List;

/**
 * The basics 2D physics object from which implements all the standard physics code. Takes the physicsPoint and adds
 * angular properties and angular property propagation.
 *
 * Created by Clayton on 13/11/2014.
 */
public abstract class PhysicsObject2D extends PhysicsPoint2D {

    protected double angle;
    protected double angularVelocity;
    protected double angularAcceleration;

    protected double momentOfInertia;

    protected List<Double> moments = new ArrayList<>();

    /**
     * Create a new physics object with the given parameters.
     *
     * @param mass
     *        The mass of the object in kg.
     *
     * @param momentOfInertia
     *        The moment of inertia of the object around the axis normal to the plane of the screen in kg*m.
     *
     * @param position
     *        The initial position vector of the object in m.
     *
     * @param velocity
     *        The initial velocity vector of the object in m/s.
     *
     * @param angle
     *        The initial rotation angle of the object in rad, clockwise from North.
     *
     * @param angularVelocity
     *        The initial angular velocity of the object in rad/s, positive is clockwise.
     */
    public PhysicsObject2D(double mass, double momentOfInertia, Vector2D position, Vector2D velocity, double angle, double angularVelocity) {
        super(mass, position, velocity);

        this.momentOfInertia     = momentOfInertia;
        this.angle               = angle;
        this.angularVelocity     = angularVelocity;
        this.angularAcceleration = 0.0;
    }

    /**
     * This is the fundamental method which is called by the physics system every loop.
     *
     * @param deltaT
     *        The time step size to simulate in seconds.
     */
    @Override
    public void stepPhysics (double deltaT) {
        // All of the linear forces are taken care of in the super
        super.stepPhysics(deltaT);

        // Angular motion
        double totalMoments = 0.0;
        for (double moment : moments) {
            totalMoments += moment;
        }
        angularAcceleration = totalMoments / momentOfInertia;
        angularVelocity += angularAcceleration * deltaT;
        angle += angularVelocity * deltaT;

        // Clear the moments for the next iteration.
        moments.clear();
    }
}
