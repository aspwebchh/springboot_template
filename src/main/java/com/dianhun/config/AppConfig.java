package com.dianhun.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AppConfig {
    @Value("${ams.app_id}")
    private int appId;

    public int getAppId() {
        return appId;
    }
}
