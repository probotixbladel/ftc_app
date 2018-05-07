package nl.probotix.ftc.relicrecovery.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import nl.probotix.ftc.relicrecovery.EthanHardware;

/**
 * Copyright 2018 (c) ProBotiX
 */

class AutoHelper {

    boolean autoTransmit = false;

    private EthanHardware ethanHardware;
    private LinearOpMode opMode;

    //TICKS TO DRIVE WHEN SEEING CRYPTOBOX
    int FIRST_COLUMN = 1000;
    int SECOND_COLUMN = 3000;
    int THIRD_COLUMN = 5000;

    AutoHelper(EthanHardware ethanHardware, LinearOpMode opMode) {
        this.ethanHardware = ethanHardware;
        this.opMode = opMode;
    }

    void driveAndWait(double linearX, double linearY, double angularZ, double time, double timeout) {
        driveEncoded(linearX, linearY, angularZ, time);
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();

        while (opMode.opModeIsActive() && runtime.seconds() < timeout && (ethanHardware.leftFrontWheel.isBusy() ||
                ethanHardware.rightFrontWheel.isBusy() || ethanHardware.leftRearWheel.isBusy() ||
                ethanHardware.rightRearWheel.isBusy())) {
        }
    }

    void driveEncoded(double linearX, double linearY, double angularZ, double time) {
        //Needed numbers
        double WHEEL_DIAMETER = 100;
        double WHEEL_SEPERATION_WIDTH = 336;
        double WHEEL_SEPARATION_LENGTH = 352;
        double GEAR_RATIO = 1.6;
        double COUNTS_PER_REV = 1478.4;
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

        int ticksFL = (int) (rpmFL / 60 * COUNTS_PER_REV * time);
        int ticksFR = (int) (rpmFR / 60 * COUNTS_PER_REV * time);
        int ticksRL = (int) (rpmRL / 60 * COUNTS_PER_REV * time);
        int ticksRR = (int) (rpmRR / 60 * COUNTS_PER_REV * time);

        ethanHardware.setMotorEncodeMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ethanHardware.setMotorEncodeMode(DcMotor.RunMode.RUN_TO_POSITION);

        ethanHardware.addWheelTicks(ticksFL, ticksFR, ticksRL, ticksRR);

        ethanHardware.setMotorPowers(rpmFL / WHEEL_MAX_RPM, rpmFR / WHEEL_MAX_RPM, rpmRL / WHEEL_MAX_RPM, rpmRR / WHEEL_MAX_RPM);
    }
}
