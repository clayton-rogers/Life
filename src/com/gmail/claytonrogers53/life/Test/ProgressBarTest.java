package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Graphics.GraphicsSystem;
import com.gmail.claytonrogers53.life.Graphics.GUI.ProgressBar;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Vector2D;

/**
 * Tests the use of progress bars.
 *
 * Created by Clayton on 25/1/2015.
 */
public class ProgressBarTest {
    public static void main(String[] args) {
        Log.init("Test.log");
        Configuration.loadConfigurationItems();

        GraphicsSystem graphicsSystem = new GraphicsSystem();
        PhysicsSystem physicsSystem = new PhysicsSystem();
        graphicsSystem.registerPhysicsSystem(physicsSystem);

        physicsSystem.start();
        graphicsSystem.start();

        Box myBox = new Box(1, 1, new Vector2D(0.0, 0.0), new Vector2D(0.0, 0.0), 0.0, 0.0);
        graphicsSystem.addToDrawList(myBox);
        physicsSystem.addObject(myBox);


        ProgressBar p = new ProgressBar();
        p.setPosition(100,100);
        graphicsSystem.addGUIElement(p);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.exit(32);
        }

        for (int i = 0; i <= 100; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.exit(32);
            }
            p.setProgress(i);
        }
        for (int i = 100; i >= 0; i--) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.exit(32);
            }
            p.setProgress(i);
        }
        p.setProgress(75);
        p.setWidth(300);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        p.setWidth(100);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        p.setWidth(250);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        p.setPosition(200, 200);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        p.setPosition(300, 300);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        p.setPosition(100, 100);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        p.setHeight(100);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }

        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }
    }
}
