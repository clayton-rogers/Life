package com.gmail.claytonrogers53.life;

import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Graphics.GraphicsSystem;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Vector2D;

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

        GraphicsSystem graphicsSystem = new GraphicsSystem();
        PhysicsSystem physicsSystem = new PhysicsSystem();

        graphicsSystem.registerPhysicsSystem(physicsSystem);

        graphicsSystem.start();
        physicsSystem.start();

        Box myBox = new Box(1, 1, new Vector2D(0.0, 0.0), new Vector2D(0.0, 0.0), 0.0, 0.0);
        graphicsSystem.addToDrawList(myBox);
        physicsSystem.addToPhysicsList(myBox);

        try {
            while (physicsSystem.isPhysicsRunning() && graphicsSystem.isGraphicsRunning()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Log.error("Main Life thread was interrupted!!!");
        }

        // Stop both the systems (one will have already stopped).
        graphicsSystem.stopDrawing();
        physicsSystem.stopPhysics();

        // Call some methods to it stop complaining about them being unused.
        graphicsSystem.removeFromDrawList(myBox);
        graphicsSystem.clearDrawList();
        graphicsSystem.setPan(0,0);
        graphicsSystem.setZoom(1.0);
        graphicsSystem.setGraphicsTimeDelta(17);
        graphicsSystem.setFPS(60);
    }
}