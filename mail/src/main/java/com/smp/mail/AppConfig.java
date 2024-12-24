package com.smp.mail;

import com.smp.mail.exception.ConfigFileNotFoundException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

@Configuration
public class AppConfig implements EnvironmentAware {

    @Override
    public void setEnvironment(Environment environment) {
        File configFile = new File("./config.txt");
        if (!configFile.exists()) {
            throw new ConfigFileNotFoundException("Файл конфигурации config.txt не найден в корневой директории");
        }
    }
}
