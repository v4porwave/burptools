package burp.ui;

import burp.ITab;
import burp.core.ConfigCore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AutoDirTab implements ITab {

    @Override
    public String getTabCaption() {
        return ConfigCore.instance.getTitle();
    }

    @Override
    public Component getUiComponent() {
        JSplitPane dirPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        DirTable urlTable = new DirTable();
        JScrollPane bottom = new JScrollPane(urlTable);

        JTextArea jTextArea = new JTextArea();
        jTextArea.setText("toptoptotpotptop");
        JPanel top = new JPanel();

        dirPane.add(top,"left");
        dirPane.add(bottom,"right");
        dirPane.setDividerLocation(0.5D);
        return dirPane;
    }

    private JPanel getJPanel(int index) {
        JPanel panel = new JPanel();
        panel.setAlignmentX(0.0f);
        panel.setLayout(new BoxLayout(panel, index));
        panel.setForeground(Color.BLACK);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));
        return panel;
    }

    private JScrollPane getJScrollPanel() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setAlignmentX(0.0f);
        scrollPane.setLayout(new ScrollPaneLayout());
        scrollPane.setForeground(Color.BLACK);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(5, 0, 5, 0));
        scrollPane.setVisible(true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }
}
