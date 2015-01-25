package com.gmail.claytonrogers53.life.Physics;

import java.util.List;

/**
 * Represents a force on an object.
 * Created by Clayton on 13/11/2014.
 */
public class Vector2D {

    private double magX, magY;

    public Vector2D(double magX, double magY) {
        this.magX = magX;
        this.magY = magY;
    }

    public Vector2D(Vector2D v) {
        this(v.getMagX(), v.getMagY());
    }

    public Vector2D() {
        this.magX = 0.0;
        this.magY = 0.0;
    }

    public double getMagX() {
        return magX;
    }

    public void setMagX(double magX) {
        this.magX = magX;
    }

    public double getMagY() {
        return magY;
    }

    public void setMagY(double magY) {
        this.magY = magY;
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "magX=" + magX +
                ", magY=" + magY +
                '}';
    }

    static void getSumOfVectors (List<Vector2D> vectorList, Vector2D summedVector) {
        summedVector.zero();
        for (Vector2D v : vectorList) {
            summedVector.add(v);
        }
    }

    public void zero() {
        magX = 0;
        magY = 0;
    }

    public void add (Vector2D secondVector) {
        this.magX += secondVector.getMagX();
        this.magY += secondVector.getMagY();
    }

    public void sub (Vector2D secondVector) {
        this.magX -= secondVector.getMagX();
        this.magY -= secondVector.getMagY();
    }

    public void scalarMultiply (double scalar) {
        this.magX *= scalar;
        this.magY *= scalar;
    }

    public void scalarDivide (double scalar) {
        this.magX /= scalar;
        this.magY /= scalar;
    }

    public void set (Vector2D secondVector) {
        this.magX = secondVector.getMagX();
        this.magY = secondVector.getMagY();
    }
}
