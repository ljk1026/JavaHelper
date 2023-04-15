package com.ljk;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.ljk.common.SettingConstant;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 配置信息
 */
public class JavaHelperSettings implements SearchableConfigurable {

    private JPanel mainPanel;
    private JTextField apiTokenField;
    private JLabel apiTokenLabel;
    private JCheckBox  enableProxyCheckBox ;
    private ButtonGroup proxyButtonGroup;
    private JLabel hostNameLabel;
    private JTextField hostNameField;
    private JLabel portLabel;
    private JTextField portField;
    private JLabel timeoutLabel;
    private JTextField timeoutField;


    @Nullable
    @Override
    public JComponent createComponent() {
        // Create the label for API token
        apiTokenLabel = new JLabel("API Token:");
        apiTokenField = new JTextField();
        apiTokenField.setPreferredSize(new Dimension(120, 20));
        String currentApiToken = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.apiToken", "");
        apiTokenField.setText(currentApiToken);
        SettingConstant.TOKEN = currentApiToken;

        // Create the label for proxy
        enableProxyCheckBox = new JCheckBox("启动代理",null,true);
        proxyButtonGroup = new ButtonGroup();
        proxyButtonGroup.add(enableProxyCheckBox);
        Boolean enableProxy = PropertiesComponent.getInstance().getBoolean("com.ljk.JavaHelperSettings.enableProxy", true);
        SettingConstant.ENABLE_PROXY = enableProxy;



        // Create the label for host name
        hostNameLabel = new JLabel("hostName:");
        hostNameField = new JTextField();
        hostNameField.setPreferredSize(new Dimension(80, 20));
        String currentHostName = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.hostName", "");
        hostNameField.setText(currentHostName);
        SettingConstant.PROXY_HOSTNAME = currentHostName;

        // Create the label for port
        portLabel = new JLabel("port:");
        portField = new JTextField();
        portField.setPreferredSize(new Dimension(30, 20));
        String currentPort = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.port", "7890");
        portField.setText(currentPort);
        SettingConstant.PROXY_PORT = currentPort;

        // Create the label for timeout
        timeoutLabel = new JLabel("timeOut:");
        timeoutField = new JTextField();
        timeoutField.setPreferredSize(new Dimension(30, 20));
        String currentTimeOut = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.timeout", "600");
        timeoutField.setText(currentTimeOut);
        SettingConstant.PROXY_TIMEOUT = currentTimeOut;

        // Create the main panel and layout
        mainPanel = new JPanel();
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);

        // Add the components to the layout
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(apiTokenLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(apiTokenField))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(enableProxyCheckBox))

                        .addGroup(layout.createSequentialGroup()
                                        .addComponent(hostNameLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(hostNameField)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(portLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(portField)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(timeoutLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(timeoutField))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(apiTokenLabel)
                                .addComponent(apiTokenField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(enableProxyCheckBox))

                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(hostNameLabel)
                                .addComponent(hostNameField)
                                .addComponent(portLabel)
                                .addComponent(portField)
                                .addComponent(timeoutLabel)
                                .addComponent(timeoutField))
        );


        return mainPanel;
    }

    @NotNull
    @Override
    public String getId() {
        return "com.ljk.JavaHelperSettings";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "JavaHelper Settings";
    }

    @Override
    public void apply() throws ConfigurationException {
        //保存token
        String apiToken = apiTokenField.getText();
        PropertiesComponent.getInstance().setValue("com.ljk.JavaHelperSettings.apiToken", apiToken);
        SettingConstant.TOKEN = apiToken;
        //代理信息保存
        boolean enableProxy = enableProxyCheckBox.isSelected();
        SettingConstant.ENABLE_PROXY = enableProxy;
        PropertiesComponent.getInstance().setValue("com.ljk.JavaHelperSettings.enableProxy", enableProxy);

        String hostName = hostNameField.getText();
        SettingConstant.PROXY_HOSTNAME = hostName;
        PropertiesComponent.getInstance().setValue("com.ljk.JavaHelperSettings.hostName", hostName);

        String port = portField.getText();
        SettingConstant.PROXY_PORT = port;
        PropertiesComponent.getInstance().setValue("com.ljk.JavaHelperSettings.port", port,"7890");

        String timeout = timeoutField.getText();
        SettingConstant.PROXY_TIMEOUT = timeout;
        PropertiesComponent.getInstance().setValue("com.ljk.JavaHelperSettings.timeout", timeout,"600");


    }

    @Override
    public boolean isModified() {
        String currentApiToken = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.apiToken", "");
        String newApiToken = apiTokenField.getText();
        if (!currentApiToken.equals(newApiToken)) {
            return true;
        }

        boolean currentEnableProxy = PropertiesComponent.getInstance().getBoolean("com.ljk.JavaHelperSettings.enableProxy", true);
        boolean newEnableProxy = enableProxyCheckBox.isSelected();
        if (currentEnableProxy != newEnableProxy) {
            return true;
        }

        String currentHostName = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.hostName", "");
        String newHostName = hostNameField.getText();
        if (!currentHostName.equals(newHostName)) {
            return true;
        }

        int currentPort = PropertiesComponent.getInstance().getInt("com.ljk.JavaHelperSettings.port", 7890);
        int newPort = Integer.parseInt(portField.getText());
        if (currentPort != newPort) {
            return true;
        }

        long currentTimeOut = PropertiesComponent.getInstance().getOrInitLong("com.ljk.JavaHelperSettings.timeout", 600);
        long newTimeOut = Long.parseLong(timeoutField.getText());
        if (currentTimeOut != newTimeOut) {
            return true;
        }
        return false;
    }
}
