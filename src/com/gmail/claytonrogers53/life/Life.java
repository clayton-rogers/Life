package com.gmail.claytonrogers53.life;

import com.gmail.claytonrogers53.life.Graphics.DrawLoop;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Physics.Vector2D;

public class Life {

    public static void main (String args[]) {
        DrawLoop drawLoop = new DrawLoop();
        Thread drawingThread = new Thread(drawLoop);
        PhysicsSystem physicsSystem = new PhysicsSystem();
        Thread physicsThread = new Thread(physicsSystem);

        physicsThread.start();
        drawingThread.start();

        Box myBox = new Box(1, 1, new Vector2D(20.0, 20.0), new Vector2D(1.50, 1.50), 20.0, 20.0);
        drawLoop.addToDrawList(myBox);
        physicsSystem.addToPhysicsList(myBox);

        try {
//            Thread.sleep(10000); // for now we will automatically close after 10 seconds
//            physicsThread.interrupt();
//            drawingThread.interrupt();
            physicsThread.join();
            drawingThread.join();
        } catch (InterruptedException e) {
            System.exit(13);
        }
    }
}