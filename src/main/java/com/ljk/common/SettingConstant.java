package com.ljk.common;


import com.intellij.ide.util.PropertiesComponent;

public class SettingConstant {
    public static String TOKEN = "";

    static {
        TOKEN = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.apiToken", "");
    }
}
