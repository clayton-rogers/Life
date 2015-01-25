package com.gmail.claytonrogers53.life.Graphics;

import com.gmail.claytonrogers53.life.Configuration.ConfigFormatException;
import com.gmail.claytonrogers53.life.Configuration.Configuration;
import com.gmail.claytonrogers53.life.Configuration.ValueNotConfiguredException;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * The draw loop keeps a list of all of the drawable objects and draws them once per frame. It also insures that a
 * particular (adjustable) frame rate is used.
 *
 * Created by Clayton on 13/11/2014.
 */
public final class DrawLoop extends JFrame implements Runnable{

    /** The default refresh time used if one is not specified in the configuration file. */
    final static public long DEFAULT_DT = 17;

    /** The default window width if one is not specified in the configuration file. */
    final static public int DEFAULT_WIDTH = 900;

    /** The default window height if one is not specified in the configuration file. */
    final static public int DEFAULT_HEIGHT = 500;

    /**
     * The default window zoom level if one is not specified in the configuration file. A zoom level of 1.0
     * means that one meter will show up as one pixel. We will typically, use zoom level of higher than 1.0 since most
     * objects will be smaller than a meter.
     */
    final static public double DEFAULT_ZOOM = 200.0;

    /** The default X centre of the screen in meters. */
    final static public double DEFAULT_PAN_X = 0.0;

    /** The default Y centre of the screen in meters. */
    final static public double DEFAULT_PAN_Y = 0.0;

    // Actual instance variables. For all these variables we will user "this" as the locking object.
    private long                draw_dt       = DEFAULT_DT;
    private int                 width         = DEFAULT_WIDTH;
    private int                 height        = DEFAULT_HEIGHT;
    private double              zoom          = DEFAULT_ZOOM;
    private double              panX          = DEFAULT_PAN_X;
    private double              panY          = DEFAULT_PAN_Y;
    private volatile boolean    isDrawing     = true;

    /** The list of objects that will be drawn every loop */
    private final List<Drawable> drawableList = new ArrayList<>();

    /**
     * Constructs a new runnable DrawLoop object. If the window settings are set in the configuration file then they
     * are read.
     */
    public DrawLoop () {

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //this.setUndecorated(true);
        try {
            if (Configuration.isSet("WINDOW_WIDTH")) {
                width = Configuration.getValueInt("WINDOW_WIDTH");
            }
            if (Configuration.isSet("WINDOW_HEIGHT")) {
                height = Configuration.getValueInt("WINDOW_HEIGHT");
            }
            if (Configuration.isSet("DRAW_DT")){
                draw_dt = Configuration.getValueInt("DRAW_DT");
            }
        } catch (ValueNotConfiguredException e) {
            // Since we're checking whether these values have been set, this exception should never happen.
            // So just print a stack trace and kill the thread.
            Thread.dumpStack();
            isDrawing = false;
        } catch (ConfigFormatException e) {
            // TODO-IMPROVEMENT: For now we will just do the same as if it was not configured, but maybe should not
            // crash.

            // Since we're checking whether these values have been set, this exception should never happen.
            // So just print a stack trace and kill the thread.
            Thread.dumpStack();
            isDrawing = false;
        }
        this.setSize(width, height);
        this.setVisible(true);

        // This sleep is here in hopes of stopping the exception that sometimes happens when creating the buffer
        // strategy later on.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(13);
        }

        this.createBufferStrategy(2);
    }

    /**
     * Called by the new Thread to start the drawing.
     */
    @Override
    public void run() {
        graphicsLoop();
    }

    /**
     * The main graphics/drawing loop.
     */
    private void graphicsLoop () {

        long endOfLastLoopTime = System.currentTimeMillis();
        long timeToWait;

        while (isDrawing) {
            // TODO-IMPROVEMENT: Use nanosecond timers.

            drawScreen();

            // After draw screen has completed, there still may be lots of time left in the frame. So we will wait
            // until the frame has expired. The time to wait is the time left in the current frame.
            synchronized (this) { // Because we are reading draw_dt.
                timeToWait = (endOfLastLoopTime + draw_dt) - System.currentTimeMillis();
            }
            try {
                if (timeToWait > 0) {
                    Thread.sleep(timeToWait);
                } else {
                    // If the graphics thread is overloaded and never sleeps, we still need to check if we have been
                    // interrupted so that we will stop when we have.
                    if (Thread.interrupted()) {
                        stopDrawing();
                    }
                }
            } catch (InterruptedException e) {
                stopDrawing();
            }
            endOfLastLoopTime = System.currentTimeMillis();
        }
    }

    /**
     * Draws the current frame on the back buffer and then shows it to the screen. Called once every draw loop.
     */
    private void drawScreen () {
        // TODO-IMPROVEMENT: Add support for GUI elements which do not pan or zoom, they stay the same size and
        // position.
        BufferStrategy bf = this.getBufferStrategy();
        Graphics g = null;
        Graphics2D g2;
        Drawing drawing;

        try {
            g = this.getBufferStrategy().getDrawGraphics();
            g2 = (Graphics2D) g;

            // Get the old transform so we can set it back after each object.
            AffineTransform oldAf = g2.getTransform();

            // Clear the entire screen so that the next frame can be drawn.
            synchronized (this) { // Because we are reading width and height.
                g.clearRect(0, 0, width, height);
            }

            // This synchronized section is in effect locking the drawable list, so that it is not changed by another
            // thread while we are drawing it. In the case where we spend a majority of the time drawing, this could
            // cause delays when we try to add or remove an object from the draw loop.
            synchronized (drawableList) {
                for (Drawable object : drawableList) {
                    drawing = object.getDrawing();

                    // Because all the screen objects work in a normal coordinate system. i.e. with metres, but java2D
                    // uses pixels starting in the top left, we must convert the coordinates.
                    convertToScreenCoordinates(drawing);

                    // Set a transform to use for the drawing
                    AffineTransform af = new AffineTransform(oldAf);
                    af.translate(drawing.xPosition, drawing.yPosition);
                    af.scale(drawing.spriteZoom, drawing.spriteZoom);
                    af.rotate(drawing.rotation);

                    // TODO-BUG: Rotation is around top left corner rather than the center of the object.

                    g2.drawImage(drawing.sprite, af, null);
                }
            }
        } finally {
            if (g != null) {
                g.dispose();
            }
        }

        bf.show();
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Considers the current view position and zoom and uses it to convert the drawing into something that can be
     * drawn. It needs to be synchronized because is reads the pan and zoom.
     *
     * @param inputDrawing
     *        The drawing coming from the drawable object.
     */
    private synchronized void convertToScreenCoordinates (Drawing inputDrawing) {
        // This transform runs on every drawable object every graphics loop, so it needs to be kind of fast.

        // At a zoom level of 1.0 and zero pan vector, the centre of the screen should be (0 m, 0 m) and a line of 1 m
        // length should be 1 pixel long.
        // If the pan is something other than (0 m, 0 m) then that point will be at the centre of the screen.
        // If the zoom level is something other than 1, then the size in meters of the object will be multiplied by that
        // to obtain its size in pixels.

        // Translate the points by the pan of the screen. (Since the pan is in meters, we do this before the scaling.)
        inputDrawing.xPosition -= panX;
        inputDrawing.yPosition -= panY;

        // Scale the inputs to the zoom level, they are now in pixel coordinates.
        inputDrawing.xPosition *= zoom;
        inputDrawing.yPosition *= zoom;

        // Flit the y and translate the coordinates because on the screen the origin is at the upper left hand corner.
        inputDrawing.xPosition += width/2.0;
        inputDrawing.yPosition *= -1;
        inputDrawing.yPosition += height/2.0;

        // We are going to let the drawing transforms actually take care of transforming the sprite. However, we do need
        // translate the zoom of the sprite to the zoom of the screen.
        inputDrawing.spriteZoom *= zoom;

        // Translate the coordinates for the distance from the centre to the top corner of the sprite.
        inputDrawing.xPosition -= inputDrawing.sprite.getWidth(null)  *inputDrawing.spriteZoom /2.0;
        inputDrawing.yPosition -= inputDrawing.sprite.getHeight(null) *inputDrawing.spriteZoom /2.0;
    }

    /**
     * Allows other threads with a reference to the draw loop to stop it drawing and quit. This is the normal way of
     * stopping the draw thread when one is done with it.
     */
    public void stopDrawing () {
        // isDrawing is volatile therefore we do not need to obtain a lock here.
        isDrawing = false;
    }

    /**
     * Adds an object to the list of objects to be draw every frame. The object will be draw until it is explicitly
     * removed with removeFromDrawList or clearDrawList. If the object is already in the draw list, it will not be
     * added again.
     *
     * @param object
     *        The drawable object to be added to the draw list.
     *
     * @see #removeFromDrawList
     * @see #clearDrawList
     */
    public void addToDrawList (Drawable object) {
        if (object == null) {
            Thread.dumpStack();
            System.exit(13);
        }

        synchronized (drawableList) {
            if (!drawableList.contains(object)) {
                drawableList.add(object);
            }
        }
    }

    /**
     * Removes the given object from the draw list.
     *
     * @param object
     *        The object to be removed from the draw list.
     *
     * @see #clearDrawList
     */
    public void removeFromDrawList (Drawable object) {
        if (object == null) {
            Thread.dumpStack();
            System.exit(13);
        }

        synchronized (drawableList) {
            drawableList.remove(object);
        }
    }

    /**
     * Clears every object out of the draw list. This will clear every object off of the screen. This method should be
     * used very sparingly. It will have better performance than calling removeFromDrawList on every object.
     *
     * @see #removeFromDrawList
     */
    public void clearDrawList () {
        synchronized (drawableList) {
            drawableList.clear();
        }
    }

    /**
     * Sets the current graphics refresh rate. Note that the dt can be set to 0 ms to have the graphics system run as
     * fast as it can.
     *
     * @param dt_millis
     *        The desired frame time in ms.
     *
     * @see #setFPS
     */
    public synchronized void setGraphicsTimeDelta (long dt_millis) {
        if (dt_millis < 0) return;

        draw_dt = dt_millis;
    }

    /**
     * Sets the current graphics refresh rate. As the graphics system only has ms resolution, the max settable FPS is
     * 1000.
     *
     * @param FPS
     *        The desired frames per second.
     *
     * @see #setGraphicsTimeDelta
     */
    public synchronized void setFPS (int FPS) {
        if (FPS <= 0) return;
        if (FPS > 1000) FPS = 1000;

        draw_dt = 1000/FPS;
    }

    /**
     * Sets the zoom of the current viewing area. A zoom of 1.0 will show a 1 m line as 1 pixel, a zoom of 2.0 will
     * show a 1 m line as 2 pixels
     *
     * @param zoom
     *        The desired zoom level.
     */
    public synchronized void setZoom(double zoom) {
        if (zoom <= 0.0) return;
        this.zoom = zoom;
    }

    /**
     * Sets the current centre of the screen in meters.
     *
     * @param panX
     *        The x component of the desired centre of screen.
     *
     * @param panY
     *        The y component of the desired centre of screen.
     */
    public synchronized void setPan(double panX, double panY) {
        this.panX = panX;
        this.panY = panY;
    }
}