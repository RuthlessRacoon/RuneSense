package com.runesense.api;

import com.google.gson.annotations.Expose;

public class Trigger implements Comparable<Trigger> {

    public enum Type {
        //DROP_GP,
        XP_GAIN
    }
    @Expose
    private int threshold;
    public int getThreshold() { return threshold; }

    @Expose
    private Type type;
    public Type getType() { return type; }

    public Trigger(Type type, int threshold) {
        this.type = type;
        this.threshold = threshold;
    }

    public int threshold() { return threshold; }

    public String thresholdWithSuffix() {
        switch (type) {
            //case DROP_GP:
            //    return threshold + "gp";
            case XP_GAIN:
                return threshold + "xp";
        }
        return "";
    }

    public Type type() { return type; }

    public String toString() {
        switch (type) {
            case XP_GAIN:
                return "XP Gain";
            //case DROP_GP:
            //    return "Drop Value";
        }
        return "";
    }

    @Override
    public int compareTo(Trigger t) {
        if (this.type() == t.type())
            return t.threshold() - this.threshold();

        return this.type().compareTo(t.type());
    }
}
