package nl.probotix.ftc.relicrecovery;

/**
 * Copyright 2018 (c) ProBotiX
 */

public enum LiftServo {

    ZERO(0.0), LIFT(0.15), BLOW(0.75);

    private double position;

    LiftServo(double position) {
        this.position = position;
    }

    public double getPosition() {
        return position;
    }
}
