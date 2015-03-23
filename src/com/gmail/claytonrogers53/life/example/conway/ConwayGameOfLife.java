package com.gmail.claytonrogers53.life.example.conway;

import com.gmail.claytonrogers53.life.Graphics.GraphicsSystem;
import com.gmail.claytonrogers53.life.Physics.PhysicsSystem;
import com.gmail.claytonrogers53.life.Util.Configuration;
import com.gmail.claytonrogers53.life.Util.Log;

/**
 * Simulates Conway's Game of Life using the Life framework.
 *
 * Created by Clayton on 27/2/2015.
 */
public final class ConwayGameOfLife {
    private ConwayGameOfLife() {
    }

    public static void main (String[] args) {
        Log.init("ConwayLife.log");
        Configuration.loadConfigurationItems("Conway.conf");
        GraphicsSystem graphicsSystem = new GraphicsSystem();
        PhysicsSystem physicsSystem = new PhysicsSystem();
        graphicsSystem.registerPhysicsSystem(physicsSystem);

        graphicsSystem.setZoom(5.0);

        Board board = new Board(300, 300, graphicsSystem);
        board.randomizeCells();
        physicsSystem.addPhysicsThing(board);

        graphicsSystem.start();
        physicsSystem.start();
    }
}
