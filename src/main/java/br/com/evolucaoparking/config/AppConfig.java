package br.com.evolucaoparking.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ParkingProperties.class, PixProperties.class})
public class AppConfig {
}
