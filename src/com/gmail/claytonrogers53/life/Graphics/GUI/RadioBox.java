package com.gmail.claytonrogers53.life.Graphics.GUI;

import com.gmail.claytonrogers53.life.Util.Log;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A radio box which can be set to one of a number of possibilities. The height is auto calculated
 * based on the number of options and the width is auto calculated based on the length of the
 * longest option.
 *
 * Created by Clayton on 30/12/2014.
 */
public class RadioBox extends GUIelement {

    /** The height of a single row. Set to zero when the box size need to be recalculated. */
    private int textHeight = 0;
    /** This list of options. Displayed as is. */
    private List<String> optionList = null;
    /** The currently selected option. */
    private int selectedOptionIndex = 0;
    /** The radius of the corner arc. Generally never changed. */
    private int cornerArcRadius = 5;

    /** The pixels size of the square checkbox. */
    private static final int CHECK_BOX_SIZE = 8;

    /**
     * Sets the list of possible options of the radio box. Must be done at least once for the radio
     * box otherwise it will not display on screen.
     *
     * @param optionList
     *        A list of strings to be used as the options text.
     */
    public void setOptionList(List<String> optionList) {
        if (optionList == null) {
            Log.warning("Tried to set a radio box option list to null.");
            return;
        }

        synchronized (this) {
            this.optionList = new ArrayList<>(optionList);
        }
    }

    /**
     * Allows a list of the possible options to be queried. If the option list has not yet been
     * populated, then it will return null.
     *
     * @return A string list of the possible options.
     */
    public List<String> getOptionList() {
        synchronized (this) {
            if (optionList == null) {
                Log.warning("Attempted to get an option list but it hasn't yet been set.");
                return null;
            }
            return new ArrayList<>(optionList);
        }
    }

    /**
     * Allows the text of the currently selected option to be queried.
     *
     * @return The text of the currently selected option. Returns an empty string if the options
     *         have not yet been set.
     */
    public String getSelectedOption() {
        synchronized (this) {
            if (optionList == null) {
                Log.warning("Tried to retrieve the selected option, but there are no options.");
                return "";
            }
            return optionList.get(selectedOptionIndex);
        }
    }

    /**
     * Allows the currently selected index to be queried.
     *
     * @return The index of the currently selected option (zero indexed). Returns -1 if the options
     *         have not yet been set.
     */
    public int getSelectedOptionIndex() {
        synchronized (this) {
            if (optionList == null) {
                Log.warning("Tried to retrieve the selected option index, but there are no options.");
                return -1;
            }
            return selectedOptionIndex;
        }
    }

    /**
     * Allows the currently selected index to be set externally. Has no effect if the option list
     * does not exist or if the index argument is out of range.
     *
     * @param optionIndex
     *        The index of the option to be selected (zero indexed).
     */
    public void setSelectedOption(int optionIndex) {
        synchronized (this) {
            if (optionList == null) {
                Log.warning("Tried to set a radio box index but there is no options.");
                return;
            }
            if (optionIndex < 0) {
                Log.warning("Tried to set a radio box index to less than zero.");
                return;
            }
            if (optionIndex > optionList.size()-1) {
                Log.warning("Tried to set a radio box index to greater than the max amount(" + (optionList.size()-1) + ").");
                return;
            }

            selectedOptionIndex = optionIndex;
        }
    }

    /**
     * Sets the corner arc radius in pixels. This generally does not have to be set as the default
     * value of 5 pixels looks good in most cases.
     *
     * @param cornerArcRadius
     *        The desired corner arc radius in pixels.
     */
    public void setCornerArcRadius (int cornerArcRadius) {
        if (cornerArcRadius < 0) {
            Log.warning("Attempted to set a radio box corner arc radius of less than 0.");
            return;
        }
        synchronized (this) {
            this.cornerArcRadius = cornerArcRadius;
        }
    }

    /**
     * Sets the currently selected option when the radio box is clicked. Does not change the
     * selection if the upper or lower margins are selected. If the option list has not yet been
     * set, then the click will have no effect.
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
            if (textHeight == 0) {
                // We haven't drawn the box yet and therefore don't know how big anything is. So
                // just drop the click.
                return;
            }
            if (optionList == null) {
                // We don't have an option list yet and therefore can't be clicked.
                return;
            }

            // Now we know for sure we hit one of the lines, just have to figure out which.
            // Since this is int division, it will always properly give the lower bound.
            selectedOptionIndex = (localY-TEXT_MARGIN)/(textHeight+TEXT_MARGIN);
            if (selectedOptionIndex > optionList.size()-1) {
                // We need this because on the very lowest pixel of the last entry, and the lower
                // margin, it will otherwise return max+1.
                selectedOptionIndex = optionList.size()-1;
            }
        }
    }

    /**
     * Draws the radio box on the screen. Called by the graphics loop on the graphics thread.
     *
     * @param g2
     *        The graphics parameter.
     */
    @Override
    public void draw(Graphics2D g2) {
        synchronized (this) {
            // If there are no options then there is nothing to do.
            if (optionList == null) {
                return;
            }

            // Recalculate the text/button size is this is the first time since changing the button text.
            if (textHeight == 0) {
                FontMetrics metrics = g2.getFontMetrics();
                textHeight = metrics.getAscent();
                // Go through each of the line and size the box to the longest one.
                int textWidth = 0;
                for (String option : optionList) {
                    int tempTextWidth = metrics.stringWidth(option);
                    textWidth = tempTextWidth > textWidth ? tempTextWidth : textWidth;
                }

                // Calculate the button width with padding (on either side).
                width = textWidth + 3 * TEXT_MARGIN + CHECK_BOX_SIZE;
                height = textHeight*optionList.size() + TEXT_MARGIN + optionList.size()*TEXT_MARGIN;
            }

            // Draw the background and outline.
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(xPos, yPos, width, height, cornerArcRadius, cornerArcRadius);
            g2.setColor(Color.BLACK);
            g2.draw(new RoundRectangle2D.Double(xPos, yPos, width, height, cornerArcRadius, cornerArcRadius));
            // For each of the options, draw the checkbox and the text.
            int index = 0;
            int yTopCorner = yPos+TEXT_MARGIN;
            int xTextCorner = xPos + TEXT_MARGIN + CHECK_BOX_SIZE + TEXT_MARGIN;
            for (String option : optionList) {

                // Draw the text on the button. (Origin of the text draw is the bottom corner.
                g2.drawString(option, xTextCorner, yTopCorner+textHeight);
                // Draw the outside of the checkbox, then the inside. (Origin of rectangle is the top corner.)
                // The ones are magical to get the boxes to line up properly.
                g2.draw(new Rectangle2D.Double(xPos + TEXT_MARGIN, yTopCorner+TEXT_MARGIN-1, CHECK_BOX_SIZE, CHECK_BOX_SIZE));
                if (selectedOptionIndex == index) {
                    g2.fillRect(xPos + TEXT_MARGIN, yTopCorner+TEXT_MARGIN-1, CHECK_BOX_SIZE, CHECK_BOX_SIZE);
                }

                index++;
                yTopCorner += textHeight + TEXT_MARGIN;
            }
        }
    }
}
