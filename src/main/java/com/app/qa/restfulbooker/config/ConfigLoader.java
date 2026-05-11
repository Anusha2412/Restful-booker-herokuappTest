package com.app.qa.restfulbooker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {
    private static final Properties PROPS = load();

    private ConfigLoader() {}

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
        return p;
    }

    public static String baseUrl()       { return require("base.url"); }
    public static String username()      { return require("auth.username"); }
    public static String password()      { return require("auth.password"); }
    public static int connectTimeoutMs() { return Integer.parseInt(require("http.connect.timeout.ms")); }
    public static int readTimeoutMs()    { return Integer.parseInt(require("http.read.timeout.ms")); }

    private static String require(String key) {
        String sysVal = System.getProperty(key);
        if (sysVal != null && !sysVal.isBlank()) return sysVal;
        String fileVal = PROPS.getProperty(key);
        if (fileVal == null || fileVal.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return fileVal;
    }
}
