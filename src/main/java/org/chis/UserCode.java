package org.chis;


import java.awt.Color;

import org.chis.util.MotorController;
import org.chis.util.MotorController.MotorType;
import org.chis.util.Util;


public class UserCode {
    
    MotorController flywheelMotor;

    double targetAng, currentAng, prevAng = 0;

    double kP = -1;
    double kI = 0;
    double kD = 0.0;
    double dt = 0.010;

    double error, prevError = targetAng;
    double P, I, D = 0;


    public void robotInit(){
        flywheelMotor = new MotorController(0, MotorType.kBrushed);
    }

    public void teleopPeriodic() {
        targetAng = 0;
        currentAng = Main.getGyroPitch();


        error = targetAng - currentAng;

        // P = kP * error;

        // D = kD * -(currentAng - prevAng) / dt;

        // if(Math.abs(error) < 5){
        //     I += kI * error * dt;
        // }
        // if(Math.signum(error) != Math.signum(prevError)){
        //     I = 0;
        // }


        // double power = P + I + D;

        double power = 
            Main.salto.bodyIntegrator.pos * 0.5 + 
            Main.salto.bodyIntegrator.vel * 0.5 + 
            Main.salto.flywheelIntegrator.vel * 0.03 + 
            Main.salto.flywheelIntegrator.pos * -0.08
        ;

        power += Math.copySign(0.6, Main.salto.bodyIntegrator.pos);


        power = Util.limit(power, 1);

        flywheelMotor.set(power);
        Main.graph.putNumber("power", power, Color.BLACK);

        // if(System.currentTimeMillis() % 3000 < 1500){
        //     flywheelMotor.set(1);

        // }else{
        //     flywheelMotor.set(-1);
        // }

        // System.out.println("error:" + error);

        prevAng = currentAng;
        prevError = error;
    }

    public double convertToDegrees(double ticks) {
        double ticksPerRev = 42.0;
        double degrees = ticks / ticksPerRev * 360.0;
        return degrees;
    }

    
}
