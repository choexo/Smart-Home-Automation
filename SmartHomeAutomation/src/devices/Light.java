package devices;

import exceptions.DeviceAlreadyOffException;
import exceptions.DeviceAlreadyOnException;
import interfaces.Controllable;

public class Light implements Controllable {
    private boolean isOn;

    public Light() {
        this.isOn = false;
    }

    @Override
    public void turnOn() throws DeviceAlreadyOnException {
        if (isOn) {
            throw new DeviceAlreadyOnException("Light is already ON.");
        }
        isOn = true;
    }

    @Override
    public void turnOff() throws DeviceAlreadyOffException {
        if (!isOn) {
            throw new DeviceAlreadyOffException("Light is already OFF.");
        }
        isOn = false;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    @Override
    public String getName() {
        return "Light";
    }
}
