package com.runesense.ui;

import com.runesense.RuneSensePlugin;
import com.runesense.api.Command;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@Singleton
public class RuneSensePanel extends PluginPanel {

    private final RuneSensePlugin plugin;

    private final JPanel eventContainer;

    public RuneSensePanel(RuneSensePlugin plugin) {
        this.plugin = plugin;

        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        JButton addEventButton = new JButton("Add Event");
        addEventButton.setLayout(new BorderLayout());
        addEventButton.setPreferredSize(new Dimension(0 , 30));
        addEventButton.addActionListener(e ->
                new EventFrame(this.getRootPane(), plugin, this)
        );
        this.add(addEventButton, BorderLayout.PAGE_START);

        eventContainer = new JPanel();
        eventContainer.setLayout(new BoxLayout(eventContainer, BoxLayout.Y_AXIS));
        eventContainer.setVisible(true);
        this.add(eventContainer, BorderLayout.CENTER);

        this.setVisible(true);
    }

    public void refreshCommandPanel() {
        eventContainer.removeAll(); // Not efficient

        for (int i = 0; i < plugin.commands.size(); i++) {
            Command command = plugin.commands.get(i);

            final JPanel commandPanel = new JPanel();
            commandPanel.setLayout(new GridLayout(3, 1));
            commandPanel.setBorder(new LineBorder(ColorScheme.BORDER_COLOR));
            commandPanel.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);

            final JLabel triggerType = new JLabel(command.getTrigger().toString());
            commandPanel.add(triggerType);

            final JPanel triggerSubPanel = new JPanel();
            triggerSubPanel.setLayout(new GridLayout(1, 2));
            triggerSubPanel.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);

            final JLabel triggerSubType = new JLabel("-Subtype (Future)-");
            triggerSubPanel.add(triggerSubType);
            final JLabel triggerThreshold = new JLabel(command.getTrigger().thresholdWithSuffix(), SwingConstants.RIGHT);
            triggerSubPanel.add(triggerThreshold);
            commandPanel.add(triggerSubPanel);

            final JLabel actionLabel = new JLabel(
                    command.getAction().str()
                            + " at "
                            + command.getStrength()
                            + " for "
                            + command.getDuration()
                            + " seconds"
            );
            commandPanel.add(actionLabel);
            final int idx = i;
            RuneSensePanel p = this;
            commandPanel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    new EventFrame(commandPanel.getRootPane(), plugin, p, idx);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    commandPanel.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
                    triggerSubPanel.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    commandPanel.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
                    triggerSubPanel.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
                }
            });

            eventContainer.add(commandPanel);
        }
    }
}
