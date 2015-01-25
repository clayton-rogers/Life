package com.gmail.claytonrogers53.life;

import com.gmail.claytonrogers53.life.Graphics.DrawLoop;
import com.gmail.claytonrogers53.life.Graphics.Drawable;
import com.gmail.claytonrogers53.life.Graphics.Drawing;
import com.gmail.claytonrogers53.life.Physics.PhysicsObject;
import com.gmail.claytonrogers53.life.Physics.Vector2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * A test box to test the physics and graphics systems.
 * Created by Clayton on 15/11/2014.
 */
public class Box extends PhysicsObject implements Drawable {

    protected double height, width;
    protected Drawing drawing = new Drawing();

    public Box(double mass, double momentOfInertia, Vector2D position, Vector2D velocity, double height, double width) {
        super(mass, momentOfInertia, position, velocity);
        this.height = height;
        this.width = width;

        BufferedImage sprite = new BufferedImage(20, 20,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = sprite.createGraphics();
        g.fill(new Rectangle2D.Double(0.0,0.0,20.0,20.0));
        g.draw(new Rectangle2D.Double(4.0, 4.0, 8.0, 8.0));
        drawing.sprite = sprite;
    }

    @Override
    public Drawing getDrawing() {
        drawing.xPosition = this.position.getMagX();
        drawing.yPosition = this.position.getMagY();
        return drawing;
    }
}
