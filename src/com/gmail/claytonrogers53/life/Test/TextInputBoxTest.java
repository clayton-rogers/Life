package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Graphics.*;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Vector2D;

/**
 * Tests the use of input boxes
 *
 * Created by Clayton on 25/1/2015.
 */
public class TextInputBoxTest {
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
        physicsSystem.addToPhysicsList(myBox);


        TextInputBox tib = new TextInputBox();
        tib.setText("");
        tib.setPosition(100,100);
        graphicsSystem.addGUIElement(tib);

        TextBox tb = new TextBox();
        tb.setPosition(400,100);
        tb.setText("");
        graphicsSystem.addGUIElement(tb);

        for (int i = 0; i < 1000; i ++) {

            tb.setText("Input text is: " + tib.getText());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.exit(32);
            }
        }

        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }
    }
}
