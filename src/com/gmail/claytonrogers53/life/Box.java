package com.gmail.claytonrogers53.life;

import com.gmail.claytonrogers53.life.Graphics.Drawable;
import com.gmail.claytonrogers53.life.Graphics.Drawing;
import com.gmail.claytonrogers53.life.Physics.PhysicsObject2D;
import com.gmail.claytonrogers53.life.Physics.Vector2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * A test box to test the physics and graphics systems.
 * Created by Clayton on 15/11/2014.
 */
public class Box extends PhysicsObject2D implements Drawable {

    protected Drawing drawing = new Drawing();
    protected BufferedImage sprite;

    public Box(double mass, double momentOfInertia, Vector2D position, Vector2D velocity) {
        super(mass, momentOfInertia, position, velocity);

        sprite = new BufferedImage(20, 20,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = sprite.createGraphics();
        g.fill(new Rectangle2D.Double(0.0,0.0,20.0,20.0));
        g.draw(new Rectangle2D.Double(4.0, 4.0, 8.0, 8.0));
        drawing.sprite = sprite;
        // TODO: make sprite black edge with white centre.
    }

    @Override
    public Drawing getDrawing() {
        drawing.xPosition = position.getMagX();
        drawing.yPosition = position.getMagY();
        drawing.rotation = angle;
        drawing.spriteZoom = 1.0/20.0;
        drawing.sprite = sprite;

        angle += .1;
        return drawing;
    }

    @Override
    protected void calculatePhysics(double deltaT) {
        // don't need to do any physics
    }
}
