package com.gmail.claytonrogers53.life.Graphics;

import com.gmail.claytonrogers53.life.Util.Log;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Shows a progress bar. The percent completed is set dynamically. The width and height can be set.
 *
 * Created by Clayton on 25/1/2015.
 */
public class ProgressBar extends GUIelement {

    /** The progress location of the progress bar. Goes zero to PROGRESS_MAX_PERCENT. */
    private int progress = 0;
    /** The maximum of the progress bar. Always 100 (percent).*/
    private static final int PROGRESS_MAX_PERCENT = 100;

    /**
     * Does nothing since progress bars can't be clicked.
     *
     * @param localX
     *        The x component of the click location.
     *
     * @param localY
     *        The y component of the click location.
     */
    @Override
    void clicked(int localX, int localY) {
        // Do nothing.
    }

    /**
     * Draws the progress bar on the screen.
     *
     * @param g2
     *        The graphics object.
     */
    @Override
    void draw(Graphics2D g2) {
        synchronized (this) {
            // Draw the white background, then the outline, then the solid black part.
            g2.setColor(Color.WHITE);
            g2.fillRect(xPos, yPos, width, height);
            g2.setColor(Color.BLACK);
            g2.draw(new Rectangle2D.Double(xPos, yPos, width, height));
            int progressPixels = (int) (width * (double) progress /PROGRESS_MAX_PERCENT);
            g2.fillRect(xPos, yPos, progressPixels, height);
        }

    }

    /**
     * Sets the current progress of the progress bar. Does not have to always increase. Giving a
     * value outside the 0 to 100 range will not change the current progress.
     *
     * @param progress
     *        The current progress of the progress bar (percent).
     */
    public void setProgress (int progress) {
        if (progress < 0) {
            Log.warning("Tried to set a progress of less than zero.");
            return;
        }
        if (progress > PROGRESS_MAX_PERCENT) {
            Log.warning("Tried to set a progress of greater than one hundred.");
            return;
        }

        synchronized (this) {
            this.progress = progress;
        }
    }

    /**
     * Sets the width of the progress bar in pixels. If not set, it defaults to the default GUI
     * element width, i.e. approx one sentence.
     *
     * @param width
     *        The desired width of the progress bar in pixels.
     */
    public void setWidth (int width) {
        if (width <= 0) {
            Log.warning("Tried to set the width of a progress bar to less than or equal to zero.");
            return;
        }

        synchronized (this) {
            this.width = width;
        }
    }

    /**
     * Sets the height of the progress bar in pixels. If not set, it defaults to the default GUI
     * element height, i.e. approx one line.
     *
     * @param height
     *        The desired height of the progress bar in pixels.
     */
    public void setHeight (int height) {
        if (height <= 0) {
            Log.warning("Tried to set the height of the progress bar to less than or equal to zero.");
            return;
        }

        synchronized (this) {
            this.height = height;
        }
    }
}
