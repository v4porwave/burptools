package burp.ui;

import javax.swing.*;
import java.awt.*;

public class SmartPanel extends JPanel {
    public SmartPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Configuration", new ConfigPanel());
        pane.addTab("Interceptor", new InterceptorList());
        pane.setEnabledAt(0, true);

        add(pane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
    }
}
