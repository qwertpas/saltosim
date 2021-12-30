package org.chis.util;

import org.chis.Main;

public class MotorController {
    public MotorController(int canID, MotorType motortype){

    }
    public void set(double power){
        Main.salto.motor.setPower(power);
    }
    public double getPosition(){
        return Main.salto.motor.getEncoderPosition();
    }

    public enum MotorType{
        kBrushless, kBrushed
    }
}
