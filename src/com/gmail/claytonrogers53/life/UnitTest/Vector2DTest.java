package com.gmail.claytonrogers53.life.UnitTest;

import static org.junit.Assert.assertEquals;

import com.gmail.claytonrogers53.life.Physics.Vector2D;
import org.junit.Test;

import java.util.ArrayList;

/**
 * UnitTest code for the {@link com.gmail.claytonrogers53.life.Physics.Vector2D} class.
 *
 * Created by Clayton on 07/12/2014.
 */
public class Vector2DTest {

    private static final double EPS = 0.001;
    Vector2D v1, v2;

    @Test
    public void alwaysPasses() {
        assertEquals(1, 1);
    }

    @Test
    public void regularConstructor() {
        v1 = new Vector2D(2.1, 4.3);
        assertEquals(2.1, v1.getMagX(), EPS);
        assertEquals(4.3, v1.getMagY(), EPS);
    }

    @Test
    public void defaultConstructor() {
        v1 = new Vector2D();
        assertEquals(0.0, v1.getMagX(), EPS);
        assertEquals(0.0, v1.getMagY(), EPS);
    }

    @Test
    public void copyConstructor() {
        v1 = new Vector2D(2.4, 51.9);
        v2 = new Vector2D(v1);
        assertEquals(2.4, v2.getMagX(), EPS);
        assertEquals(51.9, v2.getMagY(), EPS);

        // To make sure v2 is not coupled with v1.
        v1.set(1.0, 2.0);
        assertEquals(2.4, v2.getMagX(), EPS);
        assertEquals(51.9, v2.getMagY(), EPS);
    }

    @Test
    public void getVectorFromMagAndDir() {
        v1 = Vector2D.getVector2DMagnitudeAndDirection(1.0, 0.0);
        assertEquals(0.0, v1.getMagX(), EPS);
        assertEquals(1.0, v1.getMagY(), EPS);

        v1 = Vector2D.getVector2DMagnitudeAndDirection(1.0, Math.PI/2);
        assertEquals(1.0, v1.getMagX(), EPS);
        assertEquals(0.0, v1.getMagY(), EPS);

        v1 = Vector2D.getVector2DMagnitudeAndDirection(1.0, Math.PI);
        assertEquals(0.0, v1.getMagX(), EPS);
        assertEquals(-1.0, v1.getMagY(), EPS);

        v1 = Vector2D.getVector2DMagnitudeAndDirection(1.0, Math.PI * (3.0/2.0));
        assertEquals(-1.0, v1.getMagX(), EPS);
        assertEquals(0.0, v1.getMagY(), EPS);
    }

    @Test
    public void polar() {
        v1 = new Vector2D(0, 4.3);
        assertEquals(4.3, v1.getMag(), EPS);

        v1 = new Vector2D(2.1,0);
        assertEquals(2.1, v1.getMag(), EPS);

        v1 = new Vector2D(2.1, 4.3);
        assertEquals(4.785, v1.getMag(), EPS);

        v1 = new Vector2D(0.0001, 1);
        assertEquals(0.0, v1.getDirection(), EPS);

        v1 = new Vector2D(1, 0);
        assertEquals(Math.PI/2, v1.getDirection(), EPS);

        v1 = new Vector2D(0, -1);
        assertEquals(Math.PI, v1.getDirection(), EPS);

        v1 = new Vector2D(-1, 0);
        assertEquals(Math.PI * (3.0/2.0), v1.getDirection(), EPS);

        v1 = new Vector2D(.7071, .7071);
        assertEquals(Math.PI * (1.0/4.0), v1.getDirection(), EPS);

        v1 = new Vector2D(.7071, -.7071);
        assertEquals(Math.PI * (3.0/4.0), v1.getDirection(), EPS);

        v1 = new Vector2D(-.7071, -.7071);
        assertEquals(Math.PI * (5.0/4.0), v1.getDirection(), EPS);

        v1 = new Vector2D(-.7071, .7071);
        assertEquals(Math.PI * (7.0/4.0), v1.getDirection(), EPS);
    }

    @Test
    public void add() {
        v1 = new Vector2D(1.0, 3.4);
        v2 = new Vector2D(-1.0, 4.4);

        v1 = v1.add(v2);

        assertEquals(0.0, v1.getMagX(), EPS);
        assertEquals(7.8, v1.getMagY(), EPS);

        v1 = new Vector2D(-1.0, -3.4);
        v2 = new Vector2D(-1.0, 4.4);

        v1 = v1.add(v2);

        assertEquals(-2.0, v1.getMagX(), EPS);
        assertEquals(1.0, v1.getMagY(), EPS);
    }

    @Test
    public void sub() {
        v1 = new Vector2D(1.0, 3.4);
        v2 = new Vector2D(-1.0, 4.4);

        v1 = v1.sub(v2);

        assertEquals(2.0, v1.getMagX(), EPS);
        assertEquals(-1.0, v1.getMagY(), EPS);

        v1 = new Vector2D(-1.0, -3.4);
        v2 = new Vector2D(-1.0, 4.4);

        v1 = v1.sub(v2);

        assertEquals(0.0, v1.getMagX(), EPS);
        assertEquals(-7.8, v1.getMagY(), EPS);
    }

    @Test
    public void mul() {
        v1 = new Vector2D(1.0, 3.4);

        v1 = v1.scalarMultiply(3.0);

        assertEquals(3.0, v1.getMagX(), EPS);
        assertEquals(10.2, v1.getMagY(), EPS);

        v1 = new Vector2D(1.0, 3.4);

        v1 = v1.scalarMultiply(0.5);

        assertEquals(0.5, v1.getMagX(), EPS);
        assertEquals(1.7, v1.getMagY(), EPS);

        v1 = new Vector2D(1.0, 3.4);

        v1 = v1.scalarMultiply(-2.0);

        assertEquals(-2, v1.getMagX(), EPS);
        assertEquals(-6.8, v1.getMagY(), EPS);
    }

    @Test
    public void div() {
        v1 = new Vector2D(1.0, 3.3);

        v1 = v1.scalarDivide(3.0);

        assertEquals(0.3333, v1.getMagX(), EPS);
        assertEquals(1.1, v1.getMagY(), EPS);

        v1 = new Vector2D(1.0, 3.3);

        v1 = v1.scalarDivide(.5);

        assertEquals(2.0, v1.getMagX(), EPS);
        assertEquals(6.6, v1.getMagY(), EPS);

        v1 = new Vector2D(1.0, 3.3);

        v1 = v1.scalarDivide(-.5);

        assertEquals(-2.0, v1.getMagX(), EPS);
        assertEquals(-6.6, v1.getMagY(), EPS);
    }

    @Test
    public void toStringTest() {
        v1 = new Vector2D(2.1, 4.1);
        String actual = v1.toString();
        String expected = "Vector2D{magX=2.1, magY=4.1}";

        assertEquals(expected, actual);
    }

    @Test
    public void sumOfVectorsTest() {
        ArrayList<Vector2D> vectorList = new ArrayList<>();
        vectorList.add(new Vector2D( 2.1,   3.4));
        vectorList.add(new Vector2D(-4.2, -33.4));
        vectorList.add(new Vector2D( 7.1,  -5.4));
        vectorList.add(new Vector2D(-2.5,   3.5));

        v1 = Vector2D.getSumOfVectors(vectorList);
        assertEquals(2.5, v1.getMagX(), EPS);
        assertEquals(-31.9, v1.getMagY(), EPS);
    }

    @Test
    public void zeroVector() {
        v1 = new Vector2D(4.1, 7.2);
        assertEquals(4.1, v1.getMagX(), EPS);
        assertEquals(7.2, v1.getMagY(), EPS);

        v1.zero();
        assertEquals(0.0, v1.getMagX(), EPS);
        assertEquals(0.0, v1.getMagY(), EPS);
    }

    @Test
    public void setToVector() {
        v1 = new Vector2D(4.1,5.1);
        v2 = new Vector2D(7.2,4.2);
        v1.set(v2);
        assertEquals(7.2, v1.getMagX(), EPS);
        assertEquals(4.2, v1.getMagY(), EPS);
    }

    @Test
    public void setToValues() {
        v1 = new Vector2D(4.1,5.1);
        assertEquals(4.1, v1.getMagX(), EPS);
        assertEquals(5.1, v1.getMagY(), EPS);

        v1.set(1.4,5.5);
        assertEquals(1.4, v1.getMagX(), EPS);
        assertEquals(5.5, v1.getMagY(), EPS);
    }
}
