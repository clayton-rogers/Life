package com.gmail.claytonrogers53.life;

import com.gmail.claytonrogers53.life.Graphics.Drawable;
import com.gmail.claytonrogers53.life.Graphics.Drawing;
import com.gmail.claytonrogers53.life.Physics.Collidable;
import com.gmail.claytonrogers53.life.Physics.PhysicsObject;
import com.gmail.claytonrogers53.life.Util.Vector2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * A test box to test the physics and graphics systems.
 * Created by Clayton on 15/11/2014.
 */
public class Box extends PhysicsObject implements Drawable {

    protected Drawing drawing = new Drawing();
    protected BufferedImage sprite;

    public Box(double mass, double momentOfInertia, Vector2D position, Vector2D velocity,
               double angle, double angularVelocity) {
        super(mass, momentOfInertia);

        this.state.position = position;
        this.state.velocity = velocity;
        this.state.angle = angle;
        this.state.angularVelocity = angularVelocity;

        sprite = new BufferedImage(20, 20,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = sprite.createGraphics();
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(0.0,0.0,20.0,20.0));
        g.setColor(Color.BLACK);
        g.draw(new Rectangle2D.Double(0.0, 0.0, 19.0, 19.0));
        drawing.sprite = sprite;
    }

    @Override
    public boolean isIntersecting(Vector2D vertex) {
        return false;
    }

    @Override
    public Drawing getDrawing() {
        drawing.xPosition  = state.position.getMagX();
        drawing.yPosition  = state.position.getMagY();
        drawing.rotation   = state.angle;
        drawing.spriteZoom = 1.0/15.0;
        drawing.sprite = sprite;

        return drawing;
    }

    public void setIsCollidable(boolean isCollidable) {
        this.isCollidable = isCollidable;
    }

    @Override
    public void notifyCollision(Collidable otherObject, boolean isCollisionResolved) {
        // Do nothing.
    }
}
