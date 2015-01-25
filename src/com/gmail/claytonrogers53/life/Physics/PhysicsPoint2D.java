package com.gmail.claytonrogers53.life.Physics;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical point on a 2D plane. The point can have mass/linear momentum, but does not have a rotation or
 * any other angular properties.
 *
 * Created by Clayton on 8/12/2014.
 */
public abstract class PhysicsPoint2D implements PhysicsThing {
    protected Vector2D position;
    protected Vector2D velocity;
    protected Vector2D acceleration;

    protected double mass;

    protected List<Vector2D> forces = new ArrayList<>();

    /**
     * Creates a new PhysicsPoint2D with the given properties. The 2D physics point implements linear physics, but not
     * angular physics. For angular physics, use PhysicsObject2D
     *
     * @param mass
     *        The mass (linear inertia) of the point object in kg.
     *
     * @param position
     *        The initial position of the object in m.
     *
     * @param velocity
     *        The initial velocity of the object in m/s.
     *
     * @see com.gmail.claytonrogers53.life.Physics.PhysicsObject2D
     */
    public PhysicsPoint2D (double mass, Vector2D position, Vector2D velocity) {
        this.mass         = mass;
        this.position     = new Vector2D(position);
        this.velocity     = new Vector2D(velocity);
        this.acceleration = new Vector2D();
    }

    /**
     * This is the fundamental method which is called by the physics system every loop.
     *
     * @param deltaT
     *        The time step size to simulate in seconds.
     */
    @Override
    public void stepPhysics (double deltaT) {

        // Let the object add all the forces and moments than it needs
        calculatePhysics(deltaT);

        // Linear motion
        Vector2D totalForce = Vector2D.getSumOfVectors(forces);
        acceleration = totalForce.scalarDivide(mass);

        velocity = velocity.add(acceleration.scalarMultiply(deltaT));

        position = position.add(velocity.scalarMultiply(deltaT));

        // The physics object is responsible for adding the forces and moments every loop,
        // so the current forces and moments are cleared.
        forces.clear();
    }


    /**
     * The method is called once for every physics time step and allows the user to add any physics they need.
     *
     * @param deltaT
     *        The time step size to simulate in seconds.
     */
    protected abstract void calculatePhysics (double deltaT);
}
