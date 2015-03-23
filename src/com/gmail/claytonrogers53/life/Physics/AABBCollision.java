package com.gmail.claytonrogers53.life.Physics;

import com.gmail.claytonrogers53.life.Util.Vector2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implements the axis aligned bounding box method of collision detection. Uses the X-axis as the primary checking
 * direction, thus objects spread out over the X-axis will cause it to run faster.
 *
 * Created by Clayton on 25/2/2015.
 */
class AABBCollision implements CollisionSystem {

    /**
     * Represents either the upper or lower bound of a collidable object.
     */
    private static final class Bound implements Comparable<Bound>{
        /** The X position of the bound. */
        public final double value;
        /** The object that this is a bound of. */
        public final Collidable object;
        /** Whether this is an upper (left) or lower (right) bound. */
        public final boolean isUpper ;

        /**
         * Creates a bound object.
         *
         * @param object
         *        The object.
         *
         * @param value
         *        The location of the bound.
         *
         * @param isUpper
         *        Whether the bound is an upper or lower bound.
         */
        private Bound(Collidable object, double value, boolean isUpper) {
            this.object = object;
            this.value = value;
            this.isUpper = isUpper;
        }

        /**
         * Implements the Comparable interface.
         *
         * @param o
         *        The other object.
         *
         * @return 1, 0, -1 : When this object is greater than, equal to, or less than the other object (respectively).
         */
        @Override
        public int compareTo(@NotNull Bound o) {
            if (value == o.value) {
                return 0;
            }
            if (value > o.value) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * Implements the interface.
     *
     * @param objectList
     *        The list of collidable objects to be tested.
     *
     * @param physicsDT_seconds
     *        The length of the time step in seconds.
     *
     * @return All of the possible collisions.
     */
    @Override
    public List<Collision> findCollisions(List<Collidable> objectList, double physicsDT_seconds) {
        List<Collision> collisions = new ArrayList<>(3);
        List<Bound> bounds = new ArrayList<>(objectList.size()*2);

        for (Collidable object : objectList) {
            if (!object.isCollisionsEnabled()) {continue;}
            // Upper (i.e. left) bound
            bounds.add(new Bound(object, object.getCollisionRadius()
                    + object.getNextState().position.getMagX(), true));
            // Lower (i.e. right) bound
            bounds.add(new Bound(object, object.getCollisionRadius()
                    - object.getNextState().position.getMagX(), false));
        }

        Collections.sort(bounds);

        Collection<Collidable> activeObjects = new ArrayList<>(10);

        for (Bound bound : bounds) {
            if (bound.isUpper) {
                // For every other object that is currently active, this is a possible collision.
                for (Collidable object : activeObjects) {
                    if (isPotentialCollisionInY(object, bound.object)) {
                        collisions.add(new Collision(object, bound.object, physicsDT_seconds));
                    }
                }

                activeObjects.add(bound.object);
            } else {
                // Is a lower bound, just remove the object from the actives.
                activeObjects.remove(bound.object);
            }
        }

        return collisions;
    }

    /**
     * Checks for collisions using only the Y direction, since the main loop uses the X direction.
     *
     * @param obj1
     *        The first object.
     *
     * @param obj2
     *        The second object.
     *
     * @return True when there is the potential for a collision.
     */
    private static boolean isPotentialCollisionInY(Collidable obj1, Collidable obj2) {
        double maxDistance = obj1.getCollisionRadius() + obj2.getCollisionRadius();
        Vector2D pos1 = obj1.getNextState().position;
        Vector2D pos2 = obj2.getNextState().position;

        return Math.abs(pos1.getMagY() - pos2.getMagY()) < maxDistance;
    }
}
