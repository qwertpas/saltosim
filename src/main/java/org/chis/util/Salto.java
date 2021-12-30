package org.chis.util;

import org.chis.Constants;
import org.chis.util.Motor.MotorType;

public class Salto {


    public Motor motor;
    public VerletIntegrator bodyIntegrator = new VerletIntegrator(0, 0, 0, 0);
    public VerletIntegrator flywheelIntegrator = new VerletIntegrator(0, 0, 0, 0);

    public double bodyAngle = bodyIntegrator.pos;
    
    final double M = Constants.BODY_MASS;
    final double m = Constants.FLYWHEEL_MASS / 2.0;
    final double R = Constants.RETRACTED_LENGTH;
    final double r = Constants.FLYWHEEL_RADIUS;
    final double mg = (M + 2*m) * 9.81; 
    final double flywheelMOI = 2 * m * r*r;
    double bodyMOI;

    java.util.Random rng = new java.util.Random();

    double starttime;

    public void init(){
        motor = new Motor(MotorType.CORELESS, 1);
        starttime = 0;
    }

    public void update(double dt){
        bodyMOI = M*R*R + 2 * m * (R*R + Math.pow(r * Math.cos(flywheelIntegrator.pos), 2));

        motor.update(flywheelIntegrator.vel * Constants.FLYWHEEL_GEAR_RATIO, dt);


        double appliedTorque = Util.applyFrictions(
            motor.torque * Constants.FLYWHEEL_GEAR_RATIO, 
            flywheelIntegrator.vel, 
            0.003, 
            0.0025, 
            0.0002, 
            Constants.FRIC_THRES
        );


        flywheelIntegrator.update(appliedTorque / flywheelMOI, dt);
        // System.out.println("applied:" + appliedTorque);

        double gravTorque = mg * Math.sin(bodyIntegrator.pos) * R;
        double noise = rng.nextGaussian() * 0.005;


        double netBodyTorque = Util.applyFrictions(
            -appliedTorque + gravTorque + noise,
            bodyIntegrator.vel, 
            0.001, 
            0.001, 
            Constants.VISC_FRIC, 
            Constants.FRIC_THRES
        );
        
        bodyIntegrator.update(netBodyTorque / bodyMOI, dt);

        bodyAngle = bodyIntegrator.pos;

        if(Math.abs(bodyAngle) > Math.PI/2){
            bodyIntegrator = new VerletIntegrator(0, 0, 0, 0);
            flywheelIntegrator = new VerletIntegrator(0, 0, 0, 0);
            System.out.println(System.currentTimeMillis() - starttime);
            starttime = System.currentTimeMillis();
        }
    }
}
