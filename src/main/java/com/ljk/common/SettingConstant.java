package com.ljk.common;


import com.intellij.ide.util.PropertiesComponent;

public class SettingConstant {
    public static String TOKEN = "";
    public static Boolean ENABLE_PROXY = true;
    public static String PROXY_HOSTNAME = "";
    public static String PROXY_PORT = "7890";
    public static String PROXY_TIMEOUT = "600";

    static {
        TOKEN = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.apiToken", "");
        ENABLE_PROXY = PropertiesComponent.getInstance().getBoolean("com.ljk.JavaHelperSettings.enableProxy", true);
        PROXY_HOSTNAME = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.hostName", "");
        PROXY_PORT = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.port", "7890");
        PROXY_TIMEOUT = PropertiesComponent.getInstance().getValue("com.ljk.JavaHelperSettings.timeout", "600");

    }
}
