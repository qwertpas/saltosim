package org.chis.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.chis.Constants;
import org.chis.Main;

public class GraphicSim extends JPanel{


    static JFrame frame;

	static int screenHeight, screenWidth;
	static int windowWidth, windowHeight;
	public static GraphicSim sim;

	static double DISP_SCALE = 300;

	static int flywheelRadius = (int)(Constants.FLYWHEEL_RADIUS*1000);
	static int legLength = (int)(Constants.RETRACTED_LENGTH*1000);


    public static void init(){
		screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		frame = new JFrame("Arm Sim");
		sim = new GraphicSim();
		frame.add(sim);
		frame.setSize(screenWidth/5, screenHeight/2);
		frame.setLocation(0, screenHeight/2);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

		windowWidth = (int) g.getClipBounds().getWidth();
		windowHeight = (int) g.getClipBounds().getHeight();

		//center the grid and flip y so it is up (default y is down)
		g2d.translate(windowWidth/2, windowHeight - 20);
		g2d.scale(1, -1);

        g.setColor(Color.BLACK);
        g.fillOval(-10, -10, 20, 20);
        g.setColor(Color.BLUE);

        g2d.rotate(Math.toRadians(Main.getTargetDeg()));
        g.drawRect(-5, -5, 100, 10);
        g2d.rotate(Math.toRadians(-Main.getTargetDeg()));



        g.setColor(Color.RED);

        g2d.rotate(Main.salto.bodyAngle + Math.PI/2.0);

		g.fillRect(-5, -5, legLength, 10);

		g2d.translate(legLength, 0);
		g2d.rotate(Main.salto.flywheelIntegrator.pos);

		g.setColor(Color.magenta);
		g.fillRect(-flywheelRadius, -5, flywheelRadius*2, 10);
		g.setColor(Color.BLACK);
        g.fillOval(-10, -10, 20, 20);
    }


}
