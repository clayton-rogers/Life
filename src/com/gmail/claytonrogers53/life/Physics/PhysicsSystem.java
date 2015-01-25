package com.gmail.claytonrogers53.life.Physics;

import java.util.ArrayList;
import java.util.List;

/**
 * The physics system keeps track of all of the physics objects and updates them once every physics loop.
 * Created by Clayton on 13/11/2014.
 */
public class PhysicsSystem implements Runnable {
    List<PhysicsObject> physicsList = new ArrayList<PhysicsObject>();
    private volatile boolean isPhysicsRunning = true;

    private long physics_dt;

    public PhysicsSystem() {
        this(17);
    }

    public PhysicsSystem(long physics_dt) {
        this.physics_dt = physics_dt;
    }

    private void physicsLoop() {
        double dt = physics_dt;
        long endOfLastLoopTime = System.currentTimeMillis();
        long timeToWait;

        while (isPhysicsRunning) {
            // do physics for every object
            for (PhysicsObject po : physicsList) {
                po.stepPhysics(dt * 0.001);  // physics works with seconds
            }

            // wait for the required time before
            timeToWait = (endOfLastLoopTime + physics_dt) - System.currentTimeMillis();
            try {
                if (timeToWait > 0) {
                    Thread.sleep(timeToWait);
                } else {
                    // so that even if the thread isn't waiting at the end of the frame,
                    // it will still stop when interrupted
                    if (Thread.interrupted()) {
                        isPhysicsRunning = false;
                    }
                }
            } catch (InterruptedException e) {
                // if something causes the physics not to sleep properly, we will just stop the physics
                isPhysicsRunning = false;
            }

            // set the end of loop time
            endOfLastLoopTime = System.currentTimeMillis();
        }

    }

    public boolean isPhysicsRunning() {
        return isPhysicsRunning;
    }

    public void setPhysicsRunning(boolean isPhysicsRunning) {
        this.isPhysicsRunning = isPhysicsRunning;
    }

    @Override
    public void run() {
        physicsLoop();
    }

    public void addToPhysicsList (PhysicsObject object) {
        physicsList.add(object);
    }

    public void removeFromPhysicsList (PhysicsObject object) {
        // TODO
    }

    public void empty () {
        // TODO
    }

    public int getNumberOfPhysicsObjects () {
        // TODO
        return 0;
    }

    public boolean isRunning () {
        // TODO
        return true;
    }

    public void pause () {
        // TODO
        // TODO: make sure the physics actually checks to see if it's running
    }

    public void continuePhysics () {
        // TODO
    }

    public void setPhysicsTimeDelta (long dt_millis) {
        // TODO
    }
}
