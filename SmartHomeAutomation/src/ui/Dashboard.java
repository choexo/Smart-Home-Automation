package ui;

import devices.Fan;
import devices.Light;
import devices.Thermostat;
import exceptions.DeviceAlreadyOffException;
import exceptions.DeviceAlreadyOnException;
import interfaces.Controllable;
import utils.AutoShutdownTask;
import utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class Dashboard {
    private JFrame frame;
    private HashMap<String, Controllable> devices;
    private HashMap<String, JLabel> statusLabels;
    private HashMap<String, JButton> sleepButtons;
    private HashMap<String, Boolean> sleepStates;
    private HashMap<String, JTextField> sleepFields;

    public Dashboard() {
        devices = new HashMap<>();
        statusLabels = new HashMap<>();
        sleepButtons = new HashMap<>();
        sleepStates = new HashMap<>();
        sleepFields = new HashMap<>();

        devices.put("Light", new Light());
        devices.put("Fan", new Fan());
        devices.put("Thermostat", new Thermostat());

        createUI();
    }

    private void createUI() {
        frame = new JFrame("Smart Home Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new GridLayout(devices.size() * 2, 1)); // One row for controls, one for sleep mode

        for (String deviceName : devices.keySet()) {
            Controllable device = devices.get(deviceName);

            JPanel controlPanel = new JPanel(new FlowLayout());
            JLabel label = new JLabel(deviceName + " is OFF");
            statusLabels.put(deviceName, label);

            JButton onButton = new JButton("ON");
            JButton offButton = new JButton("OFF");

            onButton.addActionListener((ActionEvent e) -> {
                try {
                    device.turnOn();
                    label.setText(device.getName() + " is ON");
                    Logger.log(device.getName() + " turned ON");

                    // Disable sleep mode on power on
                    sleepButtons.get(deviceName).setBackground(Color.RED);
                    sleepStates.put(deviceName, false);

                } catch (DeviceAlreadyOnException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
                }
            });

            offButton.addActionListener((ActionEvent e) -> {
                try {
                    device.turnOff();
                    label.setText(device.getName() + " is OFF");
                    Logger.log(device.getName() + " turned OFF");

                    // Disable sleep mode visually too
                    sleepButtons.get(deviceName).setBackground(Color.GRAY);
                    sleepStates.put(deviceName, false);

                } catch (DeviceAlreadyOffException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
                }
            });

            controlPanel.add(new JLabel(deviceName));
            controlPanel.add(onButton);
            controlPanel.add(offButton);
            controlPanel.add(label);
            frame.add(controlPanel);

            // Sleep mode row
            JPanel sleepPanel = new JPanel(new FlowLayout());
            JButton sleepToggle = new JButton();
            sleepToggle.setPreferredSize(new Dimension(20, 20));
            sleepToggle.setBackground(Color.GRAY); // Off by default

            JTextField timeField = new JTextField("5", 5);
            sleepFields.put(deviceName, timeField);
            sleepButtons.put(deviceName, sleepToggle);
            sleepStates.put(deviceName, false);

            sleepToggle.addActionListener(e -> {
                if (!device.isOn()) {
                    sleepToggle.setBackground(Color.GRAY);
                    sleepStates.put(deviceName, false);
                    return;
                }

                boolean currentState = sleepStates.get(deviceName);
                if (currentState) {
                    sleepToggle.setBackground(Color.RED);
                    sleepStates.put(deviceName, false);
                } else {
                    sleepToggle.setBackground(Color.GREEN);
                    sleepStates.put(deviceName, true);

                    try {
                        int seconds = Integer.parseInt(sleepFields.get(deviceName).getText());
                        new Thread(new AutoShutdownTask(device, seconds, () -> {
                            SwingUtilities.invokeLater(() -> {
                                label.setText(device.getName() + " is OFF");
                                sleepToggle.setBackground(Color.GRAY);
                                sleepStates.put(deviceName, false);
                            });
                        })).start();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid sleep time!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            sleepPanel.add(new JLabel("Sleep for " + deviceName + " (s):"));
            sleepPanel.add(timeField);
            sleepPanel.add(sleepToggle);
            frame.add(sleepPanel);
        }

        frame.setVisible(true);
    }

    public static void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> new Dashboard());
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}
