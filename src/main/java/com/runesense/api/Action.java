package com.runesense.api;

public enum Action {
    VIBRATE (0, 20, "Vibrate"),
    ROTATE (0, 20, "Rotate"),
    PUMP (0, 3, "Pump"),
    THRUSTING (0, 20, "Thrusting"),
    FINGERING (0, 20, "Fingering"),
    SUCTION (0, 20, "Suction"),
    ALL (0, 20, "All");

    private final int rangeMin;
    private final int rangeMax;
    private final String str;

    Action(int rangeMin, int rangeMax, String str) {
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
        this.str = str;
    }

    public int min() { return rangeMin; }
    public int max() { return rangeMax; }
    public String str() { return str; }

    public String format(int strength) {
        return this.str
                + ":"
                + Math.min(this.rangeMax, Math.max(this.rangeMin, strength));
    }
}
