package com.gmail.claytonrogers53.life.example.conway;

import com.gmail.claytonrogers53.life.Graphics.Drawable;
import com.gmail.claytonrogers53.life.Graphics.Drawing;
import com.gmail.claytonrogers53.life.Util.Log;
import com.gmail.claytonrogers53.life.Util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a single cell in Conway's game of life.
 *
 * Created by Clayton on 27/2/2015.
 */
public class Cell implements Drawable {

    /** A black pixel to represent a dead cell. */
    private static final BufferedImage DEAD_SPRITE;
    /** A blue pixel to represent an alice cell. */
    private static final BufferedImage ALIVE_SPRITE;

    static {
        ALIVE_SPRITE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        DEAD_SPRITE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = ALIVE_SPRITE.createGraphics();
        g2.setColor(Color.BLUE);
        g2.fillRect(0,0,1,1);

        g2 = DEAD_SPRITE.createGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,1,1);
    }

    private boolean isAlive;
    private boolean nextIsAlive;
    private final Drawing drawing = new Drawing();
    private final Vector2D pos;

    /** The neighbours of this cell.
     *   0 1 2
     *   3   4
     *   5 6 7
     */
    private final Cell[] neighbours = new Cell[8];

    /**
     * Allows the board to set the neighbours of this cell once all the cells have been allocated.
     *   0 1 2
     *   3   4
     *   5 6 7
     *
     * @param neighbours
     *        The array of neighbours.
     */
    void setNeighbours (Cell[] neighbours) {
        if (neighbours.length != 8) {
            String errorText = "Tried to create a cell with not 8 neighbours.";
            Log.error(errorText);
            throw new IllegalArgumentException(errorText);
        }
        System.arraycopy(neighbours, 0, this.neighbours, 0, 8);
    }

    /**
     * Creates a cell at the given position (world coordinates).
     *
     * @param position
     *        The position to create the cell.
     */
    public Cell (Vector2D position) {
        pos = new Vector2D(position);
    }

    /**
     * Whether this cell is currently alive or dead.
     *
     * @return True when the cell is alive.
     */
    boolean isAlive() {
        return isAlive;
    }

    /**
     * Allows the user to set the state of the cell.
     *
     * @param isAlive
     *        The state of the cell to be set.
     */
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    /**
     * Calculates whether the cell should be alive or dead next step using the number of neighbours.
     */
    public void step() {
        int numNeighbours = 0;
        for (Cell cell : neighbours) {
            if (cell.isAlive()) {
                numNeighbours++;
            }
        }
        nextIsAlive = isAliveNext(numNeighbours);
    }

    /**
     * Uses the number of neighbours, the rules of life, and whether the cell is alive or dead to determine whether the
     * cell should be alive in the next time step.
     *
     * @param numberNeighbours
     *        The number of neighbours which are currently alive.
     *
     * @return Whether the cell will be alive in the next time step.
     */
    private boolean isAliveNext(int numberNeighbours) {
        if (isAlive) {
            return numberNeighbours == 2 || numberNeighbours == 3;
        } else {
            return numberNeighbours == 3;
        }
    }

    /**
     * Commits the calculated next step to the current state.
     */
    public void commit() {
        isAlive = nextIsAlive;
    }

    /**
     * Implements the drawable interface so that the cell can be draw.
     *
     * @return A drawing of the cell.
     */
    @Override
    public Drawing getDrawing() {
        drawing.xPosition  = pos.getMagX();
        drawing.yPosition  = pos.getMagY();
        drawing.rotation   = 0.0;
        drawing.spriteZoom = 1.0;

        if (isAlive) {
            drawing.sprite = ALIVE_SPRITE;
        } else {
            drawing.sprite = DEAD_SPRITE;
        }

        return drawing;
    }
}
