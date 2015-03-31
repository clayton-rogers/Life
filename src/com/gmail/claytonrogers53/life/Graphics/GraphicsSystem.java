package com.gmail.claytonrogers53.life.Graphics;

import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.RollingAverage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The graphics system keeps a list of all of the drawable objects and draws them once per frame.
 * It also insures that a particular (adjustable) frame rate is used.
 *
 * Created by Clayton on 13/11/2014.
 */
public final class GraphicsSystem extends JFrame implements Runnable{

    /** The default refresh time used if one is not specified in the configuration file. */
    private static final int DEFAULT_DT = 17;

    /** The default window width if one is not specified in the configuration file. */
    private static final int DEFAULT_WIDTH = 900;

    /** The default window height if one is not specified in the configuration file. */
    private static final int DEFAULT_HEIGHT = 500;

    /**
     * The default window zoom level if one is not specified in the configuration file. A zoom
     * level of 1.0 means that one meter will show up as one pixel. We will typically, use zoom
     * level of higher than 1.0 since most objects will be smaller than a meter.
     */
    private static final double DEFAULT_ZOOM = 200.0;

    /** The default X centre of the screen in meters. */
    private static final double DEFAULT_PAN_X = 0.0;

    /** The default Y centre of the screen in meters. */
    private static final double DEFAULT_PAN_Y = 0.0;

    private static final double PAN_SCALE        = 1.0;
    private static final double ZOOM_SCALE       = 1.1;
    /** Then number of inputs processed in each frame. This needs to be set high enough so that
     * even at a low frame rate (ex. 10 fps), enough actions per frame will be processed to keep
     * things snappy. Don't want to set it too large, or the graphics system may lag if it gets
     * flooded with inputs.
     */
    private static final int    INPUTS_PER_LOOP  = 75;

    /** The x and y position of the frame time indicator. */
    private static final int FPS_X_POS = 10;
    private static final int FPS_Y_POS = 100;

    // Actual instance variables. For all these variables we will use "this" as the locking object.
    private int draw_dt        = DEFAULT_DT;
    private int                 width          = DEFAULT_WIDTH;
    private int                 height         = DEFAULT_HEIGHT;
    private double              zoom           = DEFAULT_ZOOM;
    private double              panX           = DEFAULT_PAN_X;
    private double              panY           = DEFAULT_PAN_Y;
    private volatile boolean    isDrawing      = false;
    private boolean             isFpsDisplayed = false;
    private final RollingAverage<Long>frameTimeAvg   = new RollingAverage<>(40);
    private final RollingAverage<Double> gLoadAvg    = new RollingAverage<>(40);
    private PhysicsSystem       physicsSystem;

    /** The list of objects that will be drawn every loop */
    private final Collection<Drawable> drawableList = new ArrayList<>(40);
    /** The list of all GUI objects on the screen (i.e. objects which do not pan and zoom). */
    private final Collection<GUIelement> GUIelementList = new ArrayList<>(40);
    /** The list of inputs to be processed. */
    private final Queue<InputMessage> inputMessages = new ConcurrentLinkedQueue<>();

    /**
     * Constructs a new runnable GraphicsSystem object. If the window settings are set in the
     * configuration file then they are read.
     */
    public GraphicsSystem() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        width   = Configuration.getValueInt("WINDOW_WIDTH",  DEFAULT_WIDTH);
        height  = Configuration.getValueInt("WINDOW_HEIGHT", DEFAULT_HEIGHT);
        draw_dt = Configuration.getValueInt("DRAW_DT",       DEFAULT_DT);

        setSize(width, height);
        setVisible(true);

        // This sleep is here in hopes of stopping the exception that sometimes happens when
        // creating the buffer strategy later on.
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            Log.error("Draw loop was interrupted while starting up.");
            Log.error(e.toString());
            stopDrawing();
            return;
        }

        createBufferStrategy(2);
    }

    /**
     * Creates and starts the graphics thread. Can only be called once.
     */
    public void start() {
        if (isDrawing) {
            Log.warning("Tried to start the graphics thread after it was already started.");
            return;
        }

        isDrawing = true;
        Thread graphicsThread = new Thread(this);
        graphicsThread.start();
    }

    /**
     * Called on the new Thread to start the drawing.
     */
    @Override
    public void run() {
        Log.info("Starting graphics loop.");

        // Create an input instance and register it with the parent listener methods.
        // This will handle the panning, zooming, and clicking on the screen.
        InputHandler inputHandler = new InputHandler(this);
        addMouseListener(inputHandler);
        addMouseMotionListener(inputHandler);
        addMouseWheelListener(inputHandler);

        // Add a listener for the ctrl-shift-f to toggle the FPS counter.
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.isControlDown() && e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F)) {
                    synchronized (this) {
                        isFpsDisplayed = !isFpsDisplayed;
                    }
                }
            }
        });

        // Resize handler.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);

                synchronized (GraphicsSystem.this) {
                    height = e.getComponent().getHeight();
                    width = e.getComponent().getWidth();
                }
            }
        });

        graphicsLoop();
        Log.info("Graphics loop exited.");
    }

    /**
     * The main graphics/drawing loop.
     */
    private void graphicsLoop () {

        long endOfLastLoopTime = System.currentTimeMillis();

        while (isDrawing) {

            processInputs();
            drawScreen();


            int localDrawDT;
            synchronized (this) {
                localDrawDT = draw_dt;
            }
            long timeToWait = (endOfLastLoopTime + localDrawDT) - System.currentTimeMillis();
            try {
                if (timeToWait > 0L) {
                    Thread.sleep(timeToWait);
                } else {
                    // If the graphics thread is overloaded and never sleeps, we still need to
                    // check if we have been interrupted so that we will stop when we have.
                    if (Thread.interrupted()) {
                        Log.warning("Graphics thread should not be interrupted to stop. Use stopDrawing().");
                        stopDrawing();
                    }
                }
            } catch (InterruptedException e) {
                Log.warning("Graphics thread should not be interrupted to stop. Use stopDrawing().");
                Log.warning(e.toString());
                stopDrawing();
            }
            endOfLastLoopTime = System.currentTimeMillis();

            // Record the frame time and load so that it can be displayed on the next frame
            synchronized (this) {
                long frameTime = draw_dt - timeToWait;
                frameTimeAvg.addToPool(frameTime);
                double gLoad = frameTime / ((double) draw_dt) * 100.0;
                gLoadAvg.addToPool(gLoad);
            }

        }
    }

    private void processInputs() {

        // Process up to "INPUTS_PER_LOOP" inputs per frame.
        InputMessage inputMessage;
        for (int i = 0; i < INPUTS_PER_LOOP; i++) {
            inputMessage = inputMessages.poll();
            if (inputMessage == null) {break;}

            switch (inputMessage.getMessageType()) {
                case SCROLL:
                    synchronized (this) {
                        // Since the pan coordinates are the world coordinates of the centre of the
                        // screen, when the x is negative, it means the whole screen has moved in
                        // the positive x direction.
                        panX -= inputMessage.getDataX() * PAN_SCALE / zoom;
                        // Same message as above for the x, but the y has an additional factor of
                        // -1 because the positive screen direction is down.
                        panY += inputMessage.getDataY() * PAN_SCALE / zoom;
                    }
                    break;
                case ZOOM:
                    synchronized (this) {
                        if (inputMessage.getDataZOOM() > 0.0) {
                            zoom *= inputMessage.getDataZOOM() * ZOOM_SCALE;
                        } else {
                            zoom /= -inputMessage.getDataZOOM() * ZOOM_SCALE;
                        }
                    }
                    break;
                case CLICK:
                    notifyGUIElementsOfClick(inputMessage.getDataX(), inputMessage.getDataY());
                    break;
            }
        }
    }

    /**
     * Draws the current frame on the back buffer and then shows it to the screen. Called once
     * every draw loop.
     */
    private void drawScreen () {

        BufferStrategy bf = getBufferStrategy();
        Graphics g = null;

        try {
            g = getBufferStrategy().getDrawGraphics();
            Graphics2D g2 = (Graphics2D) g;

            // Get the old transform so we can set it back after each object.
            AffineTransform oldAf = g2.getTransform();

            // Clear the entire screen so that the next frame can be drawn.
            synchronized (this) { // Because we are reading width and height.
                g.clearRect(0, 0, width, height);
            }

            // This synchronized section is in effect locking the drawable list, so that it is not
            // changed by another thread while we are drawing it. In the case where we spend a
            // majority of the time drawing, this could cause delays when we try to add or remove
            // an object from the draw loop.
            synchronized (drawableList) {
                Drawing drawing;
                for (Drawable object : drawableList) {
                    drawing = object.getDrawing();

                    // Because all the screen objects work in a normal coordinate system. i.e. with
                    // metres, but java2D uses pixels starting in the top left, we must convert
                    // the coordinates.
                    convertToScreenCoordinates(drawing);

                    // Set a transform to use for the drawing
                    AffineTransform af = new AffineTransform(oldAf);
                    af.translate(drawing.xPosition, drawing.yPosition);
                    af.scale(drawing.spriteZoom, drawing.spriteZoom);
                    af.rotate(drawing.rotation);

                    // This translate is to account for the fact that the drawing of the sprite is
                    // done from the top left corner, rather than the center. Since we want the
                    // rotation to be around the center, but then we must move the cursor to the
                    // corner before we start drawing.
                    af.translate(-drawing.sprite.getWidth(null)/2.0, -drawing.sprite.getHeight(null)/2.0);

                    g2.drawImage(drawing.sprite, af, null);
                }
            }

            synchronized (GUIelementList) {
                for (GUIelement e : GUIelementList) {
                    e.draw(g2);
                }
            }

            boolean localIsFpsDisplayed;
            synchronized (this) {
                localIsFpsDisplayed = isFpsDisplayed;
            }
            if (localIsFpsDisplayed) {
                g.drawString("Frame Time: " + frameTimeAvg.getAverage() + " ms", FPS_X_POS, FPS_Y_POS);
                g.drawString("G Load: " + (int)gLoadAvg.getAverage() + " %", FPS_X_POS, FPS_Y_POS + 15);
                if (physicsSystem != null) {
                    g.drawString("--------------------", FPS_X_POS, FPS_Y_POS + 30);
                    g.drawString("Frame Time: " + physicsSystem.frameTime + " ms", FPS_X_POS, FPS_Y_POS+45);
                    g.drawString("G Load: " + (int)physicsSystem.load + " %", FPS_X_POS, FPS_Y_POS + 60);
                }
                // 15 is roughly the text height
            }
        } finally {
            if (g != null) {
                g.dispose();
            }
        }

        bf.show();
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Considers the current view position and zoom and uses it to convert the drawing into
     * something that can be drawn. It needs to be synchronized because is reads the pan and zoom.
     *
     * @param inputDrawing
     *        The drawing coming from the drawable object.
     */
    private synchronized void convertToScreenCoordinates (Drawing inputDrawing) {
        synchronized (this) {
            // This transform runs on every drawable object every graphics loop, so it needs to be
            // kind of fast.

            // At a zoom level of 1.0 and zero pan vector, the centre of the screen should be
            // (0 m, 0 m) and a line of 1 m length should be 1 pixel long.
            // If the pan is something other than (0 m, 0 m) then that point will be at the centre
            // of the screen.
            // If the zoom level is something other than 1, then the size in meters of the object
            // will be multiplied by that to obtain its size in pixels.

            // Translate the points by the pan of the screen. (Since the pan is in meters, we do
            // this before the scaling.)
            inputDrawing.xPosition -= panX;
            inputDrawing.yPosition -= panY;

            // Scale the inputs to the zoom level, they are now in pixel coordinates.
            inputDrawing.xPosition *= zoom;
            inputDrawing.yPosition *= zoom;

            // Flip the y and translate the coordinates because on the screen the origin is at the
            // upper left hand corner.
            inputDrawing.xPosition += width / 2.0;
            inputDrawing.yPosition *= -1.0;
            inputDrawing.yPosition += height / 2.0;

            // We are going to let the drawing transforms actually take care of transforming the
            // sprite. However, we do need
            // translate the zoom of the sprite to the zoom of the screen.
            inputDrawing.spriteZoom *= zoom;

            // At the end of this method, the x and y pos of the drawing will be in screen pixel
            // coordinates. The spriteZoom will be that actual scaling of the sprite from sprite
            // pixels to screen pixels.
        }
    }

    /**
     * Allows other threads with a reference to the draw loop to stop it drawing and quit. This is
     * the normal way of stopping the draw thread when one is done with it.
     */
    public void stopDrawing () {
        // isDrawing is volatile therefore we do not need to obtain a lock here.
        isDrawing = false;
        Log.info("Stopping drawing.");
        dispose();
    }

    /**
     * Allows users to query whether the graphics thread has stopped. The graphics thread will be
     * in the stopped state when: before it has been started with start(), after it has ended with
     * a call to stopDrawing(), or after it has stopped due to an interrupt.
     *
     * @return Whether the graphics system is in the running state.
     */
    public boolean isGraphicsRunning() {
        return isDrawing;
    }

    /**
     * Adds an object to the list of objects to be draw every frame. The object will be draw until
     * it is explicitly removed with removeFromDrawList or clearDrawList. If the object is already
     * in the draw list, it will not be added again.
     *
     * @param object
     *        The drawable object to be added to the draw list.
     *
     * @see #removeFromDrawList
     * @see #clearDrawList
     */
    public void addToDrawList (Drawable object) {
        if (object == null) {
            Log.warning("Attempted to add a null object to the draw list.");
            return;
        }

        synchronized (drawableList) {
            boolean changed = drawableList.add(object);
            if (!changed) {
                Log.warning("Attempted to add an object to the draw list which was already there.");
            } else {
                Log.info("Added an object to the draw list.");
            }
        }
    }

    /**
     * Removes the given object from the draw list.
     *
     * @param object
     *        The object to be removed from the draw list.
     *
     * @see #clearDrawList
     */
    public void removeFromDrawList (Drawable object) {
        if (object == null) {
            Log.warning("Attempted to remove a null object from the draw list.");
            return;
        }

        synchronized (drawableList) {
            boolean didRemoveDoAnything = drawableList.remove(object);
            if (didRemoveDoAnything) {
                Log.info("Removed an object from the draw list.");
            } else {
                Log.warning("Attempted to remove an object from the draw list which was not there.");
            }

        }
    }

    /**
     * Clears every object out of the draw list. This will clear every object off of the screen.
     * This method should be used very sparingly. However, it will have better performance than
     * calling removeFromDrawList on every object.
     *
     * @see #removeFromDrawList
     */
    public void clearDrawList () {
        synchronized (drawableList) {
            Log.info("Drawing list was cleared.");
            drawableList.clear();
        }
    }

    /**
     * Sets the current graphics refresh rate. Note that the dt can be set to 0 ms to have the
     * graphics system run as fast as it can.
     *
     * @param graphicsTimeDelta
     *        The desired frame time in ms.
     *
     * @see #setFPS
     */
    public synchronized void setGraphicsTimeDelta (int graphicsTimeDelta) {
        if (graphicsTimeDelta < 0L) {
            Log.warning("Attempted to set a negative graphics delta t.");
            return;
        }

        draw_dt = graphicsTimeDelta;
        Log.info("Setting drawing delta t to: " + draw_dt + " ms.");
    }

    /**
     * Sets the current graphics refresh rate. As the graphics system only has ms resolution, the
     * max settable fps is 1000.
     *
     * @param fps
     *        The desired frames per second.
     *
     * @see #setGraphicsTimeDelta
     */
    public synchronized void setFPS (int fps) {
        if (fps <= 0) {
            Log.warning("Attempted to set an fps less than or equal to zero.");
            return;
        }

        int FPStoUse = fps > 1000 ? 1000 : fps;

        draw_dt = 1000/FPStoUse;
        Log.info("Setting drawing delta t to: " + draw_dt + " ms.");
    }

    /**
     * Sets the zoom of the current viewing area. A zoom of 1.0 will show a 1 m line as 1 pixel, a
     * zoom of 2.0 will show a 1 m line as 2 pixels.
     *
     * @param zoom
     *        The desired zoom level.
     */
    public synchronized void setZoom(double zoom) {
        if (zoom <= 0.0) {
            Log.warning("Attempted to set a zoom level less than or equal to zero.");
            return;
        }
        this.zoom = zoom;
        Log.info("Setting zoom level to: " + this.zoom);
    }

    /**
     * Sets the current centre of the screen in meters.
     *
     * @param panX
     *        The x component of the desired centre of screen.
     *
     * @param panY
     *        The y component of the desired centre of screen.
     */
    public synchronized void setPan(double panX, double panY) {
        this.panX = panX;
        this.panY = panY;
        Log.info("Setting pan to: (" + this.panX + ", " + this.panY + ") m.");
    }

    /**
     * Allows the input handler to send messages to the draw loop.
     *
     * @param message
     *        The message that the draw loop will later interpret.
     */
    void addToInputQueue (InputMessage message) {
        if (inputMessages == null) {return;}
        inputMessages.add(message);
    }

    /**
     * Adds a GUI element to the list of elements to be drawn.
     *
     * @param e
     *        The element to be added.
     */
    public void addGUIElement (GUIelement e) {
        if (e == null) {
            Log.warning("Attempted to add a null GUI element list.");
            return;
        }
        synchronized (GUIelementList) {
            GUIelementList.add(e);
        }
    }

    /**
     * Removes a GUI element from the list of elements.
     *
     * @param e
     *        The element to be removed.
     */
    public void removeGUIElement (GUIelement e) {
        if (e == null) {
            Log.warning("Attempted to add a null GUI element.");
            return;
        }
        synchronized (GUIelementList) {
            GUIelementList.remove(e);
        }
    }

    /**
     * Clears every element out of the GUI element list.
     */
    public void clearGUIElementList () {
        synchronized (GUIelementList) {
            GUIelementList.clear();
        }
    }

    /**
     * Called when a click occurs. Checks each GUI element to see whether the click was within its
     * bounds.
     *
     * @param xClickPos
     *        The x component of the click location.
     *
     * @param yClickPos
     *        The y component of the click location.
     */
    void notifyGUIElementsOfClick (int xClickPos, int yClickPos) {
        synchronized (GUIelementList) {
            for (GUIelement guiElement : GUIelementList) {
                if (guiElement.isWithinBounds(xClickPos, yClickPos)) {
                    guiElement.clicked(xClickPos-guiElement.getLowerX(), yClickPos-guiElement.getUpperY());
                }
            }
        }
    }

    /**
     * Registers a given physics system with the graphics system. Allows the graphics system to
     * query the load of the physics system.
     *
     * @param physicsSystem
     *        The physics system to register.
     */
    public void registerPhysicsSystem(PhysicsSystem physicsSystem) {
        this.physicsSystem = physicsSystem;
    }

    /**
     * The possible type of messages which can be passed from the input handler to the graphics
     * system.
     */
    private enum MessageType {
        SCROLL,
        ZOOM,
        CLICK
    }

    /**
     * Handles the panning, scrolling, and clicking input into the program. Passes the information
     * asynchronously to the graphics system.
     *
     * Created by Clayton on 9/12/2014.
     */
    private static class InputHandler implements MouseListener, MouseMotionListener, MouseWheelListener {
        /** The button which is currently pressed */
        private int button = MouseEvent.NOBUTTON;

        private int lastPanX = 0;
        private int lastPanY = 0;

        private final GraphicsSystem graphicsSystem;

        /**
         * Allows the GraphicsSystem to create an instance for itself. It provides a reference to
         * itself so that the input class can pass the scroll and zoom messages to it.
         *
         * @param graphicsSystem
         *        A reference to the creating GraphicsSystem.
         */
        InputHandler(GraphicsSystem graphicsSystem) {
            this.graphicsSystem = graphicsSystem;
        }

        /**
         * Gets called whenever a mouse button is pressed. We only care about the right click,
         * since it is used to pan.
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
                graphicsSystem.addToInputQueue(new InputMessage(
                        MessageType.SCROLL,
                        e.getX() - lastPanX,
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
            graphicsSystem.addToInputQueue(new InputMessage(
                    MessageType.ZOOM,
                    e.getPreciseWheelRotation()
            ));
        }

        /**
         * Tells the graphics loop that the mouse has been clicked so that it can notify the GUI
         * elements.
         *
         * @param e
         *        The mouse event.
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            if (button == MouseEvent.BUTTON1) {
                graphicsSystem.addToInputQueue(new InputMessage(
                        MessageType.CLICK,
                        e.getX(),
                        e.getY()
                ));
            }
        }

        /**
         * Unused.
         * @param e Unused param.
         */
        @Override
        public void mouseExited(MouseEvent e) {}

        /**
         * Unused.
         * @param e Unused param.
         */
        @Override
        public void mouseClicked(MouseEvent e) {}

        /**
         * Unused.
         * @param e Unused param.
         */
        @Override
        public void mouseEntered(MouseEvent e) {}

        /**
         * Unused.
         * @param e Unused param.
         */
        @Override
        public void mouseMoved(MouseEvent e) {}
    }

    /**
     * Carried the information of one "input". This input can be a scroll, pan, or click event.
     *
     * Created by Clayton on 11/12/2014.
     */
    private static class InputMessage {

        /** The type of the message */
        private final MessageType messageType;

        /** The value of the message. */
        private final int dataX;
        private final int dataY;
        private final double dataZOOM;


        /**
         * Creates a new message with type SCROLL or CLICK.
         *
         * @param messageType
         *        The type of the message (scroll/zoom).
         *
         * @param dataX
         *        The x component of the data.
         *
         * @param dataY
         *        The y component of the data.
         */
        InputMessage (MessageType messageType, int dataX, int dataY) {
            if (messageType != MessageType.CLICK &&
                    messageType != MessageType.SCROLL) {
                Log.error("Tried to crate a non click or scroll message from the click and scroll constructor.");
            }
            this.messageType = messageType;
            this.dataX = dataX;
            this.dataY = dataY;
            this.dataZOOM = 0.0;
        }

        /**
         * Creates a new message with type ZOOM
         *
         * @param messageType
         *        The message type. Must be ZOOM.
         *
         * @param dataZOOM
         *        The amount of the zoom.
         */
        InputMessage (MessageType messageType, double dataZOOM) {
            if (messageType != MessageType.ZOOM) {
                Log.error("Tried to create a non zoom message from the zoom message constructor.");
                throw new IllegalArgumentException();
            }
            this.messageType = MessageType.ZOOM;
            this.dataZOOM = dataZOOM;
            this.dataX = 0;
            this.dataY = 0;
        }

        /**
         * Returns the type of the message.
         *
         * @return The type of the message.
         */
        public MessageType getMessageType() {
            return messageType;
        }

        /**
         * Returns the x component of the data in the message.
         *
         * @return The x component of the data in the message.
         */
        public int getDataX() {
            if (messageType != MessageType.SCROLL &&
                    messageType != MessageType.CLICK) {
                Log.error("Tried to get the x data from a non scroll or click message.");
                throw new IllegalArgumentException();
            }
            return dataX;
        }

        /**
         * Returns the y component of the data in the message.
         *
         * @return The y component of the data in the message.
         */
        public int getDataY() {
            if (messageType != MessageType.SCROLL &&
                    messageType != MessageType.CLICK) {
                Log.error("Tried to get the y data from a non scroll or click message.");
                throw new IllegalArgumentException();
            }
            return dataY;
        }

        /**
         * Returns the zoom amount if the message is of type ZOOM.
         *
         * @return The zoom amount.
         */
        public double getDataZOOM() {
            if (messageType != MessageType.ZOOM) {
                Log.error("Tried to get the zoom value of a non zoom message.");
                throw new IllegalArgumentException();
            }
            return dataZOOM;
        }
    }
}
