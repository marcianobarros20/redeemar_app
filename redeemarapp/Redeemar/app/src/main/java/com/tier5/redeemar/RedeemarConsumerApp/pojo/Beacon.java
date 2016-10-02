package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import java.io.Serializable;

public class Beacon implements Serializable {


    private String name, uuid, color;

    private int id, redeemarId, major, minor, actionId, particularId, active;

    public String getName() {
        return name;
    }

    public int getRedeemarId() {
        return redeemarId;
    }

    public void setRedeemarId(int redeemarId) {
        this.redeemarId = redeemarId;
    }

    public int getParticularId() {
        return particularId;
    }

    public void setParticularId(int particularId) {
        this.particularId = particularId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
