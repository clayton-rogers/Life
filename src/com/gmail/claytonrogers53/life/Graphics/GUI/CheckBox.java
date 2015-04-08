package com.gmail.claytonrogers53.life.Graphics.GUI;

import com.gmail.claytonrogers53.life.Util.Log;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A checkbox which can be toggled. The width is auto calculated from the contents. The height may
 * be set, but defaults to one line.
 *
 * Created by Clayton on 30/12/2014.
 */
public class CheckBox extends GUIelement {

    private int textHeight = 0;
    private int textWidth = 0;
    private String checkBoxText = "Default Checkbox Text";
    private int cornerArcRadius = 5;
    private State state = State.UNCHECKED;

    /** The pixels size of the square checkbox. */
    private static final int CHECK_BOX_SIZE = 8;

    /**
     * Checkboxes have two states as defined by the State class.
     */
    public enum State {
        CHECKED,
        UNCHECKED
    }

    /**
     * Sets the text that will be printed on the checkbox. The checkbox width will resize to
     * accommodate the text.
     *
     * @param text
     *        The text on the button.
     */
    public void setCheckBoxText(String text) {
        if (text == null) {
            Log.warning("Attempted to set checkbox text to a null string.");
            return;
        }

        synchronized (this) {
            checkBoxText = text;
            textHeight = 0;
            textWidth = 0;
        }
    }

    /**
     * Sets the height of the checkbox. If not set, the height defaults to the default GUI elements
     * height, i.e. approx one line. Note: The width is calculated automatically using the checkbox
     * text.
     *
     * The minimum height is 10 pixels. If an attempt is made to set it lower then a warning will
     * be logged and the height will be unaffected.
     *
     * @param height
     *        The desired height of the button in pixels.
     */
    public void setHeight (int height) {
        if (height < 10) {
            Log.warning("Attempted to set a checkbox height of less than 10 pixels.");
            return;
        }

        synchronized (this) {
            this.height = height;
        }
    }

    /**
     * Sets the corner arc radius in pixels. This generally does not have to be set as the default
     * value of 5 pixels looks good in most circumstances.
     *
     * @param cornerArcRadius
     *        The desired corner arc radius in pixels.
     */
    public void setCornerArcRadius (int cornerArcRadius) {
        if (cornerArcRadius <= 0) {
            Log.warning("Attempted to set a checkbox corner arc radius of less than or equal to 0.");
        }

        synchronized (this) {
            this.cornerArcRadius = cornerArcRadius;
        }
    }

    /**
     * Toggles the state of the checkbox when it is clicked.
     *
     * @param localX
     *        The x component of the click location.
     *
     * @param localY
     *        The y component of the click location.
     */
    @Override
    public void clicked(int localX, int localY) {
        synchronized (this) {
            if (state == State.CHECKED) {
                state = State.UNCHECKED;
            } else {
                state = State.CHECKED;
            }
        }
    }

    /**
     * Allows the user to explicitly set the state of the checkbox.
     *
     * @param state
     *        The desired state of the checkbox.
     */
    public void setState (State state) {
        synchronized (this) {
            this.state = state;
        }
    }

    /**
     * Allows the user to query the state of the checkbox.
     *
     * @return The state of the checkbox.
     */
    public State getState () {
        synchronized (this) {
            return state;
        }
    }

    /**
     * Draws the checkbox on the screen. Called by the graphics loop on the graphics thread.
     *
     * @param g2
     *        The graphics parameter.
     */
    @Override
    public void draw(Graphics2D g2) {
        synchronized (this) {
            // Recalculate the text/button size is this is the first time since changing the button text.
            if (textHeight == 0) {
                FontMetrics metrics = g2.getFontMetrics();
                textHeight = metrics.getAscent();
                textWidth = metrics.stringWidth(checkBoxText);

                // Calculate the button width with padding (on either side).
                width = textWidth + 3 * TEXT_MARGIN + CHECK_BOX_SIZE;
            }

            // Draw the background and outline.
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(xPos, yPos, width, height, cornerArcRadius, cornerArcRadius);
            g2.setColor(Color.BLACK);
            g2.draw(new RoundRectangle2D.Double(xPos, yPos, width, height, cornerArcRadius, cornerArcRadius));
            // Draw the text on the checkbox.
            g2.drawString(checkBoxText, xPos + TEXT_MARGIN + CHECK_BOX_SIZE + TEXT_MARGIN, yPos + (height / 2) + (textHeight / 2));
            // Draw the outside of the checkbox, then the inside.
            g2.draw(new Rectangle2D.Double(xPos + TEXT_MARGIN, yPos + (height / 2.0) - (CHECK_BOX_SIZE / 2.0), CHECK_BOX_SIZE, CHECK_BOX_SIZE));
            if (state == State.CHECKED) {
                g2.fillRect(xPos + TEXT_MARGIN, yPos + (height / 2) - (CHECK_BOX_SIZE / 2), CHECK_BOX_SIZE, CHECK_BOX_SIZE);
            }
        }
    }
}
