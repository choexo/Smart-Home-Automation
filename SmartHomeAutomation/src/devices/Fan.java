package devices;

import exceptions.DeviceAlreadyOffException;
import exceptions.DeviceAlreadyOnException;
import interfaces.Controllable;

public class Fan implements Controllable {
    private boolean isOn;

    public Fan() {
        this.isOn = false;
    }

    @Override
    public void turnOn() throws DeviceAlreadyOnException {
        if (isOn) {
            throw new DeviceAlreadyOnException("Fan is already ON.");
        }
        isOn = true;
    }

    @Override
    public void turnOff() throws DeviceAlreadyOffException {
        if (!isOn) {
            throw new DeviceAlreadyOffException("Fan is already OFF.");
        }
        isOn = false;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    @Override
    public String getName() {
        return "Fan";
    }
}
