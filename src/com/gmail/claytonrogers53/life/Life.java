package com.gmail.claytonrogers53.life;

import com.gmail.claytonrogers53.life.Configuration.Configuration;
import com.gmail.claytonrogers53.life.Graphics.DrawLoop;
import com.gmail.claytonrogers53.life.Log.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Physics.Vector2D;

import java.io.IOException;

public class Life {

    public static void main (String[] args) {
        String logFilename = null;
        if (args.length == 2 && args[0].equals("--log")) {
            logFilename = args[1];
        }
        if (logFilename == null) {
            Log.init();
        } else {
            Log.init(logFilename);
        }
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
            physicsThread.join();
            drawingThread.join();
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }

        // Call some methods to it stop complaining about them being unused.
        drawLoop.stopDrawing();
        drawLoop.removeFromDrawList(myBox);
        drawLoop.clearDrawList();
        drawLoop.setPan(0,0);
        drawLoop.setZoom(1.0);
        drawLoop.setGraphicsTimeDelta(17);
        drawLoop.setFPS(60);
    }
}