package org.chis;


import org.chis.util.MotorController;
import org.chis.util.MotorController.MotorType;
import org.chis.util.Util;
import org.ejml.simple.SimpleMatrix;


public class UserCode {
    
    MotorController flywheelMotor;

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


    public void robotInit(){
        flywheelMotor = new MotorController(0, MotorType.kBrushed);
    }

    public void teleopPeriodic() {
        double bodyAng = Main.salto.bodyIntegrator.pos;
        double bodyVel = Main.salto.bodyIntegrator.vel;
        double flyVel = Main.salto.flywheelIntegrator.vel;

        double power = 995 * bodyAng + 118*bodyVel + 0.066 * flyVel;



        // double power = 
        //     Main.salto.bodyIntegrator.pos * 0.5 + 
        //     Main.salto.bodyIntegrator.vel * 0.5 + 
        //     Main.salto.flywheelIntegrator.vel * 0.03 + 
        //     Main.salto.flywheelIntegrator.pos * -0.08
        // ;

        power += Math.copySign(0.3, Main.salto.bodyIntegrator.pos);


        power = Util.limit(power, 1);

        flywheelMotor.set(power);
        // Main.graph.putNumber("power", power, Color.BLACK);
    }


    
}
