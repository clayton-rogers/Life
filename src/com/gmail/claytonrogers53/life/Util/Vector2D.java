package com.gmail.claytonrogers53.life.Util;

import java.util.List;

/**
 * A two dimensional vector.
 *
 * Created by Clayton on 13/11/2014.
 */
public class Vector2D {

    /** The internal representation of the vector. */
    private double magX, magY;

    /**
     * Constructs a 2D vector from the given components.
     *
     * @param magX
     *        The x component of the new vector.
     *
     * @param magY
     *        The y component of the new vector.
     */
    public Vector2D(double magX, double magY) {
        this.magX = magX;
        this.magY = magY;
    }

    /**
     * Copy constructor. Performs a deep copy of the vector.
     *
     * @param vector
     *        The vector to be copied.
     */
    public Vector2D(Vector2D vector) {
        this(vector.getMagX(), vector.getMagY());
    }

    /**
     * Creates a new vector with the default value of (0, 0)
     */
    public Vector2D() {
        this.magX = 0.0;
        this.magY = 0.0;
    }

    /**
     * Creates a new Vector2D with the given magnitude and direction.
     *
     * @param magnitude
     *        The absolute magnitude (length) of the vector.
     * @param direction
     *        The direction of the vector (radians). North is zero, east is pi/2. Does not have to be normalised to the
     *        range 0 to 2pi.
     *
     * @return A new vector with the given direction and magnitude.
     */
    public static Vector2D getVector2DMagnitudeAndDirection (double magnitude, double direction) {
        while (direction < 0) {
            direction += 2 * Math.PI;
        }
        while (direction > 2 * Math.PI) {
            direction -= 2 * Math.PI;
        }

        double xComponent = Math.sin(direction) * magnitude;
        double yComponent = Math.cos(direction) * magnitude;

        return new Vector2D(xComponent, yComponent);
    }

    /**
     * Returns the x component of the vector.
     *
     * @return The x component of the vector.
     */
    public double getMagX() {
        return magX;
    }

    /**
     * Returns the y component of the vector.
     *
     * @return The y component of the vector.
     */
    public double getMagY() {
        return magY;
    }

    /**
     * Returns the absolute magnitude (length) of the vector.
     *
     * @return The magnitude of the vector.
     */
    public double getMag() {
        return Math.sqrt(
                Math.pow(magX, 2) +
                Math.pow(magY, 2)
        );
    }

    /**
     * Returns the direction of the vector. Zero is north, pi/2 is east. The return value will always be a value in the
     * range 0 to 2pi.
     *
     * @return The direction of the vector in radians.
     */
    public double getDirection() {
        double value = Math.atan(magY/magX);
        // Convert to clockwise.
        value *= -1;
        // And move zero to north (instead of east).
        value += Math.PI/2;

        if (magX < 0) {
            value += Math.PI;
        }

        // Normalise to the expected range.
        value = value > Math.PI*2 ?
                value - Math.PI*2 :
                value;

        return value;
    }

//    public void setMagX(double magX) {
//        this.magX = magX;
//    }

//    public void setMagY(double magY) {
//        this.magY = magY;
//    }

    /**
     * Allows for an easy way of printing the vector. Returns a string in the format:
     * Vector2D{magX=<i>mag_x</i>, magY=<i>mag_y</i>}
     *
     * @return A string representation of the vector, in the format defined above.
     */
    @Override
    public String toString() {
        return "Vector2D{" +
                "magX=" + magX +
                ", magY=" + magY +
                '}';
    }

    /**
     * Sums the vectors in the list and returns the value.
     *
     * @param vectorList
     *        The list of vectors to be added together.
     *
     * @return The result of the summation.
     */
    public static Vector2D getSumOfVectors (List<Vector2D> vectorList) {
        Vector2D outputVector = new Vector2D(0.0, 0.0);

        for (Vector2D v : vectorList) {
            outputVector = outputVector.add(v);
        }

        return outputVector;
    }

    /**
     * Resets the vector to zero.
     */
    public void zero() {
        magX = 0;
        magY = 0;
    }

    /**
     * Adds the given vector to the current vector. And returns the result in a new vector.
     *
     * @param secondVector
     *        The vector to be added to the current vector.
     *
     * @return The result of the addition.
     */
    public Vector2D add (Vector2D secondVector) {
        if (secondVector == null) {
            throw new IllegalArgumentException("Vector to add must not be null.");
        }
        return new Vector2D(
                this.magX + secondVector.getMagX(),
                this.magY + secondVector.getMagY()
        );
    }

    /**
     * Subtracts the given vector from the current vector. And returns the result in a new vector.
     *
     * @param secondVector
     *        The vector to be subtracted from the current vector.
     *
     * @return The result of subtraction.
     */
    public Vector2D sub (Vector2D secondVector) {
        if (secondVector == null) {
            throw new IllegalArgumentException("Vector to sub must not be null.");
        }
        return new Vector2D(
                this.magX - secondVector.getMagX(),
                this.magY - secondVector.getMagY()
        );
    }

    /**
     * Multiplies the magnitude of the vector by a scalar. And returns the result in a new vector.
     *
     * @param scalar
     *        The scalar to multiply the vector by.
     *
     * @return The result of the multiplication.
     */
    public Vector2D scalarMultiply (double scalar) {
        return new Vector2D(
                this.magX * scalar,
                this.magY * scalar
        );
    }

    /**
     * Divides the magnitude of a vector by a scalar. And returns the result in a new vector.
     *
     * @param scalar
     *        The scalar to multiply the vector by.
     *
     * @return The result of the divide.
     */
    public Vector2D scalarDivide (double scalar) {
        return new Vector2D(
            this.magX / scalar,
            this.magY / scalar
        );
    }

    /**
     * Sets an existing vector to be equal to another vector.
     *
     * @param secondVector
     *        The vector to set this vector equal to. 
     */
    public void set (Vector2D secondVector) {
        this.magX = secondVector.getMagX();
        this.magY = secondVector.getMagY();
    }

    /**
     * Set the components of an existing vector.
     *
     * @param x
     *        The desired x component.
     *
     * @param y
     *        The desired y component.
     */
    public void set(double x, double y) {
        this.magX = x;
        this.magY = y;
    }
}
