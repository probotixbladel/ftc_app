package nl.probotix.ftc.relicrecovery.autonomous;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import nl.probotix.ftc.relicrecovery.AutoTransitioner;
import nl.probotix.ftc.relicrecovery.EthanHardware;
import nl.probotix.ftc.relicrecovery.LiftServo;
import nl.probotix.ftc.relicrecovery.LiftStage;

/**
 * Copyright 2018 (c) ProBotiX
 */

@Autonomous(name = "EthanAuto: Red One", group = "EthanAuto")
public class RedOne extends LinearOpMode {

    private EthanHardware ethanHardware;
    private AutoHelper autoHelper;
    private String opModeName = "Red One";

    @Override
    public void runOpMode() throws InterruptedException {
        //start of initialization
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        Log.d("Ethan", opModeName + " > Status > Initializing...");

        //setting up ethanHardware and AutoHelper
        ethanHardware = new EthanHardware(hardwareMap);
        autoHelper = new AutoHelper(ethanHardware, this);

        //if autoTransmit is true, set up auto transmit
        if(autoHelper.autoTransmit)
            AutoTransitioner.transitionOnStop(this, "Ethan: Drive");

        //starting up vuforia
        final String TAG = "Vuforia VuMark Sample";
        OpenGLMatrix lastLocation = null;
        VuforiaLocalizer vuforia;
        RelicRecoveryVuMark vuMark = null;
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AYXaaqn/////AAAAGRbKNVO4ekY1ks89vClKvDx44q74XkV/6/eBsXwbMXgg4taPOFA7gkZ7U4RYN+J+EileHE9jv4OprPDDLKLD6aMTDdXHHbNqnI0rKNhfnWPn0u8kAOk0VxiZrvmI19vr1ApjT6xEdj3kqvv1ea09hcIW6NMfUUdzM2l43xrppdFAWM90i5uQ3mhy2PRzbaG0AoSmRqqBhMqEnGaLnk98sEDjM0HbmiigzM49ynmpFbv0+VoCYTZY3gYBGVCs3lvj9hl31gtc0WJW62dcN+zcr2X/z4fqil0lFcGZtDN/Qj9WLGjT7dNXl9h5QaRUV+VOP+ZQHpOGEwvbnkpxqWNXfgFrTWZ+nSxQ7DJuUoxm/qbN";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        VuforiaTrackables relicTrackables = vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");

        //initialization done
        ethanHardware.telemetryData(telemetry, opModeName, "Status", "Initialized. Press start...");

        waitForStart();
        //user pressed start
        ethanHardware.telemetryData(telemetry, opModeName, "Status", "Autonomous started");

        /*
         * Traject
         * - Reading vuforia VuMark (max 3 sec, 2 sec)
         * - (hitting ball) (4sec)
         * - rotate 180 degrees left (4 sec)
         * - drive 60 centimeters to right (of balancing stone) (5 sec)
         * - drive 30centimeters to the back (2 sec)
         * - drive to the right until seeing wall (max 50 centimeter) (3 sec)
         * - drive forward until cryptobox is seen, max 40 centimeters (3 sec)
         * - drive to right column (2 sec)
         * - place block (4 sec)
         */


        //READING VUFORIA
        ethanHardware.telemetryData(telemetry, opModeName, "Vuforia", "Reading...");
        ElapsedTime runtime = new ElapsedTime();
        relicTrackables.activate();
        while(opModeIsActive() && runtime.milliseconds() <= 2000 &&(vuMark == null || vuMark.equals(RelicRecoveryVuMark.UNKNOWN))) {
            vuMark = RelicRecoveryVuMark.from(relicTemplate);
        }
        if(vuMark == null || vuMark == RelicRecoveryVuMark.UNKNOWN) {
            ethanHardware.telemetryData(telemetry, opModeName, "Vuforia", "Could not find anything");
        } else {
            ethanHardware.telemetryData(telemetry, opModeName, "Vuforia", "VuMark found");
        }
        relicTrackables.deactivate();


        //TODO HITTING BALL


        //ROTATE 180 DEGREES LEFT
        autoHelper.driveAndWait(0, 0, 180, 3, 4);


        //DRIVE 60 SECONDS TO RIGHT
        autoHelper.driveAndWait(0, -600, 0, 4, 5);


        //DRIVE 30 CENTIMETERS BACKWARDS
        autoHelper.driveAndWait(-300, 0, 0, 1.5, 2);


        //DRIVE TO THE RIGHT UNTIL SEEING WALL
        double distance = 20;
        autoHelper.driveEncoded(0, -500, 0, 2.5);
        while(opModeIsActive() && runtime.milliseconds() < 3000 && (ethanHardware.leftFrontWheel.isBusy() ||
                ethanHardware.rightFrontWheel.isBusy() || ethanHardware.leftRearWheel.isBusy() ||
                ethanHardware.rightRearWheel.isBusy())) {
            double value = ethanHardware.rangeSensor.getDistance(DistanceUnit.CM);
            ethanHardware.telemetryData(telemetry, opModeName, "RangeSensor", "Current value is " + value);
            if(value <= distance) {
                //we are close enough to the wall
                ethanHardware.setNewWheelTargets(ethanHardware.leftFrontWheel.getCurrentPosition(), ethanHardware.rightFrontWheel.getCurrentPosition(),
                        ethanHardware.leftRearWheel.getCurrentPosition(), ethanHardware.rightRearWheel.getCurrentPosition());
            }
        }


        //DRIVE MAX 40 CENTIMETER AND STOP WHEN SEEING CRYPTOBOX (3 sec)
        double rangeStart = ethanHardware.rangeSensor.getDistance(DistanceUnit.CM);
        ethanHardware.telemetryData(telemetry, opModeName, "RangeSensor", "Start value is " + rangeStart);

        autoHelper.driveEncoded(400, 0, 0, 2.5);
        while(opModeIsActive() && runtime.milliseconds() < 3000 && (ethanHardware.leftFrontWheel.isBusy() ||
                ethanHardware.rightFrontWheel.isBusy() || ethanHardware.leftRearWheel.isBusy() ||
                ethanHardware.rightRearWheel.isBusy())) {
            double value = ethanHardware.rangeSensor.getDistance(DistanceUnit.CM);
            ethanHardware.telemetryData(telemetry, opModeName, "RangeSensor", "Current value is " + value);
            if(value < rangeStart - 3) {
                //we found the cryptobox
                ethanHardware.setNewWheelTargets(ethanHardware.leftFrontWheel.getCurrentPosition(), ethanHardware.rightFrontWheel.getCurrentPosition(),
                        ethanHardware.leftRearWheel.getCurrentPosition(), ethanHardware.rightRearWheel.getCurrentPosition());
            }
        }


        //DRIVE TO RIGHT COLUMN (2 sec)
        int ticks = 0;
        if(vuMark == RelicRecoveryVuMark.LEFT) {
            autoHelper.driveAndWait(autoHelper.THIRD_COLUMN, 0, 0, 1.5, 2);
        } else if(vuMark == RelicRecoveryVuMark.CENTER) {
            autoHelper.driveAndWait(autoHelper.SECOND_COLUMN, 0, 0, 1.5, 2);
        } else {
            //if no vumark found, place block in first box
            autoHelper.driveAndWait(autoHelper.FIRST_COLUMN, 0, 0, 1.5, 2);
        }


        //PLACE BLOCK (4 sec)
        ethanHardware.liftMotor.setTargetPosition(LiftStage.ZERO.getTicks());
        runtime.reset();
        ethanHardware.liftMotor.setPower(0.8);
        while(ethanHardware.liftMotor.isBusy() && runtime.milliseconds() < 1500) {
            //waiting until lift is at right spot, 1.5 seconds max
        }
        ethanHardware.blowplankServo.setPosition(LiftServo.BLOW.getPosition());
        sleep(500);
        ethanHardware.blowplankServo.setPosition(LiftServo.LIFT.getPosition());
        sleep(500);
        ethanHardware.liftMotor.setTargetPosition(LiftStage.SECOND.getTicks());
        runtime.reset();
        while(ethanHardware.liftMotor.isBusy() && runtime.milliseconds() < 1500) {
            //waiting until lift is at right spot, 1.5 seconds max
        }


        //AUTONOMOUS DONE
        ethanHardware.telemetryData(telemetry, opModeName, "Status", "Block placed, autonomous done");
    }
}
