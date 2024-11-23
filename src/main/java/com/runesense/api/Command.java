package com.runesense.api;

import com.google.gson.annotations.Expose;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Command implements Comparable<Command> {

    private static final int API_VERSION = 1;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Expose
    private Trigger trigger;
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
        createRequestBody();
    }
    public Trigger getTrigger() { return trigger; }

    @Expose
    private Action action;
    public void setAction(Action action) {
        this.action = action;
        createRequestBody();
    }
    public Action getAction() {
        return action;
    }

    @Expose
    private int strength;
    public void setStrength(int strength) {
        this.strength = strength;
        createRequestBody();
    }
    public int getStrength() {
        return strength;
    }

    @Expose
    private double duration;
    public void setDuration(double duration) {
        this.duration = duration;
        createRequestBody();
    }
    public double getDuration() {
        return duration;
    }

    public Command(Trigger trigger, Action action, int strength, double duration) {
        this.trigger = trigger;
        this.action = action;
        this.strength = strength;
        this.duration = duration;

        createRequestBody();
    }

    private RequestBody requestBody;
    public RequestBody getRequestBody() {
        if (requestBody == null)
            createRequestBody();
        return requestBody;
    }
    private void createRequestBody() {
        final String json = "{"
                + "\"command\": \"Function\","
                + "\"action\":" + "\"" + this.action.format(strength) + "\","
                + "\"timeSec\":" + this.duration + ","
                + "\"stopPrevious\":" + 0 + ","
                + "\"apiVer\": " + API_VERSION
                + "}";
        requestBody = RequestBody.create(json, JSON);
    }

    // compareTo is reversed
    @Override
    public int compareTo(Command c) {
        if (this.strength != c.strength)
            return c.strength - this.strength;
        return -1 * this.trigger.compareTo(c.trigger);
    }
}
