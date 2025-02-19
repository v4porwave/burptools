package burp.ui;

import burp.config.Settings;
import javafx.scene.layout.Border;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class ConfigPanel extends JPanel {

    public ConfigPanel() {
        reloadButton = new JButton();
        changeICPStatus();
        initComponents();
    }

    private JButton reloadButton ;

    private void changeICPStatus() {
        reloadButton.setText(Settings.ICP_BUTTON ? "开启" : "关闭");
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0};
        gridBagLayout.rowHeights = new int[]{0, 0};
        gridBagLayout.columnWeights = new double[]{0.3};  //设置了列的宽度为容器宽度
        gridBagLayout.rowWeights = new double[]{0.2, 0.8};  //第一行的高度占了容器的2份，第二行的高度占了容器的8份
        setLayout(gridBagLayout);

        buildTopPanel();
        buildBottomPanel();
    }

    private void buildTopPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.3, 0.7};  //设置了列的宽度为容器宽度
        gridBagLayout.rowWeights = new double[]{0.4, 0.2, 0.4};  //第一行的高度占了容器的2份，第二行的高度占了容器的8份
        JPanel topPanel = new JPanel(gridBagLayout);
        topPanel.setBackground(Color.cyan);

        JLabel ruleLabel = new JLabel("ICP/IP解析器");
        ruleLabel.setBounds(100, 100, 20, 200);
        topPanel.add(ruleLabel);
        reloadButton.addActionListener((e) -> {
            Settings.ICP_BUTTON = !Settings.ICP_BUTTON;
            changeICPStatus();
        });
        topPanel.add(reloadButton);

        GridBagConstraints top_panel = new GridBagConstraints();
        top_panel.insets = new Insets(0, 0, 5, 0);
        top_panel.fill = GridBagConstraints.BOTH;
        top_panel.gridx = 0;
        top_panel.gridy = 0;
        add(topPanel, top_panel);
    }

    private void buildBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.ORANGE);
        GridBagConstraints bottom_panel = new GridBagConstraints();
        bottom_panel.fill = GridBagConstraints.BOTH;
        bottom_panel.gridx = 0;
        bottom_panel.gridy = 1;
        add(bottomPanel, bottom_panel);
    }
}
