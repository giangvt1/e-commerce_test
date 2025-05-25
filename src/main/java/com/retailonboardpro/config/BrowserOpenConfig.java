package com.retailonboardpro.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class BrowserOpenConfig {

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserAfterStartup() {
        System.setProperty("java.awt.headless", "false");
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:5000"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
} 