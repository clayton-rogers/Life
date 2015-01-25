package com.gmail.claytonrogers53.life.Physics;

/**
 * A thing which can be added to the physics system.
 *
 * Created by Clayton on 8/12/2014.
 */
public interface PhysicsThing {
    /**
     * The method is called once every physics loop when the object is registered with the physics system. It is
     * expected that an object which implements this interface will be able to propagate in some way in time, and is
     * expected to do so when this method is called.
     *
     * @param deltaT
     *        The time step size in s.
     */
    void stepPhysics (double deltaT);
}
