package com.gmail.claytonrogers53.life.Graphics;

import com.gmail.claytonrogers53.life.Util.Log;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * A box to contain text. The text box has a fixed width that can be set. It will be automatically
 * sized in the vertical direction to accommodate the contained text. The text and the width can be
 * changed dynamically.
 *
 * Created by Clayton on 25/1/2015.
 */
public class TextBox extends GUIelement {

    /** The text contained in the text box. */
    protected String text;
    /** Whether the height should be updated and the text reflowed on the next draw cycle. */
    protected boolean updateHeight;
    /** The same as text, but split into lines that will fit within the width of the box. */
    private final Collection<String> textInLines = new ArrayList<>(10);

    /** The height of one line of text. Updated to the correct value when the box is drawn. */
    private int lineHeight;
    /** The width that the text has. i.e. the box width minus the margins. */
    private int textWidth;

    /**
     * Creates and sets up a new TextBox.
     */
    public TextBox () {
        synchronized (this) {
            updateHeight = true;
            text = "";
        }
    }

    /**
     * Sets the width of the text box. If no width is set on a new text box, the default GUI
     * element width is used, i.e. one line (200 pixels).
     *
     * @param width
     *        The desired width of the text box in pixels.
     */
    public void setWidth(int width) {
        if (width <= 0) {
            Log.warning("Attempted to set width of a text box to less than or equal to zero.");
            return;
        }

        synchronized (this) {
            this.width = width;
            updateHeight = true;
        }
    }

    /**
     * Not used since a text box cannot be clicked.
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
     * Draws the text box on screen and reflows the text to fit in the box if the text or the width
     * has changed.
     *
     * @param g2
     *        The graphics parameter.
     */
    @Override
    void draw(Graphics2D g2) {
        synchronized (this) {
            // Reflow the text if required.
            if (updateHeight) {
                FontMetrics metrics = g2.getFontMetrics();
                lineHeight = metrics.getAscent();
                textWidth = width - 2 * TEXT_MARGIN;
                reflowText(metrics);
            }

            // Draw the outline and background.
            g2.setColor(Color.WHITE);
            g2.fillRect(xPos, yPos, width, height);
            g2.setColor(Color.BLACK);
            g2.draw(new Rectangle2D.Double(xPos, yPos, width, height));

            // Draw the text lines.
            // Note that drawString starts from the lower left corner of the text.
            int xString = xPos + TEXT_MARGIN;
            int yString = yPos + TEXT_MARGIN + lineHeight;
            for (String s : textInLines) {
                g2.drawString(s, xString, yString);
                yString += lineHeight;
            }
        }
    }

    /**
     * Reflows the text into lines that will fit in the box.
     *
     * @param metrics
     *        The font metrics parameter generated from the graphics context.
     */
    private void reflowText (FontMetrics metrics) {

        textInLines.clear();

        String[] textInWords;
        textInWords = text.split("\\s"); // \s is whitespace

        int wordPointer = 0;
        boolean outOfWords = false;

        // Until we run out of words, keep fitting them on lines.
        while (!outOfWords) {
            // Add at least one word to each line, even if it doesn't fit.
            String currentLine = textInWords[wordPointer];
            wordPointer++;
            // While we are not over our width limit, add more words.
            while(true) {
                // Check if we are out of words.
                if (wordPointer == textInWords.length) {
                    outOfWords = true;
                    break;
                }
                String lastCurrentLine = currentLine;
                currentLine += ' ' + textInWords[wordPointer];
                wordPointer++;

                // If we have exceeded the line length, we need to roll back the last word and break.
                if (metrics.stringWidth(currentLine) > textWidth) {
                    currentLine = lastCurrentLine;
                    wordPointer--;
                    break;
                }
            }
            textInLines.add(currentLine);
        }

        // Update the height of the box based on the number of lines and the margins.
        height = 2*TEXT_MARGIN + textInLines.size()*lineHeight;
    }

    /**
     * Clears all of the text out of the text box.
     */
    public void clearText () {
        synchronized (this) {
            text = "";
            updateHeight = true;
        }
    }

    /**
     * Sets the text box to contain the desired string. The height of the box is automatically
     * recalculated to fit the text.
     *
     * @param text
     *        The text to be contained in the box.
     */
    public void setText(String text) {
        if (text == null) {
            Log.warning("Tried to set a text box string to null.");
            return;
        }

        synchronized (this) {
            this.text = text;
            updateHeight = true;
        }
    }

    /**
     * Appends text to the text that is already in the box. The height of the box is automatically
     * recalculated to fit the text.
     *
     * @param text
     *        The text to be appended to the text that is already in the box.
     */
    public void appendText(String text) {
        if (text == null) {
            Log.warning("Tried to append to a text box with a null string.");
            return;
        }

        synchronized (this) {
            this.text += text;
            updateHeight = true;
        }
    }

    /**
     * Queries the text that is in the box.
     *
     * @return The text that is in the box.
     */
    public String getText () {
        synchronized (this) {
            return text;
        }
    }

    /**
     * Allows the printing of the contents of the text box class.
     *
     * @return A string representation of the class.
     */
    @Override
    public String toString() {
        synchronized (this) {
            return "TextBox{" +
                    "text='" + text + '\'' +
                    ", updateHeight=" + updateHeight +
                    ", textInLines=" + textInLines +
                    ", lineHeight=" + lineHeight +
                    ", textWidth=" + textWidth +
                    '}';
        }
    }
}
