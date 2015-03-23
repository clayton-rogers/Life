package com.gmail.claytonrogers53.life.Physics;

import java.util.List;

/**
 * A collision system interface which can be implemented to and used in the physics system. This is the broad phase
 * collision detection, therefore it should be fast, and is allowed to return false positives.
 *
 * Created by Clayton on 25/2/2015.
 */
public interface CollisionSystem {
    /**
     * Finds all of the possible collisions in the list of objects. It is allowed to return extra false positives, but
     * it is not allowed to miss collisions.
     *
     * @param objectList
     *        The list of collidable objects to be tested.
     *
     * @param physicsDT_seconds
     *        The length of the time step in seconds.
     *
     * @return A list of possible collisions. All actual collisions must be contained within. Allowed to return false
     * positives.
     */
    List<Collision> findCollisions(List<Collidable> objectList, double physicsDT_seconds);
}
