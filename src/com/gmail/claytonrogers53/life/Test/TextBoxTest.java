package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Graphics.GraphicsSystem;
import com.gmail.claytonrogers53.life.Graphics.TextBox;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Vector2D;

/**
 * Tests the use of text boxes.
 *
 * Created by Clayton on 25/1/2015.
 */
public class TextBoxTest {
    public static void main(String[] args) {
        Log.init();
        Configuration.loadConfigurationItems();

        GraphicsSystem graphicsSystem = new GraphicsSystem();
        Thread drawingThread = new Thread(graphicsSystem);
        PhysicsSystem physicsSystem = new PhysicsSystem();
        Thread physicsThread = new Thread(physicsSystem);

        physicsThread.start();
        drawingThread.start();

        Box myBox = new Box(1, 1, new Vector2D(0.0, 0.0), new Vector2D(0.0, 0.0), 0.0, 0.0);
        graphicsSystem.addToDrawList(myBox);
        physicsSystem.addToPhysicsList(myBox);


        TextBox t = new TextBox();
        t.setPosition(100, 100);
        t.setWidth(200);
        t.setText("Hello World!!! and a long words of stuff");

        graphicsSystem.addGUIElement(t);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.exit(32);
        }

        final String w = " a word";

        for (int i = 0; i < 60; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.exit(32);
            }
            t.appendText(w);
        }
        t.setWidth(300);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        t.setWidth(100);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        t.setWidth(250);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        t.setPosition(200, 200);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        t.setPosition(300, 300);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        t.setPosition(100, 100);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }
        t.clearText();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.exit(32);
        }

        try {
            physicsThread.join();
            drawingThread.join();
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }

        String text = t.getText();
        System.out.println("Text box contains: |" + text + "|");
    }
}
