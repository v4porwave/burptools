package burp.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class SmartPanel extends JPanel {
    public SmartPanel() {

    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

        JTabbedPane mainTabbedPane = new JTabbedPane();

        JTabbedPane pane = new JTabbedPane();
        pane.addTab(" Highlighter and Extractor - Empower ethical hacker for efficient operations. ", null);
        pane.setEnabledAt(1, false);

        add(pane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

    }

}
