package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Graphics.*;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Vector2D;

import java.util.concurrent.Callable;

/**
 * Tests the use of check boxes.
 *
 * Created by Clayton on 25/1/2015.
 */
public class CheckBoxTest {
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


        final CheckBox c = new CheckBox();
        c.setCheckBoxText("This is a checkbox!!!");
        c.setState(CheckBox.State.CHECKED);
        c.setPosition(100,100);
        graphicsSystem.addGUIElement(c);

        TextBox t = new TextBox();
        t.setPosition(300,100);
        graphicsSystem.addGUIElement(t);

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
        graphicsSystem.addGUIElement(b);

        for (int i = 0; i < 1000; i ++) {

            t.setText("Current check box state: " + c.getState());

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

        c.setHeight(100);
        c.setCornerArcRadius(10);
    }
}
