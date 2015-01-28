package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Configuration.Configuration;
import com.gmail.claytonrogers53.life.Graphics.*;
import com.gmail.claytonrogers53.life.Log.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Physics.Vector2D;
import jdk.nashorn.internal.codegen.CompilerConstants;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Tests the use of check boxes.
 *
 * Created by Clayton on 25/1/2015.
 */
public class CheckBoxTest {
    public static void main(String[] args) {
        try {
            Log.init(Log.DEFAULT_FILENAME);
        } catch (IOException e) {
            // Logging is important, therefore, do not start without it.
            e.printStackTrace();
            System.exit(1);
        }
        Configuration.loadConfigurationItems();

        DrawLoop drawLoop = new DrawLoop();
        Thread drawingThread = new Thread(drawLoop);
        PhysicsSystem physicsSystem = new PhysicsSystem();
        Thread physicsThread = new Thread(physicsSystem);

        physicsThread.start();
        drawingThread.start();

        Box myBox = new Box(1, 1, new Vector2D(0.0, 0.0), new Vector2D(0.0, 0.0), 0.0, 0.0);
        drawLoop.addToDrawList(myBox);
        physicsSystem.addToPhysicsList(myBox);


        final CheckBox c = new CheckBox();
        c.setCheckBoxText("This is a checkbox!!!");
        c.setState(CheckBox.State.CHECKED);
        c.setPosition(100,100);
        drawLoop.addGUIElement(c);

        TextBox t = new TextBox();
        t.setPosition(300,100);
        drawLoop.addGUIElement(t);

        Button b = new Button();
        b.setPosition(600, 100);
        b.setHeight(30);
        b.setButtonText("Press this button to un-check the checkbox.");
        b.registerCallback(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                c.setState(CheckBox.State.UNCHECKED);
                return null;
            }
        });
        drawLoop.addGUIElement(b);

        for (int i = 0; i < 1000; i ++) {

            t.setText("Current check box state: " + c.getState());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.exit(32);
            }
        }

        try {
            physicsThread.join();
            drawingThread.join();
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }

        c.setHeight(100);
        c.setCornerArcRadius(10);
    }
}
