package com.gmail.claytonrogers53.life.Experiment;

import java.awt.*;
import java.awt.geom.Rectangle2D;


public class GraphicsLoop implements Runnable{
    Graphics myg = null;
    long expected_dt = 10;
    ExpMain caller;

    GraphicsLoop (ExpMain caller) {
        this.caller = caller;
    }


    @Override
    public void run() {
        long endOfLastLoopTime = System.currentTimeMillis();
        long timeToWait;

        double xPos = 20.0;
        boolean isDrawn = false;


        while (true) {

            if (myg != null) {
                caller.repaint();
                isDrawn = true;
            }

            timeToWait = (endOfLastLoopTime + expected_dt) - System.currentTimeMillis();
            if (timeToWait > 0) {
                try {
                    Thread.sleep(timeToWait);
                } catch (InterruptedException e) {
                    System.exit(13);
                }
            }

            System.out.println("Waited: " +  timeToWait + " ms. " + isDrawn);

            endOfLastLoopTime = System.currentTimeMillis();
        }
    }

    private void drawScreen (double x) {
        Graphics2D g2 = (Graphics2D) myg;
        g2.draw(new Rectangle2D.Double(x, 20.0,
                70.0,
                70.0));
    }


}