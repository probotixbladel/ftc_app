package nl.probotix.ftc.relicrecovery;

import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by robocup on 6-3-2018.
 */

public class EthanHardware {

    private HardwareMap hardwareMap;

    //motoren
    public DcMotor leftFrontWheel, rightFrontWheel, leftRearWheel, rightRearWheel, liftMotor, intakeLeft, intakeRight;

    //servo's
    public Servo blowplankServo;

    //sensoren
    public ModernRoboticsI2cRangeSensor rangeSensor;


    public EthanHardware(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        this.init();
    }

    public void init() {
        /*During init we:
        *  - Get all hardware from hardware mapping
        *  - reset all wheel powers
        *  - set proper wheel directions
        *  - set all encoding modes
        */

        //Get all hardware from hardware mapping
        leftFrontWheel = hardwareMap.dcMotor.get("leftFrontWheel");
        rightFrontWheel = hardwareMap.dcMotor.get("rightFrontWheel");
        leftRearWheel = hardwareMap.dcMotor.get("leftRearWheel");
        rightRearWheel = hardwareMap.dcMotor.get("rightRearWheel");
        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        blowplankServo = hardwareMap.servo.get("blowplankServo");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        rangeSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeSensor");

        //reset all wheel powers
        setMotorPowers(0.0, 0.0, 0.0, 0.0);
        liftMotor.setPower(0.0);

        //set all servo positions
        blowplankServo.setPosition(LiftServo.LIFT.getPosition());

        //set proper wheel directions
        rightFrontWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        rightRearWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //set all encoding modes
        setMotorEncodeMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setMotorEncodeMode(DcMotor.RunMode mode) {
        leftFrontWheel.setMode(mode);
        rightFrontWheel.setMode(mode);
        leftRearWheel.setMode(mode);
        rightRearWheel.setMode(mode);
    }

    public void setMotorPowers(double lf, double rf, double lr, double rr) {
        leftFrontWheel.setPower(lf);
        rightFrontWheel.setPower(rf);
        leftRearWheel.setPower(lr);
        rightRearWheel.setPower(rr);
    }

    public void setNewWheelTargets(int lf, int rf, int lr, int rr) {
        leftFrontWheel.setTargetPosition(lf);
        rightFrontWheel.setTargetPosition(rf);
        leftRearWheel.setTargetPosition(lr);
        rightRearWheel.setTargetPosition(rr);
    }

    public void addWheelTicks(int lf, int rf, int lr, int rr) {
        setNewWheelTargets(lf + leftFrontWheel.getCurrentPosition(), rf + rightFrontWheel.getCurrentPosition(),
                lr + leftFrontWheel.getCurrentPosition(), rr + rightRearWheel.getCurrentPosition());
    }

    public void telemetryData(Telemetry telemetry, String opModeName, String caption, String text) {
        telemetry.addData(caption, text);
        telemetry.update();
        Log.d("Ethan", opModeName + " > " + caption + " > " + text);
    }
}

