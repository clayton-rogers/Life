package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Graphics.GraphicsSystem;
import com.gmail.claytonrogers53.life.Graphics.TextBox;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.Vector2D;

/**
 * Tests the performance of the graphics and physics systems by creating 7000 objects.
 *
 * Created by Clayton on 14/12/2014.
 */
public class GraphicsPerformanceTest {
    public static void main (String[] args) {
        Log.init("Test.log");
        Configuration.loadConfigurationItems();

        GraphicsSystem graphicsSystem = new GraphicsSystem();
        PhysicsSystem physicsSystem = new PhysicsSystem();
        graphicsSystem.registerPhysicsSystem(physicsSystem);

        physicsSystem.start();
        graphicsSystem.start();

        Box myBox = new Box(1, 1, new Vector2D(0.0, 0.0), new Vector2D(0.0, 0.0), 0.0, 1.0);
        graphicsSystem.addToDrawList(myBox);
        physicsSystem.addObject(myBox);

        TextBox textBox = new TextBox();
        textBox.setPosition(20,300);
        graphicsSystem.addGUIElement(textBox);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final int NUM_BOXES = 5000;
        for (int i = 0; i < NUM_BOXES; i++) {
            double posX = 0.0;
            double posY = 0.0;
            Vector2D vel = Vector2D.getVector2DMagnitudeAndDirection(2.0, Math.random() * 2 * Math.PI);
            double angle = 0.0;
            double angVel = Math.random() * 2.0 - 2.0;
            myBox = new Box(1, 1, new Vector2D(posX,posY), vel, angle, angVel);
            myBox.setIsCollidable(false);
            graphicsSystem.addToDrawList(myBox);
            physicsSystem.addObject(myBox);
            textBox.setText("Number of objects: " + (i+1));

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }
    }
}
