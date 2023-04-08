package com.ljk;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.ljk.common.SettingConstant;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 配置信息
 */
public class JavaHelperSettings implements SearchableConfigurable {

    private JPanel mainPanel;
    private JTextField apiTokenField;
    private JLabel apiTokenLabel;



    @Nullable
    @Override
    public JComponent createComponent() {
        // Create the label
        apiTokenLabel = new JLabel("API Token:");

        // Create the main panel
        mainPanel = new JPanel();
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);


        apiTokenField = new JTextField();
        String currentApiToken = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.apiToken", "");
        apiTokenField.setText(currentApiToken);
        SettingConstant.TOKEN = currentApiToken;
        // Add the components to the layout
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(apiTokenLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(apiTokenField)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(apiTokenLabel)
                        .addComponent(apiTokenField)
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
        String apiToken = apiTokenField.getText();
        PropertiesComponent.getInstance().setValue("com.ljk.JavaHelperSettings.apiToken", apiToken);
        SettingConstant.TOKEN = apiToken;
    }

    @Override
    public boolean isModified() {
        String currentApiToken = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.apiToken", "");
        String newApiToken = apiTokenField.getText();
        return !currentApiToken.equals(newApiToken);
    }
}
