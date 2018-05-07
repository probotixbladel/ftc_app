package nl.probotix.ftc.relicrecovery.drive;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;


import nl.probotix.ftc.relicrecovery.EthanHardware;
import nl.probotix.ftc.relicrecovery.LiftServo;
import nl.probotix.ftc.relicrecovery.LiftStage;

/**
 * Copyright 2018 (c) ProBotiX
 */

@TeleOp( name = "Ethan: KnobDrive", group = "Ethan")
public class KnobDrive extends LinearOpMode {

    private EthanHardware ethanHardware;
    private ElapsedTime runtime = new ElapsedTime();
    private String opModeName = "KnobDrive";

    @Override
    public void runOpMode() throws InterruptedException {
        //start of initialization
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        Log.d("Ethan", opModeName + " > Status > Initializing...");

        //setting up ethanHardware and AutoHelper
        ethanHardware = new EthanHardware(hardwareMap);

        //initialization done
        ethanHardware.telemetryData(telemetry, opModeName, "Status", "Initialized. Press start...");

        start(); //is dit slim te doen?

        waitForStart();
        //user pressed start
        runtime.reset();
        ethanHardware.telemetryData(telemetry, opModeName, "Status", "Opmode started");


        //AT START BLOWPLANK NEEDS TO BE IN DRIVING POSITION
        ethanHardware.liftMotor.setTargetPosition(LiftStage.ZERO.getTicks());
        ethanHardware.liftMotor.setPower(0.8);

        /*
         * KNOB FUNCTIONS
         * GAMEPAD1:
         *   LEFTSTICK: MECANUM DRIVE
         *   RIGHTSTICK: ROTATE
         *   RIGHT_TRIGGER: SPEED
         * GAMEPAD2:
         *   A: LIFT->INTAKE
         *   B: LIFT->SECOND
         *   X: LIFT->FIRST
         *   Y: LIFT->ZERO
         *   RIGHT_BUMPER: LIFTSERVO->BLOW
         *   LEFT_BUMPER: LIFTSERVO->INTAKE
         *   LEFTSTICK: INTAKELEFT
         *   RIGHTSTICK: INTAKERIGHT
         */

        while(opModeIsActive()) {
            long startTime = System.currentTimeMillis();
            //driveStikcs(gamepad1.left_stick_y * 100, gamepad1.left_stick_x * 100, gamepad1.right_stick_x, 1);


            //DRIVE WITH STICKS GAMEPAD AND RIGHT_TRIGGER AS SPEED!
            double pwrFL = (gamepad1.left_stick_y+gamepad1.right_stick_x+gamepad1.left_stick_x) * gamepad1.right_trigger;
            double pwrFR = (gamepad1.left_stick_y-gamepad1.right_stick_x-gamepad1.left_stick_x) * gamepad1.right_trigger;
            double pwrRL = (gamepad1.left_stick_y+gamepad1.right_stick_x-gamepad1.left_stick_x) * gamepad1.right_trigger;
            double pwrRR = (gamepad1.left_stick_y-gamepad1.right_stick_x+gamepad1.left_stick_x) * gamepad1.right_trigger;
            ethanHardware.setMotorPowers(pwrFL, pwrFR, pwrRL, pwrRR);


            //INTAKE
            ethanHardware.intakeLeft.setPower(gamepad2.left_stick_y);
            ethanHardware.intakeRight.setPower(gamepad2.right_stick_y);


            //LIFT POSITIONING
            if(gamepad2.b) {
                //position to second
                ethanHardware.liftMotor.setTargetPosition(LiftStage.SECOND.getTicks());
            } else if(gamepad2.x) {
                //position to first
                ethanHardware.liftMotor.setTargetPosition(LiftStage.FIRST.getTicks());
            } else if(gamepad2.a) {
                //positionn to intake
                ethanHardware.liftMotor.setTargetPosition(LiftStage.INTAKE.getTicks());
            } else if(gamepad2.y) {
                //position to zero
                ethanHardware.liftMotor.setTargetPosition(LiftStage.ZERO.getTicks());
            }


            //LIFTSERVO POSITION
            if(gamepad2.left_bumper) {
                //set position to intake
                ethanHardware.blowplankServo.setPosition(LiftServo.ZERO.getPosition());
            } else if(gamepad2.right_bumper) {
                //set position to blow
                ethanHardware.blowplankServo.setPosition(LiftServo.BLOW.getPosition());
            } else {
                //if no button is pressed, always return to lift position
                ethanHardware.blowplankServo.setPosition(LiftServo.LIFT.getPosition());
            }


            ethanHardware.telemetryData(telemetry, opModeName, "Status", "Robot is running!\n" +
                    "Motor powers: " + ethanHardware.leftFrontWheel.getPower() + ", " + ethanHardware.rightFrontWheel.getPower() + ", "
                                + ethanHardware.leftRearWheel.getPower() + ", " + ethanHardware.rightRearWheel.getPower() + ", " + "\n" +
                    "Motor ticks: " + ethanHardware.leftFrontWheel.getCurrentPosition() + ", " + ethanHardware.rightFrontWheel.getCurrentPosition() + ", "
                                + ethanHardware.leftRearWheel.getCurrentPosition() + ", " + ethanHardware.rightRearWheel.getCurrentPosition() + ", " + "\n" +
                    "Motor targets: " + ethanHardware.leftFrontWheel.getTargetPosition() + ", " + ethanHardware.rightFrontWheel.getTargetPosition() + ", "
                                + ethanHardware.leftRearWheel.getTargetPosition() + ", " + ethanHardware.rightRearWheel.getTargetPosition() + ", " + "\n" +
                    "Lift power: " + ethanHardware.liftMotor.getPower() + "\n" +
                    "Lift ticks: " + ethanHardware.liftMotor.getCurrentPosition() + "\n" +
                    "Lift target: " + ethanHardware.liftMotor.getTargetPosition() + "\n" +
                    "Servo position: " + ethanHardware.blowplankServo.getPosition() + "\n" +
                    "Loop time: " + (System.currentTimeMillis() - startTime) + "ms\n" +
                    "Total time: " + runtime.seconds());
            //ethanHardware.telemetryData(telemetry, opModeName, "Status", "Loop is taking " + (System.currentTimeMillis() - startTime) + "ms");
        }

        ethanHardware.telemetryData(telemetry, opModeName, "Status", "Stopping robot...");

        //for security, direct stop of motor powers
        ethanHardware.setMotorPowers(0.0, 0.0, 0.0, 0.0);
        ethanHardware.liftMotor.setPower(0.0);

        //resetting lift to second position
        ethanHardware.liftMotor.setTargetPosition(LiftStage.SECOND.getTicks());
        runtime.reset();
        ethanHardware.liftMotor.setPower(0.8);
        ethanHardware.blowplankServo.setPosition(LiftServo.LIFT.getPosition());
        while(ethanHardware.liftMotor.isBusy() && runtime.milliseconds() < 4000) {
            //lift still getting in position
        }
        //lift resetted
        ethanHardware.liftMotor.setPower(0.0);

        ethanHardware.telemetryData(telemetry, opModeName, "Status", "Robot stopped.");
    }

    public void driveStikcs(double linearX, double linearY, double angularZ, double time) {

        //INFORMATION NEEDED FOR CALCULATIONS
        double WHEEL_DIAMETER = 100;
        double WHEEL_SEPERATION_WIDTH = 336;
        double WHEEL_SEPARATION_LENGTH = 352;
        double GEAR_RATIO = 1.6;
        double WHEEL_MAX_RPM = 125;

        double avwB = angularZ / 180 * Math.PI / time;

        double avwFL = (1 / (WHEEL_DIAMETER / 2)) * (linearX / time - linearY / time - (WHEEL_SEPERATION_WIDTH + WHEEL_SEPARATION_LENGTH) / 2 * avwB);
        double avwFR = (1 / (WHEEL_DIAMETER / 2)) * (linearX / time + linearY / time + (WHEEL_SEPERATION_WIDTH + WHEEL_SEPARATION_LENGTH) / 2 * avwB);
        double avwRL = (1 / (WHEEL_DIAMETER / 2)) * (linearX / time + linearY / time - (WHEEL_SEPERATION_WIDTH + WHEEL_SEPARATION_LENGTH) / 2 * avwB);
        double avwRR = (1 / (WHEEL_DIAMETER / 2)) * (linearX / time - linearY / time + (WHEEL_SEPERATION_WIDTH + WHEEL_SEPARATION_LENGTH) / 2 * avwB);

        double rpmFL = (avwFL * 30 / Math.PI) / GEAR_RATIO;
        double rpmFR = (avwFR * 30 / Math.PI) / GEAR_RATIO;
        double rpmRL = (avwRL * 30 / Math.PI) / GEAR_RATIO;
        double rpmRR = (avwRR * 30 / Math.PI) / GEAR_RATIO;

        ethanHardware.setMotorPowers(rpmFL / WHEEL_MAX_RPM, rpmFR / WHEEL_MAX_RPM, rpmRL / WHEEL_MAX_RPM, rpmRR / WHEEL_MAX_RPM);

        ethanHardware.telemetryData(telemetry, opModeName, "RPM", "" + rpmFL / WHEEL_MAX_RPM + ", " + rpmFR / WHEEL_MAX_RPM + ", " + rpmRL / WHEEL_MAX_RPM + ", " + rpmRR / WHEEL_MAX_RPM);
    }
}
