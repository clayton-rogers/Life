package com.gmail.claytonrogers53.life.Graphics;

import java.awt.event.*;

/**
 * Handles the panning and scrolling input into the program. Passes the information asynchronously to the draw loop.
 * Other input is handled by registering directly with the DrawLoop (ie. the JFrame.).
 *
 * Created by Clayton on 9/12/2014.
 */
class Input implements MouseListener, MouseMotionListener, MouseWheelListener {
    /** The button which is currently pressed */
    private int button = MouseEvent.NOBUTTON;

    private int lastPanX = 0;
    private int lastPanY = 0;

    private final DrawLoop drawLoop;

    /**
     * Allows the DrawLoop to create an instance for itself. It provides a reference to itself so that the input class
     * can pass the scroll and zoom messages to it.
     *
     * @param drawLoop
     *        A reference to the creating DrawLoop.
     */
    Input(DrawLoop drawLoop) {
        this.drawLoop = drawLoop;
    }

    /**
     * Gets called whenever a mouse button is pressed. We only care about the right click, since it is used to pan.
     *
     * @param e
     *        The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        button = e.getButton();
        if (button == MouseEvent.BUTTON3) {
            lastPanX = e.getX();
            lastPanY = e.getY();
        }
    }

    /**
     * Handles when the right click is dragged to pan.
     *
     * @param e
     *        The mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (button == MouseEvent.BUTTON3) {
            drawLoop.addToInputQueue(new InputMessage(
                    InputMessage.MessageType.SCROLL_X,
                    e.getX() - lastPanX
            ));
            drawLoop.addToInputQueue(new InputMessage(
                    InputMessage.MessageType.SCROLL_Y,
                    e.getY() - lastPanY
            ));

            lastPanX = e.getX();
            lastPanY = e.getY();
        }
    }

    /**
     * Handles when the mouse wheel is scrolled to change the zoom level.
     *
     * @param e
     *        The mouse event.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        drawLoop.addToInputQueue(new InputMessage(
                InputMessage.MessageType.ZOOM,
                e.getPreciseWheelRotation()
        ));
    }


    /**
     * Unused.
     * @param e
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * Unused.
     * @param e
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void mouseClicked(MouseEvent e) {}

    /**
     * Unused.
     * @param e
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void mouseReleased(MouseEvent e) {}

    /**
     * Unused.
     * @param e
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void mouseEntered(MouseEvent e) {}

    /**
     * Unused.
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {}
}
