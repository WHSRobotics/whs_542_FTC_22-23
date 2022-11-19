package org.whitneyrobotics.ftc.teamcode.subsys.Odometry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.MissingFormatArgumentException;
import java.util.function.Supplier;

public class EncoderConverter {
    private DcMotorEx encoderMotor;
    private Double wheelDiameter, ticksPerRev;
    private DistanceUnit unit = DistanceUnit.MM;
    private boolean isREVEncoder = false;
    private EncoderConverter(DcMotorEx encoder, double wheelDiameter, double ticksPerRev, DistanceUnit unit, boolean isREVThroughBoreEncoder){
        encoderMotor = encoder;
        isREVEncoder = isREVThroughBoreEncoder;
    }

    public double getCurrentRawPosition(){
        return encoderMotor.getCurrentPosition();
    }

    public double getCurrentRawVelocity(){
        return encoderMotor.getVelocity();
    }

    public double getCurrentVelocity(AngleUnit angleUnit){
        return encoderMotor.getVelocity(angleUnit);
    }

    public static class EncoderConverterBuilder {
        private Double wheelDiameter, ticksPerRev;
        private DistanceUnit unit = DistanceUnit.MM;
        private boolean isRevEncoder = false;
        private DcMotorEx encoderMotor;

        public EncoderConverterBuilder setEncoderMotor(DcMotorEx motor){
            this.encoderMotor = motor;
            return this;
        }
        public EncoderConverterBuilder setUnit(DistanceUnit unit){
            this.unit = unit;
            return this;
        }
        public EncoderConverterBuilder setWheelDiameter(double diameter){
            this.wheelDiameter = diameter;
            return this;
        }
        public EncoderConverterBuilder setTicksPerRev(double ticksPerRev){
            this.ticksPerRev = ticksPerRev;
            return this;
        }
        public EncoderConverterBuilder setRevEncoder(boolean isRevEncoder){
            this.isRevEncoder = isRevEncoder;
            return this;
        }
        public EncoderConverter build(){
            if(encoderMotor == null){
                throw new IllegalArgumentException("Motor with paired encoder must be supplied.");
            }
            if(wheelDiameter == null || ticksPerRev == null){
                throw new IllegalArgumentException("No wheel diameter or ticks per revolution supplied");
            }
            return new EncoderConverter(encoderMotor, wheelDiameter, ticksPerRev, unit, isRevEncoder);
        }
    }
}
