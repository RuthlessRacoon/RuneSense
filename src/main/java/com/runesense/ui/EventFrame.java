package com.runesense.ui;

import com.runesense.RuneSensePlugin;
import com.runesense.api.Action;
import com.runesense.api.Command;
import com.runesense.api.Trigger;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class EventFrame extends JFrame {

    private JComboBox<Trigger.Type> triggerCombo;
    private JSpinner triggerValue;
    private JComboBox<Action> actionCombo;

    private JPanel intensityPanel;
    private JLabel intensityLabel;
    private SpinnerNumberModel intensityModel;
    private JSpinner intensityValue;

    EventFrame(JRootPane rootPane, RuneSensePlugin plugin, RuneSensePanel p) {
        this(rootPane, plugin, p, -1);
    }

    EventFrame(JRootPane rootPane, RuneSensePlugin plugin, RuneSensePanel p, final int i) {
        super("Configure Event");

        Command command = i == -1 ? new Command(
                new Trigger(Trigger.Type.XP_GAIN, 100),
                Action.ALL,
                1,
                1.0
        ) : plugin.commands.get(i);

        if (isAlwaysOnTopSupported())
            setAlwaysOnTop(plugin.getRuneLiteConfig().gameAlwaysOnTop());

        setIconImage(plugin.getIcon());

        Dimension size = new Dimension(300, 300);
        setSize(size);
        setMinimumSize(size);

        setLocation(rootPane.getLocationOnScreen());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(getSize());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        mainPanel.setVisible(true);
        add(mainPanel);

        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
        eventPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        eventPanel.setVisible(true);
        mainPanel.add(eventPanel);

        JLabel eventLabel = new JLabel("Event Trigger");
        eventPanel.add(eventLabel);

        triggerCombo = new JComboBox<>(Trigger.Type.values());
        triggerCombo.setSelectedItem(command.getTrigger().type());
        eventPanel.add(triggerCombo);

        JLabel placeholder = new JLabel("Sub-event Placeholder");
        eventPanel.add(placeholder);

        SpinnerNumberModel triggerModel = new SpinnerNumberModel(100, 0, Integer.MAX_VALUE, 5);
        triggerValue = new JSpinner(triggerModel);
        triggerValue.setValue(command.getTrigger().getThreshold());
        eventPanel.add(triggerValue);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        actionPanel.setVisible(true);
        mainPanel.add(actionPanel);

        JLabel actionLabel = new JLabel("Action");
        actionPanel.add(actionLabel);

        actionCombo = new JComboBox<>(Action.values());
        actionCombo.setSelectedItem(command.getAction());
        actionCombo.addActionListener(e -> {
            Action selected = (Action) actionCombo.getSelectedItem();
            assert selected != null;
            int prevVal = Math.min((int) intensityValue.getValue(), selected.max());
            intensityValue.setValue(prevVal);

            intensityModel = new SpinnerNumberModel(
                    prevVal,
                    selected.min(),
                    selected.max(),
                    1
            );
            //intensityValue = new JSpinner(intensityModel);
            intensityValue.setModel(intensityModel);

            //intensityPanel.add(intensityValue);
        });
        actionPanel.add(actionCombo);

        intensityPanel = new JPanel();
        intensityPanel.setLayout(new BoxLayout(intensityPanel, BoxLayout.X_AXIS));
        intensityPanel.setVisible(true);
        actionPanel.add(intensityPanel);

        intensityLabel = new JLabel("Intensity: ");
        intensityPanel.add(intensityLabel);

        intensityModel = new SpinnerNumberModel(
                command.getStrength(),
                command.getAction().min(),
                command.getAction().max(),
                1
        );
        intensityValue = new JSpinner(intensityModel);
        intensityLabel.setLabelFor(intensityValue);
        intensityPanel.add(intensityValue);

        JPanel durationPanel = new JPanel();
        durationPanel.setLayout(new BoxLayout(durationPanel, BoxLayout.X_AXIS));
        durationPanel.setVisible(true);
        actionPanel.add(durationPanel);

        JLabel durationLabel = new JLabel("Duration (seconds): ");
        durationPanel.add(durationLabel);

        SpinnerNumberModel durationModel = new SpinnerNumberModel(
                command.getDuration(),
                1,
                600,
                .1
        );
        JSpinner durationValue = new JSpinner(durationModel);
        durationLabel.setLabelFor(durationValue);
        durationPanel.add(durationValue);

        JPanel buttonPannel = new JPanel();
        buttonPannel.setLayout(new BoxLayout(buttonPannel, BoxLayout.X_AXIS));
        buttonPannel.setVisible(true);
        mainPanel.add(buttonPannel);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            if (i != -1)
                plugin.commands.remove(i);
            p.refreshCommandPanel();
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        buttonPannel.add(deleteButton);

        JButton saveButton = new JButton("Save & Close");
        saveButton.addActionListener(e -> {
            command.setStrength((int) intensityValue.getValue());
            command.setTrigger(new Trigger(
                    (Trigger.Type) triggerCombo.getSelectedItem(),
                    (int) triggerValue.getValue()
            ));
            command.setDuration((double) durationValue.getValue());
            command.setAction((Action) actionCombo.getSelectedItem());

            if (i == -1)
                plugin.commands.add(command);

            p.refreshCommandPanel();
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        buttonPannel.add(saveButton);

        setVisible(true);
    }
}
