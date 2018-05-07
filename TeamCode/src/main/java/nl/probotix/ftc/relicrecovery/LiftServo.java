package nl.probotix.ftc.relicrecovery;

public enum LiftServo {

    ZERO(0.0), LIFT(0.15), BLOW(0.75);

    /**
     * Copyright ${year} (c) ProBotiX
     */
    private double position;

    LiftServo(double position) {
        this.position = position;
    }

    public double getPosition() {
        return position;
    }
}
