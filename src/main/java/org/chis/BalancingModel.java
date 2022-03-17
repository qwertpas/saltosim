package org.chis;

import java.awt.Color;

import org.chis.util.GraphicDash;
import org.chis.util.StateSpace;
import org.chis.util.Util;
import org.chis.util.Vector2D;
import org.chis.util.Vector2D.Type;
import org.ejml.simple.SimpleMatrix;


public class BalancingModel {

    public static void main(String[] args) throws InterruptedException {
        GraphicDash graph = new GraphicDash("graph", 100, true);

        double dt = .01;
        double M = Constants.BODY_MASS;
        double m = Constants.FLYWHEEL_MASS;
        double g = 9.8;
        double R = Constants.RETRACTED_LENGTH;
        double r = Constants.FLYWHEEL_RADIUS;
        double Ib = (M + 2*m) * R * R;
        double If = 2 * m * r * r;
        double Tstall = 0.0004;
        double wfree = 1885.;
    
        SimpleMatrix A = new SimpleMatrix(
            new double[][]{
                {1, dt, 0},
                {(M + 2*m) * g * R * dt / Ib, 1, Tstall * dt / (Ib * wfree)},
                {0, 0, 1 - Tstall * dt / (wfree * If)},
            }
        );
        SimpleMatrix B = new SimpleMatrix(
            new double[][]{
                {0},
                {-Tstall * dt / Ib},
                {Tstall * dt / If}
            }
        );
        SimpleMatrix Q = new SimpleMatrix(
            new double[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1},
            }
        );
        SimpleMatrix R_cost = new SimpleMatrix(
            new double[][]{
                {200},
            }
        );

        SimpleMatrix K = StateSpace.lqr2(Q, R_cost, A, B);

        System.out.println("K");
        System.out.println(K);


        SimpleMatrix x = new SimpleMatrix(
            new double[][]{
                {0},
                {0.01},
                {0},
            }
        );

        SimpleMatrix u = new SimpleMatrix(
            new double[][]{
                {0.}
            }
        );


        double startTime = System.currentTimeMillis() / 1000.;
        while(true){
            double elaspedTime = (System.currentTimeMillis() / 1000.) - startTime;
            GraphicDash.paintAll();
            Thread.sleep(100);



            u = K.mult(x);


            double power = u.get(0);
            power = Util.limit(power, 1);


            x = A.mult(x).plus(B.scale(power));

            System.out.println(power);

            // graph.putPoint("u", new Vector2D(elaspedTime, u.get(0), Type.CARTESIAN), Color.BLACK);
            graph.putPoint("bodyAng", new Vector2D(elaspedTime, x.get(0), Type.CARTESIAN), Color.RED);
            graph.putPoint("bodyVel", new Vector2D(elaspedTime, x.get(1), Type.CARTESIAN), Color.BLUE);
            graph.putPoint("flyVel", new Vector2D(elaspedTime, x.get(2), Type.CARTESIAN), Color.GREEN);
        }
    }
}
