package com.gmail.claytonrogers53.life.Graphics;

import com.gmail.claytonrogers53.life.Util.Log;

import java.awt.*;

/**
 * An interface used by the graphics system to keep track of all the GUI elements and to call them when a click occurs
 * in their bounds.
 *
 * Currently available GUI elements are:
 *  - Button - Click to do something.
 *  - CheckBox - Click the option to toggle it.
 *  - ProgressBar - Shows the current progress of something. Not interactive.
 *  - RadioBox - Click the options to set one. Can be queried for their currently set option.
 *  - TextBox - Displays some text. Auto resizes to fit the text. No interactive.
 *  - TextInputBox - Same as TextBox except you can click on it to set the contained text.
 *
 * Created by Clayton on 30/12/2014.
 */
abstract class GUIelement {

    /** The x position of the GUI element. */
    protected int xPos = 0;
    /** The y position of the GUI element. */
    protected int yPos = 0;
    /** The width of the GUI element. Used to determine if the element has been clicked.
     * Default width is approx one sentence worth of text. */
    protected int width = 200;
    /** The height of the GUI element. Used to determine if the element has been clicked.
     *  Default height is approx one line of text. */
    protected int height = 17;

    /** The distance between the edge of and element and the text. */
    protected static final int TEXT_MARGIN = 5;

    /**
     * Returns the lower (left) edge of the element.
     *
     * @return X component of the left edge.
     */
    public int getLowerX() {
        return xPos;
    }


    /**
     * Returns the upper (right) edge of the element.
     *
     * @return X component of the right edge.
     */
    public int getUpperX() {
        return xPos + width;
    }

    /**
     * Returns the upper (top) edge of the element. This will be a numerically smaller value than that returned by
     * getLowerY().
     *
     * @return Y component of the top edge.
     */
    public int getUpperY() {
        return yPos;
    }

    /**
     * Returns the lower (bottom) edge of the element. This will be numerically larger than the value returned by
     * getUpperY().
     *
     * @return Y component of the bottom edge.
     */
    public int getLowerY() {
        return yPos + height;
    }

    /**
     * Checks whether a given position is within bounds of the GUI element.
     *
     * @param xPos
     *        The x position.
     *
     * @param yPos
     *        The y position.
     *
     * @return Whether the position is inside the bounds of the GUI element.
     */
    public boolean isWithinBounds(int xPos, int yPos) {
        return getLowerX() <= xPos &&
                getUpperX() >= xPos &&
                getUpperY() <= yPos &&
                getLowerY() >= yPos;
    }

    /**
     * Sets the position of the GUI element on the screen. NOTE: This position does not include offsets for the window
     * decorations, thus the point 0,0 will be in the upper left corner behind the title bar window decoration.
     *
     * @param xPos
     *        The x position of the GUI element in pixels. Zero is at right side of screen.
     *
     * @param yPos
     *        The y position of the GUI element in pixels. Zero is at the top of the screen.
     */
    public void setPosition(int xPos, int yPos) {
        if (xPos < 0) {
            Log.warning("Tried to set the x pos of a GUI element to less than zero.");
            return;
        }
        if (yPos < 0) {
            Log.warning("Tried to set the y pos of a GUI element to less than zero.");
            return;
        }

        synchronized (this) {
            this.xPos = xPos;
            this.yPos = yPos;
        }
    }

    /**
     * When the graphics system determines that the GUI element has been clicked, it will call this method. The
     * coordinates are the local coordinates within the bounds of the element.
     *
     * @param localX
     *        The x component of the click location.
     *
     * @param localY
     *        The y component of the click location.
     */
    abstract void clicked (int localX, int localY);


    /**
     * The method is called every graphics loop to draw the element on the screen.
     *
     * @param g2
     *        The graphics parameter.
     */
    abstract void draw(Graphics2D g2);
}
