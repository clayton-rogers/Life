package com.gmail.claytonrogers53.life.Graphics;

import com.gmail.claytonrogers53.life.Util.Log;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.Callable;

/**
 * A button which can be clicked. The width of the button is auto calculated from the text on the
 * button but the height must be set. The code which is run when the button is clicked needs to be
 * set with the registerCallback() method.
 *
 * Created by Clayton on 30/12/2014.
 */
public class Button extends GUIelement {

    private int textHeight = 0;
    private int textWidth = 0;
    /** The method that we will call when clicked. */
    private Callable<Void> callback = null;
    /** The text that appears on the button. */
    private String buttonText = "Default Button Text";
    private int cornerArcRadius = 5;

    /**
     * Sets the text that will be printed on the button. The button width will resize to
     * accommodate the text.
     *
     * @param text
     *        The text on the button.
     */
    public void setButtonText(String text) {
        if (text == null) {
            Log.warning("Attempted to set button text to a null string.");
            return;
        }

        synchronized (this) {
            buttonText = text;
            textHeight = 0;
            textWidth = 0;
        }
    }

    /**
     * Sets the height of the button. If not set, it will default to the default GUI element
     * height, i.e. approx one line. Note: The width is calculated automatically using the button
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
            Log.warning("Attempted to set a button height of less than 10 pixels.");
            return;
        }

        synchronized (this) {
            this.height = height;
        }
    }

    /**
     * Sets the callback function of the button.
     *
     * @param callback
     *        The callable object.
     */
    public void registerCallback (Callable<Void> callback) {
        synchronized (this) {
            this.callback = callback;
        }
    }

    /**
     * Sets the corner arc radius in pixels. This does not generally have to be set as the default
     * of 5 pixels looks fine is most cases.
     *
     * @param cornerArcRadius
     *        The desired corner arc radius in pixels.
     */
    public void setCornerArcRadius (int cornerArcRadius) {
        if (cornerArcRadius <= 0) {
            Log.warning("Attempted to set a button corner arc radius of less than or equal to 0.");
            return;
        }

        synchronized (this) {
            this.cornerArcRadius = cornerArcRadius;
        }
    }

    /**
     * This method is called when the button is clicked. If a callback has been registered, then it
     * is called. Otherwise, the button does nothing.
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
            if (callback != null) {
                try {
                    callback.call();
                } catch (Exception e) {
                    Log.warning("An exception was thrown when performing a button callback.");
                    Log.warning(e.toString());
                }
            } else {
                Log.warning("A button was clicked which has no callback.");
            }
        }
    }

    /**
     * Draws the button on the screen.
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
                textWidth = metrics.stringWidth(buttonText);

                // Calculate the button width with padding (on either side).
                width = textWidth + 2 * TEXT_MARGIN;
            }

            // Draw the background and outline.
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(xPos, yPos, width, height, cornerArcRadius, cornerArcRadius);
            g2.setColor(Color.BLACK);
            g2.draw(new RoundRectangle2D.Double(xPos, yPos, width, height, cornerArcRadius, cornerArcRadius));
            // Draw the text on the button.
            g2.drawString(buttonText, xPos + TEXT_MARGIN, yPos + (height / 2) + (textHeight / 2));
        }
    }
}
