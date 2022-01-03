package org.chis;

import java.awt.Color;

import org.chis.util.GraphicDash;
import org.chis.util.StateSpace;
import org.chis.util.Vector2D;
import org.chis.util.Vector2D.Type;
import org.ejml.simple.SimpleMatrix;


public class BalancingModel {

    public static void main(String[] args) throws InterruptedException {
        GraphicDash graph = new GraphicDash("graph", 100, true);

        double l = .22;
        double m = (2*l)*(.006*.006)*(3.14/4)*7856;
        double M = 0.4;
        double dt = .02;
        double g = 9.8;
    
        SimpleMatrix A = new SimpleMatrix(
            new double[][]{
                {1, dt, 0, 0},
                {0,1, -(3*m*g*dt)/(7*M+4*m),0},
                {0,0,1,dt},
                {0,0,(3*g*(m+M)*dt)/(l*(7*M+4*m)),1}
            }
        );
        SimpleMatrix B = new SimpleMatrix(
            new double[][]{
                {0},
                {7*dt/(7*M+4*m)},
                {0},
                {-3*dt/(l*(7*M+4*m))}
            }
        );
        SimpleMatrix Q = new SimpleMatrix(
            new double[][]{
                {1, 0, 0, 0},
                {0, 0.0001, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 0.0001}
            }
        );
        SimpleMatrix R = new SimpleMatrix(
            new double[][]{
                {0.0005},
            }
        );

        SimpleMatrix K = StateSpace.lqr2(Q, R, A, B);

        System.out.println("K");
        System.out.println(K);


        SimpleMatrix x = new SimpleMatrix(
            new double[][]{
                {0.2},
                {0},
                {0.2},
                {0}
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



            u = K.mult(x).scale(0.1);

            x = A.mult(x).plus(B.mult(u));

            graph.putPoint("ang", new Vector2D(elaspedTime, x.get(0), Type.CARTESIAN), Color.RED);
            graph.putPoint("angvel", new Vector2D(elaspedTime, x.get(1), Type.CARTESIAN), Color.BLUE);
            graph.putPoint("x", new Vector2D(elaspedTime, x.get(2), Type.CARTESIAN), Color.GREEN);
            graph.putPoint("xvel", new Vector2D(elaspedTime, x.get(3), Type.CARTESIAN), Color.ORANGE);
        }
    }
}
