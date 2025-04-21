package utils;

import interfaces.Controllable;
import utils.Logger;
import exceptions.DeviceAlreadyOffException;

import javax.swing.*;

public class AutoShutdownTask implements Runnable {
    private Controllable device;
    private int delayInSeconds;
    private JLabel statusLabel;  // Added JLabel to update GUI

    public AutoShutdownTask(Controllable device, int delayInSeconds, JLabel statusLabel) {
        this.device = device;
        this.delayInSeconds = delayInSeconds;
        this.statusLabel = statusLabel;  // Initialize JLabel
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delayInSeconds * 1000);  // Wait for the desired time
            try {
                device.turnOff();
                Logger.log(device.getName() + " turned OFF (Auto shutdown)");

                // Update GUI label safely from Swing thread
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(device.getName() + " is OFF");
                });

            } catch (DeviceAlreadyOffException e) {
                Logger.log("Attempted auto shutdown but " + device.getName() + " was already OFF");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
