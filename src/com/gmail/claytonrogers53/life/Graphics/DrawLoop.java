package com.gmail.claytonrogers53.life.Graphics;



import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * The draw loop keeps a list of all of the drawable objects and draws them once per frame. It also insures that a
 * particular (adjustable) frame rate is used.
 * Created by Clayton on 13/11/2014.
 */
public class DrawLoop extends JFrame implements Runnable{

    boolean isDrawing = true;
    BufferStrategy bf;
    final int draw_dt;
    final int width = 900, height = 500;

    List<Drawable> drawableList = new ArrayList<Drawable>();

    public DrawLoop () {
        this(17);
    }

    public DrawLoop (int draw_dt) {
        this.draw_dt = draw_dt;

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //this.setUndecorated(true);
        this.setSize(width, height);
        this.setVisible(true);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(13);
        }

        this.createBufferStrategy(2);
    }

    @Override
    public void run() {
        graphicsLoop();
    }

    private void graphicsLoop () {

        long endOfLastLoopTime = System.currentTimeMillis();
        long timeToWait;

        while (isDrawing()) {

            drawScreen();

            timeToWait = (endOfLastLoopTime + draw_dt) - System.currentTimeMillis();

            System.out.println("Draw time: " + (draw_dt - timeToWait) + " ms.");
            try {
                if (timeToWait > 0) {
                    Thread.sleep(timeToWait);
                } else {
                    // so that even if the thread isn't waiting at the end of the frame,
                    // it will still stop when interrupted
                    if (Thread.interrupted()) {
                        isDrawing = false;
                    }
                }
            } catch (InterruptedException e) {
                setDrawing(false);
            }
            endOfLastLoopTime = System.currentTimeMillis();
        }
    }

    private void drawScreen () {
        BufferStrategy bf = this.getBufferStrategy();
        Graphics g = null;
        Graphics2D g2;
        Image image;
        Drawing drawing;

        try {
            g = this.getBufferStrategy().getDrawGraphics();
            g2 = (Graphics2D) g;

            // clear the entire screen so that the next frame can be drawn.
            g.clearRect(0, 0, width, height);

            for (Drawable object : drawableList) {
                drawing = object.getDrawing();
                // implement coordinate transformation system
                g2.drawImage(drawing.sprite, (int) drawing.xPosition, (int) drawing.yPosition, null);
            }
        } finally {
            if (g != null) {
                g.dispose();
            }
        }

        bf.show();
        Toolkit.getDefaultToolkit().sync();
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public void setDrawing(boolean isDrawing) {
        this.isDrawing = isDrawing;
    }

    public void addToDrawList (Drawable object) {
        drawableList.add(object);
    }

    public void removeFromDrawList (Drawable object) {
        // TODO
    }

    public void setGraphicsTimeDelta (long dt_millis) {
        // TODO
    }

    public void setFPS (int FPS) {
        // TODO
    }
}
