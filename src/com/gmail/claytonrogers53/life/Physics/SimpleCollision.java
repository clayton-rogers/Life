package com.gmail.claytonrogers53.life.Physics;

import com.gmail.claytonrogers53.life.Util.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the brute force method of finding collisions. It checks every object against every
 * other object to see if their bounding circles overlap.
 *
 * Created by Clayton on 25/2/2015.
 */
class SimpleCollision implements CollisionSystem {

    /**
     * Implements the interface.
     *
     * @param objectList
     *        The list of collidable objects to be tested.
     *
     * @param physicsDT_seconds
     *        The length of the time step in seconds.
     *
     * @return The list of possible collisions.
     */
    @Override
    public List<Collision> findCollisions(List<Collidable> objectList, double physicsDT_seconds) {
        List<Collision> collisions = new ArrayList<>(3);

        // Find all of the collisions
        // This is the slow part
        int numObject = objectList.size();
        for (int i = 0; i < numObject; i++) {
            if (objectList.get(i).isCollisionsEnabled()) {
                Vector2D pos1 = objectList.get(i).getNextState().position;
                double radius = objectList.get(i).getCollisionRadius();
                for (int j = i + 1; j < numObject; j++) {
                    if (objectList.get(j).isCollisionsEnabled()) {
                        double minDistance = objectList.get(j).getCollisionRadius() + radius;
                        Vector2D pos2 = objectList.get(j).getNextState().position;
                        if (pos1.sub(pos2).getMag() < minDistance) {
                            collisions.add(new Collision(objectList.get(i), objectList.get(j), physicsDT_seconds));
                        }
                    }
                }
            }
        }

        return collisions;
    }
}
