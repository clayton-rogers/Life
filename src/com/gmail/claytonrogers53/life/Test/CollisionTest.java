package com.gmail.claytonrogers53.life.Test;

import com.gmail.claytonrogers53.life.Box;
import com.gmail.claytonrogers53.life.Graphics.GraphicsSystem;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.Vector2D;

/**
 * Tests the collision system by bouncing things off of other things.
 *
 * Created by Clayton on 3/2/2015.
 */
public class CollisionTest {
    public static void main (String[] args) {
        Log.init("CollisionTest.log");
        Configuration.loadConfigurationItems();

        GraphicsSystem graphicsSystem = new GraphicsSystem();
        PhysicsSystem physicsSystem = new PhysicsSystem();
        graphicsSystem.registerPhysicsSystem(physicsSystem);

        Box box1 = new Box(1, 1, new Vector2D(0,0), new Vector2D( 0,0), 0,0.1);
        box1.setIsCollidable(true);
        Box box2 = new Box(1, 1, new Vector2D(10,0.75), new Vector2D(-1,0), 0,0.2);
        box2.setIsCollidable(true);

        physicsSystem.addObject(box1);
        physicsSystem.addObject(box2);
        graphicsSystem.addToDrawList(box1);
        graphicsSystem.addToDrawList(box2);

        physicsSystem.start();
        graphicsSystem.start();

        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            Log.error("Main thread was interrupted! Exiting.");
        }
    }
}
