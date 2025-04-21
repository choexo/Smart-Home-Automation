package devices;

import interfaces.Controllable;

public abstract class Device implements Controllable {
    protected String name;
    protected boolean isOn;

    public Device(String name) {
        this.name = name;
        this.isOn = false;
    }

    public boolean isOn() {
        return isOn;
    }

    public String getName() {
        return name;
    }
}
