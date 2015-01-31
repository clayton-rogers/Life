package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Configuration.Configuration;
import com.gmail.claytonrogers53.life.Graphics.DrawLoop;
import com.gmail.claytonrogers53.life.Log.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Physics.Vector2D;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests the performance of the graphics and physics systems by creating 7000 objects.
 *
 * Created by Clayton on 14/12/2014.
 */
public class PerformanceTest {
    public static void main (String[] args) {
        Log.init();
        Log.info("Loading configuration items.");
        Configuration.loadConfigurationItems();
        Log.info("Loading configuration items done.");

        DrawLoop drawLoop = new DrawLoop();
        Thread drawingThread = new Thread(drawLoop);
        PhysicsSystem physicsSystem = new PhysicsSystem();
        Thread physicsThread = new Thread(physicsSystem);

        physicsThread.start();
        drawingThread.start();

        Box myBox = new Box(1, 1, new Vector2D(0.0, 0.0), new Vector2D(0.0, 0.0), 0.0, 0.0);
        drawLoop.addToDrawList(myBox);
        physicsSystem.addToPhysicsList(myBox);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final int NUM_BOXES = 7000;
        for (int i = 0; i < NUM_BOXES; i++) {
            double posX = 0.0;
            double posY = 0.0;
            double velX = Math.random() * 2.0 - 1.0;
            double velY = Math.random() * 2.0 - 1.0;
            Vector2D vel = Vector2D.getVector2DMagnitudeAndDirection(10.0, Math.random() * 2 * Math.PI);
            double angle = Math.random() * Math.PI * 2.0;
            double angVel = Math.random() * 2.0 - 2.0;
            myBox = new Box(1, 1, new Vector2D(posX,posY), vel, angle, angVel);
            drawLoop.addToDrawList(myBox);
            physicsSystem.addToPhysicsList(myBox);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        try {
            physicsThread.join();
            drawingThread.join();
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }
    }
}
