package nl.probotix.ftc.relicrecovery;

/**
 * Copyright 2018 (c) ProBotiX
 */

public enum LiftStage {
    INTAKE(-5000), ZERO(-4000), FIRST(-2200), SECOND(0);

    private int ticks;

    LiftStage(int ticks){
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
