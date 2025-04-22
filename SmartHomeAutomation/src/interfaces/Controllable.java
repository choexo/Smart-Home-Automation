package interfaces;

import exceptions.DeviceAlreadyOffException;
import exceptions.DeviceAlreadyOnException;

public interface Controllable {
    void turnOn() throws DeviceAlreadyOnException;
    void turnOff() throws DeviceAlreadyOffException;
    boolean isOn();
    String getName();
}
