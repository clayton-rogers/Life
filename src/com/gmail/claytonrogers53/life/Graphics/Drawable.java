package com.gmail.claytonrogers53.life.Graphics;

/**
 * Any object which needs to be drawn on the screen needs to implement this interface.
 *
 * Created by Clayton on 15/11/2014.
 */
public interface Drawable {
    /**
     * Gets the drawing object from the given drawable object. The drawing object contains
     * information about the position, the scale and rotation and the actual sprite to be drawn.
     *
     * @return The drawing object to be drawn.
     */
    Drawing getDrawing ();
}
