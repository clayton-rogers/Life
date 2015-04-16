package com.gmail.claytonrogers53.life.Graphics.GUI;

import com.gmail.claytonrogers53.life.Util.Log;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Provides a ready made dialog to ask the user a question. The yes and no buttons can optionally
 * be changed to be ok and cancel buttons, or they can have any other text. The dialog also allows
 * the programmer to block the thread until the user presses one of the two buttons.
 *
 * Created by Clayton on 13/04/2015.
 */
public class Dialog extends GUIElement{

    private static final int SPACE_BETWEEN_BUTTON_AND_TEXT = 5;
    private static final long TIME_TO_WAIT_BEFORE_CHECKING_INPUT = 10L; // milliseconds

    /** True when the user has clicked the yes button. */
    private boolean isYesClicked;
    /** True when the user has clicked the no button. */
    private boolean isNoClicked;
    /** True when the user as clicked either of the buttons. This value is reset when {@link #reset()} is
     *  called or when {@link #waitForButtonPress()} is called. */
    private boolean isButtonPressed;

    private final Button yesButton = new Button();
    private final Button noButton = new Button();
    private final TextBox textBox = new TextBox();

    public Dialog() {
        yesButton.setButtonText("Yes");
        noButton.setButtonText("No");

        yesButton.registerCallback(new Callable<Void>() {
            @Override
            public Void call() {
                isYesClicked = true;
                isNoClicked = false;
                isButtonPressed = true;
                return null;
            }
        });

        noButton.registerCallback(new Callable<Void>() {
            @Override
            public Void call() {
                isYesClicked = false;
                isNoClicked = true;
                isButtonPressed = true;
                return null;
            }
        });
    }

    /**
     * Sets the width of the dialog.
     *
     * @param width
     *        The desired width in pixels of the dialog.
     */
    public void setWidth(int width) {
        if (width <= 0) {
            Log.warning("Attempted to set a width of a dialog to less than or equal to zero.");
            return;
        }

        synchronized (this) {
            this.width = width;
        }
    }

    /**
     * Changes the "Yes" button to have a different text.
     *
     * @param text
     *        The text to put on the yes button.
     */
    public void setYesButtonText(String text) {
        if (text == null) {
            Log.warning("Tried to set the yes button text to null.");
            return;
        }
        yesButton.setButtonText(text);
    }

    /**
     * Changes the "No" button to have a different text.
     *
     * @param text
     *        The text to go on the no button.
     */
    public void setNoButtonText(String text) {
        if (text == null) {
            Log.warning("Tried to set the no button text to null.");
            return;
        }
        noButton.setButtonText(text);
    }

    /**
     * Sets the text to be displayed in the dialog.
     *
     * @param text
     *        The new dialog text.
     */
    public void setDialogText(String text) {
        if (text == null) {
            Log.warning("Tried to set the dialog text to null.");
            return;
        }
        textBox.setText(text);
    }

    /**
     * Appends text to the dialog.
     *
     * @param text
     *        The text to append to the dialog.
     */
    public void appendDialogText (String text) {
        if (text == null) {
            Log.warning("Tried to append null text to a dialog.");
        }
        textBox.appendText(text);
    }

    /**
     * Blocks the calling thread until one of the buttons are pressed or until the given time has
     * elapsed.
     *
     * @param maxWait
     *        The maximum time to block the thread for in milliseconds..
     */
    public void waitForButtonPress(long maxWait) {
        synchronized (this) {
            isButtonPressed = false;
        }

        long numberOfIterations = maxWait/TIME_TO_WAIT_BEFORE_CHECKING_INPUT;

        while (numberOfIterations > 0L) {
            synchronized (this) {
                if (isButtonPressed) {
                    break;
                }
            }
            try {
                Thread.sleep(TIME_TO_WAIT_BEFORE_CHECKING_INPUT);
            } catch (InterruptedException e) {
                Log.warning("A dialog was interrupted while waiting on a button press.");
                Log.warning(e.toString());
                return;
            }
            numberOfIterations--;
        }
    }

    /**
     * Blocks the calling thread until one of the buttons are pressed.
     */
    public void waitForButtonPress() {
        // This will work for a very long time as long a TIME_TO_WAIT is not too small.
        waitForButtonPress(Long.MAX_VALUE);
    }

    /**
     * Resets the dialog so that neither of the buttons are considered pressed.
     */
    public void reset() {
        synchronized (this) {
            isButtonPressed = false;
        }
    }

    /**
     * Returns true when the last button to be pressed was the yes button. Will not return true if
     * {@link #reset()} has been called since the button was pressed.
     *
     * @return True when the yes button was pressed.
     */
    public boolean wasYesClicked() {
        synchronized (this) {
            return isButtonPressed && isYesClicked;
        }
    }

    /**
     * Returns true when the last button to be pressed was the no button. Will not return true if
     * {@link #reset()} has been called since the button was pressed.
     *
     * @return True when the no button was pressed.
     */
    public boolean wasNoClicked() {
        synchronized (this) {
            return isButtonPressed && isNoClicked;
        }
    }

    /**
     * Internal function to update the position of the buttons after the text box is re-flowed.
     */
    private void updatePositions() {
        textBox.setPosition(xPos, yPos);
        textBox.setWidth(width);

        int bottomOfTextBox = yPos+textBox.height;
        int middleOfTextBox = xPos+textBox.width/2;
        yesButton.setPosition(xPos, bottomOfTextBox+SPACE_BETWEEN_BUTTON_AND_TEXT);
        noButton.setPosition(middleOfTextBox, bottomOfTextBox+SPACE_BETWEEN_BUTTON_AND_TEXT);

        // Update the height of the whole dialog.
        height = noButton.getLowerY() - yPos;
    }

    /**
     * Checks whether the yes or no button was clicked.
     *
     * @param localX
     *        The x component of the click location.
     *
     * @param localY
     *        The y component of the click location.
     */
    @Override
    public void clicked(int localX, int localY) {
        int worldX = localX + xPos;
        int worldY = localY + yPos;

        if (yesButton.isWithinBounds(worldX, worldY)) {
            // This is the same code as GraphicsSystem::notifyGUIElementOfClick
            yesButton.clicked(worldX - yesButton.getLowerX(), worldY - yesButton.getUpperY());
        }
        if (noButton.isWithinBounds(worldX, worldY)) {
            // This is the same code as GraphicsSystem::notifyGUIElementOfClick
            noButton.clicked(worldX - noButton.getLowerX(), worldY - noButton.getUpperY());
        }
    }

    /**
     * Draws the dialog on screen.
     *
     * @param g2
     *        The graphics parameter.
     */
    @Override
    public void draw(Graphics2D g2) {
        synchronized (this) {
            textBox.draw(g2);
            yesButton.draw(g2);
            noButton.draw(g2);
            updatePositions();
        }
    }
}
