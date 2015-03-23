package com.gmail.claytonrogers53.life.Physics;

import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.RollingAverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The physics system keeps track of all of the physics objects and updates them once every physics loop. The actual
 * implementation of the physics is left up to the particular physics object. The abstract PhysicsObject implements
 * the basic physics needed by most objects: 2D linear and angular mass, momentum and force handling.
 * Created by Clayton on 13/11/2014.
 */
public final class PhysicsSystem implements Runnable {

    static final private String NL = System.getProperty("line.separator");

    /** Easy conversion from milliseconds to seconds. */
    static final private double MILLISECOND_TO_SECOND = 0.001;

    /** The default time between physics frames if one is not specified in the configuration file. */
    static final private long   DEFAULT_PHYSICS_DT           = 17L;
    /** The default physics multiplier if one is not specified in the configuration file. */
    static final private double DEFAULT_PHYSICS_MULTIPLIER   = 1.0;

    // Actual instance variables.
    private long                physics_dt         = DEFAULT_PHYSICS_DT;
    private double              physicsMultiplier  = DEFAULT_PHYSICS_MULTIPLIER;
    private volatile boolean    isPhysicsRunning   = false;
    private volatile boolean    isPaused           = false;
    private RollingAverage<Long>   frameTimeAvg    = new RollingAverage<>(40);
    private RollingAverage<Double> loadAvg         = new RollingAverage<>(40);
    public double frameTime;
    public double load;
    /** The lists of physics objects that will be calculated every loop */
    /** The list of non object physics things that need to be calculated every loop. ex. gravity, some game mechanic.*/
    private final Collection<PhysicsThing> physicsThings = new ArrayList<>(100);
    /** The list of objects that propagates and can (potentially) collide. */
    private final List<Collidable>         objects       = new ArrayList<>(100);
    /** The system which detects all of the collisions between objects */
    private final CollisionSystem collisionSystem = new AABBCollision();

    /**
     * Constructs a new physics systems object. The physics system reference should then be given to a new Thread
     * instance to start it. Physics objects may be added before or after the physics system has started.
     */
    public PhysicsSystem() {
        physics_dt        = Configuration.getValueInt   ("PHYSICS_DT",    (int)DEFAULT_PHYSICS_DT);
        physicsMultiplier = Configuration.getValueDouble("PHYSICS_MULTIPLIER", DEFAULT_PHYSICS_MULTIPLIER);
    }

    /**
     * Creates and starts the physics thread. Can only be called once.
     */
    public void start() {
        if (isPhysicsRunning) {
            Log.warning("Tried to start the physics thread after it was already started.");
            return;
        }

        isPhysicsRunning = true;
        Thread physicsThread = new Thread(this);
        physicsThread.start();
    }

    /**
     * Called by the new thread when it is started. Simply starts the physics loop which continues until it is actively
     * stopped.
     *
     * @see #physicsLoop
     */
    @Override
    public void run() {
        Log.info("Starting physics loop.");
        physicsLoop();
        Log.info("Physics loop has stopped.");
    }

    /**
     * The main physics loop. This is run on the new thread. It keeps running even when paused.
     */
    private void physicsLoop() {
        long endOfLastLoopTime = System.currentTimeMillis();

        while (isPhysicsRunning) {
            final long localPhysics_dt;
            synchronized (this) {
                // We need to keep the physics dt the same for every physics object, but we don't want to lock
                // "this" for the entire physics time.
                localPhysics_dt = physics_dt;
            }

            // If the physics is paused we just want to wait around for a bit, then check again if we are paused or
            // if the physics has stopped running.
            if (isPaused) {
                try {
                    // Since the default dt will always be something small but reasonable will will use it as our sleep
                    // time.
                    Thread.sleep(localPhysics_dt);
                } catch (InterruptedException e) {
                    // If something actively interrupts the physics thread, they probably want it to stop.
                    stopPhysics();
                }
            } else {

                step(localPhysics_dt);

                // After physics calculations have completed, there still may be lots of time left in the frame. So we will
                // wait until the frame has expired. The time to wait is the time left in the current frame.
                long timeToWait;
                synchronized (this) {
                    timeToWait = (endOfLastLoopTime + (long) (physics_dt / physicsMultiplier)) - System.currentTimeMillis();
                }
                try {
                    if (timeToWait > 0) {
                        Thread.sleep(timeToWait);
                    } else {
                        // If the physics thread is overloaded and never sleeps, we still need to check if we have been
                        // interrupted so that we will stop when we have.
                        if (Thread.interrupted()) {
                            stopPhysics();
                        }
                    }
                } catch (InterruptedException e) {
                    stopPhysics();
                }
                endOfLastLoopTime = System.currentTimeMillis();
                // Record the frame time and load so it can be queried by the graphics system
                long frameTime = physics_dt - timeToWait;
                frameTimeAvg.addToPool(frameTime);
                double load = frameTime / ((double) physics_dt) * 100L;
                loadAvg.addToPool(load);
                this.frameTime = frameTimeAvg.getAverage();
                this.load = loadAvg.getAverage();

            }
        }
    }


    /**
     * Steps the physics forwards by a given time step. This is automatically called every physics loop, but can also
     * be manually called.
     *
     * @param stepPhysics_dt
     *        The length of the physics time step (ms).
     */
    public void step(long stepPhysics_dt) {
        /** The list of possible collisions. */
        List<Collision> collisions;
        // Propagate and calculate collisions for all of the objects.
        synchronized (objects) {
            double physicsDT_seconds = stepPhysics_dt * MILLISECOND_TO_SECOND;
            for (;;) {
                // Calculate the next steps of the
                for (Collidable object : objects) {
                    object.calculateNextState(physicsDT_seconds);
                }

                collisions = collisionSystem.findCollisions(objects, physicsDT_seconds);

                // Remove all the false positives
                for (Collision collision : collisions) {
                    if (!collision.isCollision()) {
                        collisions.remove(collision);
                    }
                }

                // If there are no collisions, then we're done
                if (collisions.size() == 0) {break;}

                // Find the first collision
                Collision earliestCollision = collisions.get(0);
                for (Collision collision : collisions) {
                    if (collision.getCollisionTime() < earliestCollision.getCollisionTime()) {
                        earliestCollision = collision;
                    }
                }

                // Move all the objects forward to the collision time
                for (Collidable object : objects) {
                    object.calculateNextState(earliestCollision.getCollisionTime());
                    object.applyNextState();
                }
                physicsDT_seconds -= earliestCollision.getCollisionTime();

                // Evaluate the first collision
                earliestCollision.resolveCollision();

                // The next loop starts with the remaining time for this loop.
            }

            // Move all the objects forward to the end of the physics time
            for (Collidable object : objects) {
                object.calculateNextState(physicsDT_seconds);
                object.applyNextState();
            }
        }
        // TODO: look over this section.

        // Physics things are non objects which still want to have some physics calculated.
        synchronized (physicsThings) {
            for (PhysicsThing po : physicsThings) {
                po.calculatePhysics(stepPhysics_dt * MILLISECOND_TO_SECOND);  // physics works with seconds
            }
        }
    }

    /**
     * Allows users to query whether the physics calculations are currently paused.
     *
     * @return True when the physics system is actively calculating physics every loop.
     *
     * @see #pausePhysics
     * @see #continuePhysics
     */
    public boolean isPhysicsPaused() {
        // isPaused is volatile so we don't need to synchronise here.
        return isPaused;
    }

    /**
     * Allows users to stop the physics system. This is the normal way of stopping the physics system.
     */
    public void stopPhysics () {
        isPhysicsRunning = false;
        Log.info("Stopping physics using method \"stopPhysics\"");
    }

    /**
     * Allows users to query whether the physics thread has stopped. The physics may be in the stopped state: before
     * the physics thread has been started with start(), after the physics thread has ended through a call to
     * stopPhysics(), or after the physics thread has stopped due to an interrupt.
     *
     * @return Whether the physics system is in the running state.
     */
    public boolean isPhysicsRunning() {
        return isPhysicsRunning;
    }

    /**
     * Adds an objects to the physics thing calculation list. The object will continue being calculated until it is
     * removed from the physics thing list with removePhysicsThing or the physics list is cleared with
     * clearPhysicsThings. If an object is already in the physics thing list, it will be ignored.
     *
     * Physics things should be used for non object things which need to be calculated every time step.
     *
     * @param object
     *        The physics thing to be added to the list.
     *
     * @see #removePhysicsThing
     * @see #clearPhysicsThings
     */
    public void addPhysicsThing(PhysicsThing object) {
        if (null == object) {
            Log.error("Attempted to add a null object to the physics things list.");
            return;
        }

        synchronized (physicsThings) {
            if (!physicsThings.contains(object)) {
                physicsThings.add(object);
                Log.info("Adding a physics thing to the list.");
            } else {
                Log.warning("Attempted to add a physics things to the list that was already there.");
            }
        }
    }

    /**
     * Removes the given physics thing form the list.
     *
     * @param object
     *        The thing to be removed from the list.
     *
     * @see #clearPhysicsThings
     */
    public void removePhysicsThing(PhysicsThing object) {
        if (null == object) {
            Log.error("Attempted to remove a null object from the physics things list.");
            return;
        }

        synchronized (physicsThings) {
            boolean didRemoveDoAnything = physicsThings.remove(object);
            if (!didRemoveDoAnything) {
                Log.warning("Attempted to remove a physics things from the list that wasn't there.");
            } else {
                Log.info("Removed a physics thing from the list.");
            }
        }
    }

    /**
     * Clears every object out of the physics thing list. This method should generally not be used unless you really
     * want all the physics things to stop simulating (and potentially add new ones). It will have better performance
     * than calling removePhysicsThing on every object.
     *
     * @see #removePhysicsThing
     */
    public void clearPhysicsThings() {
        synchronized (physicsThings) {
            physicsThings.clear();
            Log.info("Clearing the physics things list.");
        }
    }

    /**
     * Adds an object to the physics system. The object will continue being calculated until it is
     * removed from the physics thing list with removeObject or the physics list is cleared with
     * clearObjects. If an object is already in the object list, it will be ignored.
     *
     * Objects propagate through space and (potentially) collide.
     *
     * @param object
     *        The object to be added to the system.
     */
    public void addObject (Collidable object) {
        if (null == object) {
            Log.error("Attempted to add a null object to the physics system.");
            return;
        }

        synchronized (objects) {
            if (!objects.contains(object)) {
                objects.add(object);
                Log.info("Adding an object to the physics system.");
            } else {
                Log.warning("Attempted to add an object to the physics list that was already there.");
            }
        }
    }

    /**
     * Removes the given object from the physics system.
     *
     * @param object
     *        The object to be removed from the system.
     */
    public void removeObject(Collidable object) {
        if (null == object) {
            Log.error("Attempted to remove a null object from the physics system.");
            return;
        }

        synchronized (objects) {
            boolean didRemoveDoAnything = objects.remove(object);
            if (!didRemoveDoAnything) {
                Log.warning("Attempted to remove an object from the physics list that wasn't there.");
            } else {
                Log.info("Removed an object from the physics list.");
            }
        }
    }

    /**
     * Clears every object out of the physics list. This method should generally not be used unless you really
     * want all the objects to stop simulating (and potentially add new ones). It will have better performance
     * than calling removeObject on every object.
     *
     */
    public void clearObjects() {
        synchronized (objects) {
            objects.clear();
            Log.info("Clearing the physics things list.");
        }
    }

    /**
     * Allows users to pause the simulation temporarily. The simulation can be restarted with continuePhysics. If one
     * wishes to completely stop the physics thread, they should call the stopPhysics method. If the physics is already
     * paused, this method does nothing.
     *
     * @see #continuePhysics
     * @see #stopPhysics
     */
    public void pausePhysics () {
        isPaused = true;
        Log.info("Pausing physics.");
    }

    /**
     * Allows users to continue the physics simulation after it was paused with pausePhysics. If the physics is already
     * running, this method does nothing.
     *
     * @see #pausePhysics
     */
    public void continuePhysics () {
        isPaused = false;
        Log.info("Unpausing physics.");
    }

    /**
     * Sets the current physics time delta per physics step. There is generally no need to adjust this value. Setting
     * this to a smaller value will generally give a more accurate physics simulation at the expense of CPU time. If
     * this is set too small, there may be extra errors due to rounding.
     *
     * @param dt_millis
     *        The desired time per physics frame.
     */
    public synchronized void setPhysicsTimeDelta (long dt_millis) {
        if (dt_millis < 0) {
            Log.warning("Attempted to set a physics delta t of less than zero.");
            return;
        }

        physics_dt = dt_millis;
        Log.info("Setting a physics delta t to " + String.valueOf(physics_dt) + " ms.");
    }

    /**
     * Allows users to query the the physics delta currently being used.
     *
     * @return The desired physics delta t.
     */
    public synchronized long getPhysicsTimeDelta () {
        return physics_dt;
    }

    /**
     * Allows the user to set the physics multiplier. A physics multiplier of 1.0 means the physics will run in real
     * time. A physics multiplier of 10 and a physics_dt of 10 ms means that the physics system will attempt to
     * perform 10 steps of physics (each 10 ms simulated) in 10 ms of real time. Setting the physics multiplier to a
     * value higher than the physics_dt (ex. 30x) will cause the physics to run as fast as possible. Note that the
     * virtual time between integrated physics time steps will still be physics_dt.
     *
     * @param physicsMultiplier
     *        The desired physics multiplier.
     */
    public synchronized void setPhysicsMultiplier(double physicsMultiplier) {
        if (physicsMultiplier <= 0) {
            Log.warning("Attempted to set a physics multiplier of less than or equal to zero.");
            return;
        }
        this.physicsMultiplier = physicsMultiplier;
        Log.info("Set physics multiplier to " + String.valueOf(this.physicsMultiplier) + "x.");
    }

    /**
     * Overrides the default toString method with some useful information about the physics system.
     *
     * @return A string containing information about about the physics system.
     */
    public String toString () {
        String retString = "";

        synchronized (this) {
            retString += "physics_dt:        " + physics_dt         + NL;
            retString += "physicsMultiplier: " + physicsMultiplier  + NL;
            retString += "isPhysicsRunning:  " + isPhysicsRunning   + NL;
            retString += "isPaused:          " + isPaused           + NL;
        }

        synchronized (physicsThings) {
            retString += "numPhysicsObj:    " + physicsThings.size()  + NL;
        }

        return retString;
    }
}
