package com.gmail.claytonrogers53.life.Physics;

import java.util.ArrayList;
import java.util.List;

/**
 * The basics 2D physics object from which implements all the standard physics code.
 *
 * Created by Clayton on 13/11/2014.
 */
public abstract class PhysicsObject2D {

    protected Vector2D position = new Vector2D();
    protected Vector2D deltaPosition = new Vector2D();
    protected Vector2D velocity = new Vector2D();
    protected Vector2D deltaVelocity = new Vector2D();
    protected Vector2D acceleration = new Vector2D();
    protected double angle;
    protected double angularVelocity;
    protected double angularAcceleration;

    protected double mass;
    protected double momentOfInertia;

    protected List<Vector2D> forces = new ArrayList<>();
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
     */
    public PhysicsObject2D(double mass, double momentOfInertia, Vector2D position, Vector2D velocity) {
        this.mass = mass;
        this.momentOfInertia = momentOfInertia;
        this.position = new Vector2D(position);
        this.velocity = new Vector2D(velocity);
    }

    /**
     * This is the fundamental method which is called by the physics system every loop.
     *
     * @param deltaT
     *        The time step size to simulate in seconds.
     */
    final void stepPhysics (double deltaT) {

        Vector2D totalForce = new Vector2D();
        double totalMoments;

        // Let the object add all the forces and moments than it needs
        calculatePhysics(deltaT);

        // Linear motion
        // TODO: see if there is a better way of doing the following.
        Vector2D.getSumOfVectors(forces, totalForce);
        acceleration.set(totalForce);
        acceleration.scalarDivide(mass);

        deltaVelocity.set(acceleration);
        deltaVelocity.scalarMultiply(deltaT);
        velocity.add(deltaVelocity);

        deltaPosition.set(velocity);
        deltaPosition.scalarMultiply(deltaT);
        position.add(deltaPosition);

        // The physics object is responsible for adding the forces and moments every loop,
        // so the current forces and moments are cleared.
        forces.clear();

        // Angular motion
        totalMoments = 0.0;
        for (double moment : moments) {
            totalMoments += moment;
        }
        angularAcceleration = totalMoments / momentOfInertia;
        angularVelocity += angularAcceleration * deltaT;
        angle += angularVelocity * deltaT;

        moments.clear();
    }

    /**
     * The method is called once for every physics time step and allows the user to add any physics they need.
     *
     * @param deltaT
     *        The time step size to simulate in seconds.
     */
    protected abstract void calculatePhysics (double deltaT);
}
