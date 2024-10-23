package org.example.model;

public class AlertPreferences {

    private double highTempThreshold;

    private double lowTempThreshold;

    public AlertPreferences() {}

    public AlertPreferences(double highTempThreshold, double lowTempThreshold) {
        this.highTempThreshold = highTempThreshold;
        this.lowTempThreshold = lowTempThreshold;
    }
    public double getHighTempThreshold() {
        return highTempThreshold;
    }

    public void setHighTempThreshold(double highTempThreshold) {
        this.highTempThreshold = highTempThreshold;
    }

    public double getLowTempThreshold() {
        return lowTempThreshold;
    }

    public void setLowTempThreshold(double lowTempThreshold) {
        this.lowTempThreshold = lowTempThreshold;
    }


}
