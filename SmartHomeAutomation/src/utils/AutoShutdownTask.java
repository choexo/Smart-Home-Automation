package utils;

import interfaces.Controllable;
import utils.Logger;
import exceptions.DeviceAlreadyOffException;

public class AutoShutdownTask implements Runnable {
    private Controllable device;
    private int delayInSeconds;
    private Runnable callback;

    public AutoShutdownTask(Controllable device, int delayInSeconds, Runnable callback) {
        this.device = device;
        this.delayInSeconds = delayInSeconds;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delayInSeconds * 1000);
            device.turnOff();
            Logger.log(device.getName() + " turned OFF (Auto shutdown)");
            if (callback != null) {
                callback.run(); // update GUI, etc.
            }
        } catch (InterruptedException | DeviceAlreadyOffException e) {
            e.printStackTrace();
        }
    }
}
