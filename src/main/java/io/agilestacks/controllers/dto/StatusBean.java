package io.agilestacks.controllers.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Duration;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class StatusBean {

    private String name;
    private String version;
    private Duration uptime;
    private String environmentName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Duration getUptime() {
        return uptime;
    }

    public void setUptime(Duration uptime) {
        this.uptime = uptime;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public StatusBean(String name, String version, Duration uptime, String environmentName) {
        this.name = name;
        this.version = version;
        this.uptime = uptime;
        this.environmentName = environmentName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String version;
        private Duration uptime;
        private String environmentName;

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setVersion(final String version) {
            this.version = version;
            return this;
        }

        public Builder setUptime(final Duration uptime) {
            this.uptime = uptime;
            return this;
        }

        public Builder setEnvironmentName(String environmentName) {
            this.environmentName = environmentName;
            return this;
        }

        public StatusBean build() {
            return new StatusBean(this.name, this.version, this.uptime, this.environmentName);
        }
    }
}
