package com.ticketsystem.payment.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("dev") // Only activate this configuration in dev profile
public class ConsulSSHTunnel {
    private static final Logger log = LoggerFactory.getLogger(ConsulSSHTunnel.class);
    private Session session;

    private final ConfigurableEnvironment environment;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${tunnel.vps-host}")
    private String vpsHost;

    @Value("${tunnel.vps-user}")
    private String vpsUser;

    @Value("${tunnel.vps-password}")
    private String vpsPassword;

    @Value("${server.port:8080}")
    private int localPort;

    // Constructor injection for ConfigurableEnvironment
    public ConsulSSHTunnel(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        // Update properties before creating tunnel
        updateConsulProperties();

        // Run tunnel creation in separate thread to avoid blocking Spring startup
        new Thread(this::createTunnel).start();
    }

    private void updateConsulProperties() {
        try {
            // Calculate the tunnel port
            int tunnelPort = localPort + 1000;

            // Create a map of properties to add/override
            Map<String, Object> dynamicProps = new HashMap<>();
            dynamicProps.put("spring.cloud.consul.discovery.port", tunnelPort);
            dynamicProps.put("spring.cloud.consul.discovery.ip-address", vpsHost);
            dynamicProps.put("spring.cloud.consul.discovery.service-name", appName + "DEV");

            // Add the properties to the environment
            MutablePropertySources propertySources = environment.getPropertySources();
            propertySources.addFirst(new MapPropertySource("dynamicConsulProps", dynamicProps));

            log.info("Updated Consul properties dynamically: port={}, service-name={}",
                    tunnelPort, appName + "DEV");

        } catch (Exception e) {
            log.error("Failed to update Consul properties: {}", e.getMessage());
        }
    }

    private void createTunnel() {
        try {
            Thread.sleep(2000); // Wait for app to start
            JSch jsch = new JSch();
            session = jsch.getSession(vpsUser, vpsHost, 22);
            session.setPassword(vpsPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10000);
            session.setPortForwardingR(localPort + 1000, "localhost", localPort);
            log.info("SSH tunnel created: {}:{} -> localhost:{}", vpsHost, localPort + 1000, localPort);
        } catch (Exception e) {
            log.error("SSH tunnel failed: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            log.info("SSH tunnel closed");
        }
    }

    @Bean
    @Profile("dev") // Only create this bean in dev profile
    ConsulRegistrationCustomizer portCustomizer() {
        return registration -> {
            registration.getService().setAddress(vpsHost);
            registration.getService().setId("mspayment" + "dev");
            registration.getService().setName("mspayment" + "dev");
            registration.getService().setPort(localPort + 1000);
            log.info("Consul registration customized: address={}, name={}, port={}",
                    vpsHost, appName + "DEV", localPort + 1000);
        };
    }
}