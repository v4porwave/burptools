package burp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfigPanel extends JPanel {
    public ConfigPanel() {
        initComponents();
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JPanel ruleInfoPanel = new JPanel(new GridBagLayout());
        ruleInfoPanel.setBorder(new EmptyBorder(10, 15, 5, 15));

        JLabel ruleLabel = new JLabel("ICP Parser: ");
        JCheckBox icpButton = new JCheckBox();
        JButton reloadButton = new JButton("Reload");
        JButton updateButton = new JButton("Update");

        ruleInfoPanel.add(ruleLabel);
        ruleInfoPanel.add(icpButton);
        ruleInfoPanel.add(reloadButton);
        ruleInfoPanel.add(updateButton);

        add(ruleInfoPanel, BorderLayout.NORTH);

    }
}
