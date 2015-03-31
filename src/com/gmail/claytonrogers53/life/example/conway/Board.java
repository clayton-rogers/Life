package com.gmail.claytonrogers53.life.example.conway;

import com.gmail.claytonrogers53.life.Graphics.Drawable;
import com.gmail.claytonrogers53.life.Graphics.Drawing;
import com.gmail.claytonrogers53.life.Physics.PhysicsThing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Represents the whole board of Conway cells. Handles the stepping of their "physics".
 *
 * Created by Clayton on 27/2/2015.
 */
public class Board implements PhysicsThing, Drawable{

    private final Drawing drawing = new Drawing();

    /** All of the cells of the board */
    private final Cell[] cells;
    private final int width;
    private final int height;
    /** The current generation number. */
    private int stepNumber;
    /** A random instance used to randomize the cells. */
    private static final Random RANDOM = new Random();

    /**
     * Creates a board with the given dimensions. Requires a reference to the graphics system so
     * that it can add all of the cells to the draw loop.
     *
     * @param width
     *        The number of cells in the x direction (width).
     *
     * @param height
     *        The number of cells in the y direction (height).
     */
    public Board (int width, int height) {
        this.width = width;
        this.height = height;

        Cell edge = new Cell();
        edge.setIsAlive(false);

        // Allocate the array for the cells
        cells = new Cell[width*height];

        // Allocate all the cells
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x+y*width] = new Cell();
            }
        }

        // Figure out the neighbours of all the cells
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell[] neighbours = new Cell[8];

                // If bottom edge (x and y are in world coordinates)
                if (y == 0) {
                    neighbours[0] = edge;
                    neighbours[1] = edge;
                    neighbours[2] = edge;
                }

                // If top edge
                if (y == height-1) {
                    neighbours[5] = edge;
                    neighbours[6] = edge;
                    neighbours[7] = edge;
                }

                // If left edge
                if (x == 0) {
                    neighbours[0] = edge;
                    neighbours[3] = edge;
                    neighbours[5] = edge;
                }

                // If right edge
                if (x == width-1) {
                    neighbours[2] = edge;
                    neighbours[4] = edge;
                    neighbours[7] = edge;
                }

                for (int i = 0; i < 8; i++) {
                    if (neighbours[i] == null) {
                        neighbours[i] = getNeighbour(x, y, i, width);
                    }
                }
                cells[x + y*width].setNeighbours(neighbours);
            }
        }

        initialiseDrawing();
    }

    /**
     * Creates the drawing at the center of the screen and allocates space for the graphic.
     */
    private void initialiseDrawing() {
        drawing.xPosition = 0.0;
        drawing.yPosition = 0.0;
        drawing.rotation = 0.0;
        drawing.spriteZoom = 1.0;

        drawing.sprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Used to determine which neighbours should be linked with a given cell.
     *
     * @param x
     *        The x position of the cell.
     *
     * @param y
     *        The y position of the cell.
     *
     * @param index
     *        The neighbour index (see Cell class).
     *
     * @param width
     *        The width of the current board.
     *
     * @return A reference to the cell that is the correct neighbour.
     */
    private Cell getNeighbour (int x, int y, int index, int width) {
        int xOffset = 0;
        int yOffset = 0;

        if (index == 0 || index == 1 || index == 2) {
            xOffset = index - 1;
            yOffset = -1;
        }
        if (index == 3 || index == 4) {
            xOffset = (index == 3) ? -1 : 1;
            yOffset = 0;
        }
        if (index == 5 || index == 6 || index == 7) {
            xOffset = index - 6;
            yOffset = 1;
        }

        return cells[x+xOffset + (y+yOffset)*width];
    }

    /**
     * Randomizes every cell to alive or dead.
     */
    public void randomizeCells () {
        for (Cell cell : cells) {
            cell.setIsAlive(RANDOM.nextBoolean());
        }
    }

    /**
     * Implements the physics thing. Each call will move the simulate ahead by one step.
     *
     * @param deltaT Unused.
     */
    @Override
    public void calculatePhysics(double deltaT) {
        // Figure out if each cell is alive in the next round.
        for (Cell cell : cells) {
            cell.step();
        }

        // Commit the future aliveness to current aliveness.
        for (Cell cell : cells) {
            cell.commit();
        }

        stepNumber++;
    }

    /**
     * Allows the current generation of the simulation to be queried.
     *
     * @return The current generation/step number.
     */
    public int getStepNumber() {
        return stepNumber;
    }

    @Override
    public Drawing getDrawing() {

        drawing.xPosition  = 0.0;
        drawing.yPosition  = 0.0;
        drawing.rotation   = 0.0;
        drawing.spriteZoom = 1.0;

        Graphics g = drawing.sprite.getGraphics();

        // Clear the screen.
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (cells[i + j*width].isAlive()) {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.BLACK);
                }
                g.fillRect(i,j,1,1);
            }
        }

        return drawing;
    }
}
