package com.gmail.claytonrogers53.life.Physics;

import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.RollingAverage;

import java.util.ArrayList;
import java.util.Collection;

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
    private volatile boolean    isPhysicsRunning   = true;
    private volatile boolean    isPaused           = false;
    private RollingAverage<Long>   frameTimeAvg    = new RollingAverage<>(40);
    private RollingAverage<Double> loadAvg         = new RollingAverage<>(40);
    public double frameTime;
    public double load;
    /** The list of physics objects that will be calculated every loop */
    private final Collection<PhysicsThing> physicsList = new ArrayList<>();

    /**
     * Constructs a new physics systems object. The physics system reference should then be given to a new Thread
     * instance to start it. Physics objects may be added before or after the physics system has started.
     */
    public PhysicsSystem() {
        physics_dt        = Configuration.getValueInt   ("PHYSICS_DT",    (int)DEFAULT_PHYSICS_DT);
        physicsMultiplier = Configuration.getValueDouble("PHYSICS_MULTIPLIER", DEFAULT_PHYSICS_MULTIPLIER);
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

            // If the physics is paused we just want to wait around for a bit, then check again if we are paused or
            // if the physics has stopped running.
            if (isPaused) {
                try {
                    // Since the default dt will always be something small but reasonable will will use it as our sleep
                    // time.
                    Thread.sleep(PhysicsSystem.DEFAULT_PHYSICS_DT);
                } catch (InterruptedException e) {
                    // If something actively interrupts the physics thread, they probably want it to stop.
                    stopPhysics();
                }
            }

            // Step the physics for every object in our list.
            // Note that the actually physics implementation is left up the the physics object itself.
            synchronized (physicsList) {
                final long localPhysics_dt;
                synchronized (this) {
                    // We need to keep the physics dt the same for every physics object, but we don't want to lock
                    // "this" for the entire physics time.
                    localPhysics_dt = physics_dt;
                }
                for (PhysicsThing po : physicsList) {
                    po.stepPhysics(localPhysics_dt * PhysicsSystem.MILLISECOND_TO_SECOND);  // physics works with seconds
                }
            }

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
            // Record the frame time and load so it can be queried by the graphics system
            long frameTime = physics_dt-timeToWait;
            frameTimeAvg.addToPool(frameTime);
            double load = frameTime/((double)physics_dt) * 100L;
            loadAvg.addToPool(load);
            this.frameTime = frameTimeAvg.getAverage();
            this.load = loadAvg.getAverage();
            endOfLastLoopTime = System.currentTimeMillis();
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
    public boolean isPhysicsRunning() {
        // isPaused is volatile so we don't need to synchronise here.
        return !isPaused;
    }

    /**
     * Allows users to stop the physics system. This is the normal way of stopping the physics system.
     */
    public void stopPhysics () {
        isPhysicsRunning = false;
        Log.info("Stopping physics using method \"stopPhysics\"");
    }

    /**
     * Adds an objects to the physics calculation list. The object will continue being calculated until it is removed
     * from the physics system with removeFromPhysicsList or the physics list is cleared with clearPhysicsList. If an
     * object is already in the physics list, it will be ignored.
     *
     * @param object
     *        The physics object to be added to the list.
     *
     * @see #removeFromPhysicsList
     * @see #clearPhysicsList
     */
    public void addToPhysicsList (PhysicsThing object) {
        if (null == object) {
            Log.error("Attempted to add a null object to the physics list.");
            return;
        }

        synchronized (physicsList) {
            if (!physicsList.contains(object)) {
                physicsList.add(object);
                Log.info("Adding a physics object to the physics list.");
            } else {
                Log.warning("Attempted to add a physics object to the physics list that was already there.");
            }
        }
    }

    /**
     * Removes the given object form the physics list.
     *
     * @param object
     *        The object to be removed from the physics list.
     *
     * @see #clearPhysicsList
     */
    public void removeFromPhysicsList (PhysicsThing object) {
        if (null == object) {
            Log.error("Attempted to remove a null object from the physics list.");
            return;
        }

        synchronized (physicsList) {
            boolean didRemoveDoAnything = physicsList.remove(object);
            if (!didRemoveDoAnything) {
                Log.warning("Attempted to remove an object from the physics list that wasn't there.");
            } else {
                Log.info("Removed an object from the physics list.");
            }
        }
    }

    /**
     * Clears every object out of the physics list. This method should generally not be used unless you really want all
     * the physics objects to stop simulating (and potentially add new ones). It will have better performance than
     * calling removeFromPhysicsList on every object.
     *
     * @see #removeFromPhysicsList
     */
    public void clearPhysicsList () {
        synchronized (physicsList) {
            physicsList.clear();
            Log.info("Clearing the physics list.");
        }
    }

    /**
     * Allows users to query the current number of physics objects being simulated. Generally only useful for
     * debugging, or for performance metrics.
     *
     * @return The current number of objects in the physics list.
     */
    public int getNumberOfPhysicsObjects () {
        synchronized (physicsList) {
            return physicsList.size();
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

        synchronized (physicsList) {
            retString += "numPhysicsObj:    " + physicsList.size()  + NL;
        }

        return retString;
    }
}
