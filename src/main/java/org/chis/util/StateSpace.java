package org.chis.util;

import org.ejml.simple.SimpleMatrix;

public class StateSpace {

    //comes from: https://automaticaddison.com/linear-quadratic-regulator-lqr-with-python-code-example/
    public static SimpleMatrix lqr(SimpleMatrix Q, SimpleMatrix R, SimpleMatrix A, SimpleMatrix B) {
        
        int N = 50;
        SimpleMatrix[] P = new SimpleMatrix[N + 1];
        P[N] = Q;

        for(int i = N; i >= 1; i--){
            P[i-1] = 
                Q
                .plus(A.transpose().mult(P[i]).mult(A))
                .minus(
                    A.transpose().mult(P[i]).mult(B)
                    .mult(R.plus(B.transpose().mult(P[i]).mult(B)).pseudoInverse())
                    .mult(B.transpose().mult(P[i]).mult(A)))
            ;
        }

        // System.out.println("P[N]: ");
        // System.out.println(P[N]);
        // System.out.println("P[0]: ");
        // System.out.println(P[0]);

        // SimpleMatrix KN = R.plus(B.transpose().mult(P[N]).mult(B)).pseudoInverse().mult(B.transpose()).mult(P[N]).mult(A).negative();
        SimpleMatrix K0 = R.plus(B.transpose().mult(P[0]).mult(B)).pseudoInverse().mult(B.transpose()).mult(P[0]).mult(A).negative();

        // System.out.println("KN: ");
        // System.out.println(KN);

        return K0;
    }

    //comes from: https://scicomp.stackexchange.com/questions/30757/discrete-time-algebraic-riccati-equation-dare-solver-in-c
    public static SimpleMatrix lqr2(SimpleMatrix Q, SimpleMatrix R, SimpleMatrix A, SimpleMatrix B) {
        int maxIterations = 50;
        double epsilon = 0.001;
        
        SimpleMatrix Ak = A;
        SimpleMatrix Gk = B.mult(R.pseudoInverse()).mult(B.transpose());
        SimpleMatrix Hk = Q;
        SimpleMatrix I = SimpleMatrix.identity(Gk.mult(Hk).numRows());
        SimpleMatrix Akplus1, Gkplus1, Hkplus1;

        for(int i = 0; i < maxIterations; i++){
            SimpleMatrix IplusGkHkInverse = I.plus(Gk.mult(Hk)).pseudoInverse();
            Akplus1 = Ak.mult(IplusGkHkInverse).mult(Ak);
            Gkplus1 = Gk.plus(Ak.mult(IplusGkHkInverse).mult(Gk).mult(Ak.transpose()));
            Hkplus1 = Hk.plus(Ak.transpose().mult(Hk).mult(IplusGkHkInverse).mult(Ak));

            if(Hkplus1.minus(Hk).normF() / Hkplus1.normF() < epsilon){
                SimpleMatrix K = R.plus(B.transpose().mult(Hkplus1).mult(B)).pseudoInverse().mult(B.transpose()).mult(Hkplus1).mult(A).negative();
                return K;
            }

            Ak = Akplus1;
            Gk = Gkplus1;
            Hk = Hkplus1;
        }
        System.out.println("did not converge in " + maxIterations + " iterations");
        return null;
    }


    public static void main(String[] args) {

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

        System.out.println("A: ");
        System.out.println(A);
        System.out.println("B: ");
        System.out.println(B);

        long starttime0 = System.nanoTime();
        SimpleMatrix K0 = lqr(Q, R, A, B);
        long starttime2 = System.nanoTime();
        SimpleMatrix K2 = lqr2(Q, R, A, B);
        long endtime2 = System.nanoTime();

        System.out.println("K0: ");
        System.out.println(K0);
        System.out.println("K2: ");
        System.out.println(K2);

        System.out.println("K0 took " + (starttime2-starttime0));
        System.out.println("K2 took " + (endtime2-starttime2));

        
        double diff = (K0.normF() - K2.normF()) / K2.normF();
        System.out.println(diff);

        long sum = 0;
        for(int i = 0; i < 100; i++){
            long starttime = System.nanoTime();
            lqr2(Q, R, A, B);
            long endtime = System.nanoTime();
            sum += (endtime - starttime);
        }
        System.out.println("avg: " + (sum/100.));
    }
}
