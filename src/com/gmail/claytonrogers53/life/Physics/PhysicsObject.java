package com.gmail.claytonrogers53.life.Physics;

import java.util.List;
import java.util.Vector;

/**
 * The basics physics object from which all specific physics objects inherit.
 * Created by Clayton on 13/11/2014.
 */
public abstract class PhysicsObject {

    protected Vector2D position = new Vector2D();
    protected Vector2D deltaPosition = new Vector2D();
    protected Vector2D velocity = new Vector2D();
    protected Vector2D deltaVelocity = new Vector2D();
    protected Vector2D acceleration = new Vector2D();
    protected double mass;
    protected double momentOfInertia;
    protected List<Vector2D> forces = new Vector<Vector2D>();
    protected Vector2D totalForce = new Vector2D();

    /**
     * Allows the physics system to get the mass of the object.
     * @return The mass of the object.
     */
    double getMass() {
        return mass;
    }

    /**
     * Allows the physics system to get the moment of inertia of the object.
     * @return The moment of inertia of the object.
    */
    double getMomentOfInertia() {
        return momentOfInertia;
    }

    public PhysicsObject(double mass, double momentOfInertia, Vector2D position, Vector2D velocity) {
        this.mass = mass;
        this.momentOfInertia = momentOfInertia;
        this.position = new Vector2D(position);
        this.velocity = new Vector2D(velocity);
    }

    void stepPhysics (double deltaT) {
        Vector2D.getSumOfVectors(forces, totalForce);

        acceleration.set(totalForce);
        acceleration.scalarDivide(mass);

        deltaVelocity.set(acceleration);
        deltaVelocity.scalarMultiply(deltaT);
        velocity.add(deltaVelocity);

        deltaPosition.set(velocity);
        deltaPosition.scalarMultiply(deltaT);
        position.add(deltaPosition);
    }
}
