package com.gmail.claytonrogers53.life.Physics;

import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.Util;
import com.gmail.claytonrogers53.life.Util.Vector2D;

/**
 * Represents a collision between two objects at a particular time.
 *
 * Created by Clayton on 8/2/2015.
 */
public class Collision {

    /** The precision to which the collision ime is found. Default = 0.00001 s*/
    private static final double COLLISION_TIME_PRECISION = 0.000001;
    private static final double PERCENTAGE_THRESHOLD = 0.000001;

    /** References to the two objects that are (potentially) colliding. */
    private final Collidable[] objects = new Collidable[2];
    /** The actual time of the collision. Only valid when isCollision is true. */
    private double collisionTime;
    /** The collider is the one who has a vertex inside the other object. */
    private Collidable collider;
    /** The collidee is the one who has a space which is penetrated by the other object. */
    private Collidable collidee;
    /** The location of the collision in world coordinates. */
    private final Vector2D collisionPoint = new Vector2D();
    /** A collision object is created when the is a potential collision. If the fine collision
     * detection ends up being a near miss, then this will be set to false. */
    private boolean isCollision;
    /** The time until the next frame. The collision must happen before this time. */
    private final double timeStep;

    /**
     * Create a new potential collision between two objects. Also calculates the exact time of the
     * collision or whether it is in fact a near miss.
     *
     * @param ref1
     *        Reference to the first object.
     *
     * @param ref2
     *        Reference to the second object.
     *
     * @param timeStep
     *        The length of the time step. The collision must happen no later than this.
     */
    public Collision (Collidable ref1, Collidable ref2, double timeStep) {
        objects[0] = ref1;
        objects[1] = ref2;

        this.timeStep = timeStep;

        findCollisionTime();
    }

    public double getCollisionTime() {
        if (isCollision) {
            return collisionTime;
        } else {
            String errorString = "Tried to get the collision time of a non collision.";
            Log.warning(errorString);
            throw new IllegalStateException(errorString);
        }
    }

    public boolean isCollision() {
        return isCollision;
    }

    /**
     * Advances the two objects to the collision point. Calculates the new velocities and angular
     * velocities and then recalculates the object positions at the end of the frame time.
     */
    public void resolveCollision() {
        if (!isCollision()) {
            String errorString = "Tried to resolve a collision that wasn't a collision.";
            Log.warning(errorString);
            throw new IllegalStateException(errorString);
        }

        boolean isCollisionResolved = false;
        if (objects[0].isCollisionResolutionEnabled() &&
            objects[1].isCollisionResolutionEnabled()) {
            isCollisionResolved = true;
            // We actually resolve the collision.

            // R vectors go from the CG of the shapes to the collision point.
            Vector2D colliderR = collisionPoint.sub(collider.getNextState().position);
            Vector2D collideeR = collisionPoint.sub(collidee.getNextState().position);

            // Find the normal of the collision.
            // Find the distance from the collision point to the edge of the object.

            /** This will be the point along the colliderR vector that intersects with the edge of
             * collidee. */
            Vector2D edgePosition = collisionPoint;
            double percentageRight = 1.0;
            double percentageLeft = 0.0;

            while ((percentageRight - percentageLeft) < PERCENTAGE_THRESHOLD) {
                double percentageCenter = (percentageLeft + percentageRight)/2;
                edgePosition = collider.getNextState().position.add(collideeR.scalarMultiply(colliderR.getMag()*percentageCenter));

                if (collidee.isIntersecting(edgePosition)) {
                    percentageLeft = percentageCenter;
                } else {
                    percentageRight = percentageCenter;
                }
            }

            double penetrationDistance = collisionPoint.sub(edgePosition).getMag();
            Log.info("Penetration distance is " + penetrationDistance + " m.");

            Vector2D point1 = null;
            Vector2D point2 = null;
            boolean lastWasIntersecting = false;
            boolean first = true;
            for (int i = 0; i < 361; i++) {
                double direction = i/180.0 * Math.PI;
                Vector2D aPoint = edgePosition.add(Vector2D.getVector2DMagnitudeAndDirection(penetrationDistance, direction));
                boolean isIntersecting = collidee.isIntersecting(aPoint);
                if (first) {
                    first = false;
                } else {
                    if (isIntersecting != lastWasIntersecting) {
                        lastWasIntersecting = isIntersecting;
                        if (point1 == null) {
                            point1 = aPoint;
                        } else {
                            point2 = aPoint;
                            break;
                        }
                    }
                }
            }
            Vector2D normalVector;
            if (point1 != null && point2 != null) {
                Vector2D edgeVector = point1.sub(point2);
                double normalDirection = edgeVector.getDirection() + Math.PI/2.0;
                normalDirection = Util.normaliseAngle(normalDirection);
                normalVector = Vector2D.getVector2DMagnitudeAndDirection(1.0, normalDirection);


                // We know the normal axis now, but we must find whether it is in the proper
                // direction, or the opposite direction.
                double dotProduct = normalVector.dotProduct(colliderR.opposite());
                // If the dot product is negative then it means the normal vector is going in the
                // opposite direction to the vector that goes from the collision point to the edge
                // point, and therefore we need to flip its direction.
                if (dotProduct < 0) {
                    normalVector = normalVector.opposite();
                }
            } else {
                String errorString = "Could not find the edge direction.";
                Log.error(errorString);
                throw new IllegalStateException(errorString);
            }

            // We now know:
            //  - the normal vector
            //  - the R vector of both objects
            //  - the state of both objects
            // Thus we can solve the collision.
            State[] states = new State[2];
            states[0] = objects[0].getNextState();
            states[1] = objects[1].getNextState();

            // For right now we are just going to flip the velocity of both objects along the
            // normal of the collision.
            states[0].velocity = states[0].velocity.reflectAlong(normalVector);
            states[1].velocity = states[1].velocity.reflectAlong(normalVector);
            // TODO: actually calculate the collision using:
            // - conservation of linear momentum
            // - conservation of angular momentum
            // - coefficient of restitution

            
//            Vector2D collisionObj0 = State.convertToLocalCoordinates(collisionPoint, objects[0].getNextState());
//            Vector2D collisionObj1 = State.convertToLocalCoordinates(collisionPoint, objects[1].getNextState());
        }
        // Either way, we have to notify the two objects that they've collided.
        objects[0].notifyCollision(objects[1], isCollisionResolved);
        objects[1].notifyCollision(objects[0], isCollisionResolved);
    }

    /**
     * Using the fine collision detection and binary search, this finds the time of the first
     * intersection.
     */
    private void findCollisionTime() {

        double rightEnd = timeStep;

        if (isColliding(0.0)) {
            Log.warning("Object started time step intersecting with other object.");
            // We'll just ignore that an object started inside of another object.
            isCollision = false;
            return;
        }

        if (!isColliding(timeStep)) {
            Log.warning("A collision was generated for objects which do not intersect at the end of the time step.");
            isCollision = false;
            return;
        }

        // From here, we know that the actually is a collision and we just have to find the actual
        // time of it.
        double leftEnd = 0.0;
        while (rightEnd - leftEnd > COLLISION_TIME_PRECISION) {
            double centerEnd = (leftEnd + rightEnd) / 2.0;
            if (isColliding(centerEnd)) {
                rightEnd = centerEnd;
            } else {
                leftEnd = centerEnd;
            }
        }

        // We take the time that is just after to collision, so that the two objects are just
        // barely intersecting.
        collisionTime = rightEnd;
    }

    private boolean isColliding(double time) {
        // Move the objects to the desired state.
        for (Collidable obj : objects) {
            obj.calculateNextState(time);
        }

        // Checking the object 0 points against object 1
        for (Vector2D point : objects[0].getCollisionPoints()) {
            Vector2D worldPosition = State.convertToWorldCoordinates(point, objects[0].getNextState());
            Vector2D localPosition = State.convertToLocalCoordinates(worldPosition, objects[1].getNextState());
            if (objects[1].isIntersecting(localPosition)) {
                collisionPoint.set(point);
                collidee = objects[1];
                collider = objects[0];
                return true;
            }
        }
        // Checking the object 1 points against object 0
        for (Vector2D point : objects[1].getCollisionPoints()) {
            Vector2D worldPosition = State.convertToWorldCoordinates(point, objects[1].getNextState());
            Vector2D localPosition = State.convertToLocalCoordinates(worldPosition, objects[0].getNextState());
            if (objects[0].isIntersecting(localPosition)) {
                collisionPoint.set(point);
                collidee = objects[0];
                collider = objects[1];
                return true;
            }
        }

        return false;
    }
}
