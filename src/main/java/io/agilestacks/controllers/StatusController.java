package io.agilestacks.controllers;

import io.agilestacks.controllers.dto.StatusBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RestController
public class StatusController {

    @Value("${APPLICATION_NAME:}")
    private String applicationName;
    @Value("${APPLICATION_VERSION:}")
    private String applicationVersion;
    @Value("${ENVIRONMENT_NAME:}")
    private String environmentName;

    @RequestMapping(value = {"/", "/status"})
    public StatusBean getStatus() {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

        Duration duration = Duration.of(rb.getUptime(), ChronoUnit.MILLIS);

        return StatusBean.builder()
            .setName(applicationName)
            .setVersion(applicationVersion)
            .setUptime(duration)
            .setEnvironmentName(environmentName)
            .build();
    }

}
