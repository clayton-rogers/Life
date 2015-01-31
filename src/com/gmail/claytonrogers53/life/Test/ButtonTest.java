package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Configuration.Configuration;
import com.gmail.claytonrogers53.life.Graphics.Button;
import com.gmail.claytonrogers53.life.Graphics.DrawLoop;
import com.gmail.claytonrogers53.life.Log.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Physics.Vector2D;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Tests the use of buttons.
 *
 * Created by Clayton on 30/12/2014.
 */
public class ButtonTest {
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


        Button b = new Button();
        b.setButtonText("Hello Button!");
        b.registerCallback(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                System.out.println("Hello Button Pressed!!!");
                return null;
            }
        });
        b.setPosition(100, 100);
        //b.setHeight(10);
        drawLoop.addGUIElement(b);

        Button b2 = new Button();
        b2.setButtonText("A second button.");
        b2.registerCallback(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                System.out.println("Second Button Pressed!!!");
                return null;
            }
        });
        b2.setPosition(200, 200);
        drawLoop.addGUIElement(b2);


        for (int i = 0; i < 100; ++i) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.exit(32);
            }
            b.setButtonText("Hello Button!     A very very very long text.");
            b2.setCornerArcRadius(5);
            b2.setPosition(300,300);
            b2.setHeight(25);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.exit(32);
            }
            b.setButtonText("Hello Button! Short Text.");
            b2.setCornerArcRadius(20);
            b2.setPosition(400,400);
            b2.setHeight(50);
        }

        try {
            physicsThread.join();
            drawingThread.join();
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }

        drawLoop.removeGUIElement(b);
        drawLoop.removeGUIElement(b2);
        drawLoop.clearGUIElementList();
    }
}
