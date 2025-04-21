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
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Dashboard {
    private JFrame frame;
    private HashMap<String, Controllable> devices;
    private HashMap<String, JLabel> statusLabels;
    private JCheckBox sleepModeCheck;
    private JTextField sleepTimeField;

    public Dashboard() {
        devices = new HashMap<>();
        statusLabels = new HashMap<>();
        devices.put("Light", new Light());
        devices.put("Fan", new Fan());
        devices.put("Thermostat", new Thermostat());

        createUI();
    }

    private void createUI() {
        frame = new JFrame("Smart Home Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(devices.size() + 3, 1));

        // Sleep Mode Panel
        sleepModeCheck = new JCheckBox("Sleep Mode");
        sleepTimeField = new JTextField("5", 5);
        JPanel sleepPanel = new JPanel();
        sleepPanel.add(sleepModeCheck);
        sleepPanel.add(new JLabel("Time (s):"));
        sleepPanel.add(sleepTimeField);
        frame.add(sleepPanel);

        // Device Buttons
        for (String deviceName : devices.keySet()) {
            Controllable device = devices.get(deviceName);

            JPanel panel = new JPanel(new FlowLayout());

            JLabel label = new JLabel(deviceName + " is OFF");
            statusLabels.put(deviceName, label);

            JButton onButton = new JButton("ON");
            JButton offButton = new JButton("OFF");

            onButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        device.turnOn();
                        label.setText(device.getName() + " is ON");
                        Logger.log(device.getName() + " turned ON");

                        if (sleepModeCheck.isSelected()) {
                            try {
                                int seconds = Integer.parseInt(sleepTimeField.getText());
                                // Pass the label to AutoShutdownTask
                                new Thread(new AutoShutdownTask(device, seconds, label)).start();
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(frame, "Invalid sleep time!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                    } catch (DeviceAlreadyOnException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            offButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        device.turnOff();
                        label.setText(device.getName() + " is OFF");
                        Logger.log(device.getName() + " turned OFF");
                    } catch (DeviceAlreadyOffException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            panel.add(new JLabel(deviceName));
            panel.add(onButton);
            panel.add(offButton);
            panel.add(label);

            frame.add(panel);
        }

        frame.setVisible(true);
    }

    public static void createAndShowGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Dashboard();
            }
        });
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}
