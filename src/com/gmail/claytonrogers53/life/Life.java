package com.gmail.claytonrogers53.life;

import com.gmail.claytonrogers53.life.Configuration.Configuration;
import com.gmail.claytonrogers53.life.Graphics.DrawLoop;
import com.gmail.claytonrogers53.life.Log.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Physics.Vector2D;

import java.io.IOException;

public class Life {

    public static void main (String[] args) {
        String logFilename = Log.DEFAULT_FILENAME;
        if (args.length == 2 && args[0].equals("--log")) {
            logFilename = args[1];
        }
        try {
            Log.init(logFilename);
        } catch (IOException e) {
            // Logging is important, therefore, do not start without it.
            e.printStackTrace();
            System.exit(1);
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

        Box myBox = new Box(1, 1, new Vector2D(0.0, 0.0), new Vector2D(0.0, 0.0));
        drawLoop.addToDrawList(myBox);
        physicsSystem.addToPhysicsList(myBox);

        // TODO: Actual life stuff

        try {
            physicsThread.join();
            drawingThread.join();
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }
    }
}