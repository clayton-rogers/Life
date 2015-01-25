package com.gmail.claytonrogers53.life.Graphics;

import java.awt.*;

/**
 * A drawable sprite object. Encodes a screen position, the sprite, and the rotation and scale of that sprite.
 *
 * Created by Clayton on 15/11/2014.
 */
public class Drawing {

    /** The x distance in meters from the origin of the local coordinate system to the centre of the sprite. */
    public double xPosition;

    /** The y distance in meters from the origin of the local coordinate system to the centre of the sprite. */
    public double yPosition;

    /** The rotation in radians that the sprite needs to be rotated. */
    public double rotation;

    /** The sprite image to be drawn. */
    public Image sprite;

    /**
     * The zoom of the sprite. i.e. each pixel in the sprite is how many metres. This is required since the sprite is
     * always going to be some number of pixels, but the object it represents will often be smaller than a meter.
     */
    public double spriteZoom;
}
