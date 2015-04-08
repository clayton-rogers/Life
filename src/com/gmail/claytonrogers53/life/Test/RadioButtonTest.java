package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Graphics.GUI.Button;
import com.gmail.claytonrogers53.life.Graphics.GUI.RadioBox;
import com.gmail.claytonrogers53.life.Graphics.GUI.TextBox;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Graphics.*;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Vector2D;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Tests the use of radio buttons.
 *
 * Created by Clayton on 25/1/2015.
 */
public class RadioButtonTest {
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


        final RadioBox radioBox = new RadioBox();
        radioBox.setPosition(100, 100);
        ArrayList<String> optionList = new ArrayList<>(3);
        optionList.add("Option 1");
        optionList.add("Option 2");
        optionList.add("Option 3 (actually option 2)");
        radioBox.setOptionList(optionList);
        radioBox.setSelectedOption(2);
        graphicsSystem.addGUIElement(radioBox);

        TextBox t = new TextBox();
        t.setPosition(300,100);
        graphicsSystem.addGUIElement(t);

        Button b = new Button();
        b.setPosition(600, 100);
        b.setHeight(30);
        b.setButtonText("Press this button to set the second option.");
        b.registerCallback(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                radioBox.setSelectedOption(1);
                return null;
            }
        });
        graphicsSystem.addGUIElement(b);

        for (int i = 0; i < 1000; i ++) {

            t.setText("Current radio box selection: " + radioBox.getSelectedOption() + "\n");
            t.appendText("Current radio box selection index: " + radioBox.getSelectedOptionIndex());

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

        radioBox.getOptionList();
        radioBox.setCornerArcRadius(10);
    }
}
