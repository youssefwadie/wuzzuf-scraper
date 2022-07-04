package com.github.youssefwadie.wuzzufscrapper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URI;

public class RequestsManager {
    private final WebDriver driver;

    private static RequestsManager instance = null;

    private RequestsManager(String path) {
        ChromeOptions options = new ChromeOptions();
        if (path == null) {
            options.setBinary("/usr/bin/chromium");
        } else {
            options.setBinary(path);
        }
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        options.merge(capabilities);

        driver = new ChromeDriver(options);
    }


    public static RequestsManager getInstance(String path) {
        if (instance == null) {
            instance = new RequestsManager(path);
        }
        return instance;
    }

    public static RequestsManager getInstance() {
        return getInstance(null);
    }

    public String sendGET(URI uri) {
        driver.get(uri.toString());
        return driver.getPageSource();
    }

    public void close() {
        driver.close();
    }
}
