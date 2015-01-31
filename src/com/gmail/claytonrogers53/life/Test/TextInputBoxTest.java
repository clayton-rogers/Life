package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Configuration.Configuration;
import com.gmail.claytonrogers53.life.Graphics.*;
import com.gmail.claytonrogers53.life.Log.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Physics.Vector2D;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Tests the use of input boxes
 *
 * Created by Clayton on 25/1/2015.
 */
public class TextInputBoxTest {
    public static void main(String[] args) {
        Log.init();
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


        TextInputBox tib = new TextInputBox();
        tib.setText("");
        tib.setPosition(100,100);
        drawLoop.addGUIElement(tib);

        TextBox tb = new TextBox();
        tb.setPosition(400,100);
        tb.setText("");
        drawLoop.addGUIElement(tb);

        for (int i = 0; i < 1000; i ++) {

            tb.setText("Input text is: " + tib.getText());

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
    }
}
