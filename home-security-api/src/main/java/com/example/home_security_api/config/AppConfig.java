package com.example.home_security_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    private String addressAndPort;
    private User user;
    private Environment environment;

    public static class Environment {
        private String separator;
        private String basePath;
        public String getSeparator() {
            return separator;
        }
        public void setSeparator(String separator) {
            this.separator = separator;
        }
        public String getBasePath() {
            return basePath;
        }
        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }

    static class User {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    public String getAddressAndPort() {
        return addressAndPort;
    }

    public void setAddressAndPort(String addressAndPort) {
        this.addressAndPort = addressAndPort;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
