package com.gmail.claytonrogers53.life.Experiment;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;

/**
 * Main class which starts the experiment.
 * Created by Clayton on 13/11/2014.
 */
public class ExpMain extends JFrame {

    double x = 20.0;

    public static void main (String arg[]) {
        new ExpMain();
    }

    public ExpMain () {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //this.setUndecorated(true);
        this.setSize(900,500);
        this.setVisible(true);

        this.createBufferStrategy(2);

        gameLoop();
    }

    private void gameLoop () {
        for (int i = 0; i < 200; i++) {
            drawScreen();
            System.out.println("Done drawing");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.exit(13);
            }
            x += 2.0;
        }
    }

    private void drawScreen() {
        BufferStrategy bf = this.getBufferStrategy();
        Graphics g = null;
        Graphics2D g2;

        try {
            g = bf.getDrawGraphics();
            g2 = (Graphics2D) g;

            g2.draw(new Rectangle2D.Double(x, 40.0, 40.0, 40.0));

        } finally {
            if (g != null) {
                g.dispose();
            }
        }

        bf.show();
        Toolkit.getDefaultToolkit().sync();
    }
}
