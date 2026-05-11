package com.app.qa.restfulbooker.config;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigLoaderTest {

    @Test
    public void shouldLoadBaseUrlFromProperties() {
        assertThat(ConfigLoader.baseUrl()).isEqualTo("https://restful-booker.herokuapp.com");
    }

    @Test
    public void shouldLoadCredentials() {
        assertThat(ConfigLoader.username()).isEqualTo("admin");
        assertThat(ConfigLoader.password()).isEqualTo("password123");
    }

    @Test
    public void shouldOverrideViaSystemProperty() {
        System.setProperty("base.url", "https://override.example.com");
        try {
            assertThat(ConfigLoader.baseUrl()).isEqualTo("https://override.example.com");
        } finally {
            System.clearProperty("base.url");
        }
    }

    @Test
    public void shouldLoadTimeouts() {
        assertThat(ConfigLoader.connectTimeoutMs()).isEqualTo(10_000);
        assertThat(ConfigLoader.readTimeoutMs()).isEqualTo(15_000);
    }
}
