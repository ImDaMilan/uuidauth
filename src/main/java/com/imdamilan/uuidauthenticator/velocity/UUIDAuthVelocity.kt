package com.imdamilan.uuidauthenticator.velocity;

import com.google.inject.Inject;
import com.imdamilan.uuidauthenticator.velocity.config.ConfigReader;
import com.imdamilan.uuidauthenticator.velocity.listeners.PlayerJoinListener;
import com.imdamilan.uuidauthenticator.velocity.sql.SQL;
import com.imdamilan.velocityupdater.VelocityUpdater;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.SQLException;

@Plugin(
        id = "uuidauth",
        name = "UUIDAuth",
        version = "0.6",
        authors = {"ImDaMilan"}
)

public class UUIDAuthVelocity {

    public static SQL sql = null;
    public static Logger logger = null;
    private final ProxyServer server;
    public static Path config;
    private static Metrics.Factory metricsFactory;

    @Inject
    public UUIDAuthVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        if (!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdir();
        }
        this.server = server;
        UUIDAuthVelocity.logger = logger;
        UUIDAuthVelocity.config = dataDirectory;
        UUIDAuthVelocity.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        metricsFactory.make(this, 15683);
        server.getEventManager().register(this, new PlayerJoinListener());

        sql = new SQL();
        try { sql.connect(); }
        catch (SQLException | ClassNotFoundException e) { throw new RuntimeException(e); }

        if (ConfigReader.getConfig().autoupdateEnabled) {
            if (VelocityUpdater.isLatest(102870)) {
                logger.info("§aYou are using the latest version of UUIDAuthenticator!");
            } else {
                logger.info("There is a new version of UUIDAuthenticator available!");
                logger.info("§aSince autoupdate is enabled, we will download the latest version of UUIDAuthenticator for you!");
                VelocityUpdater.updatePlugin(102870, config);
                logger.info("§aYou are now using the latest version of UUIDAuthenticator!");
            }
        } else {
            if (VelocityUpdater.isLatest(102870)) {
                logger.info("§aYou are using the latest version of UUIDAuthenticator!");
            } else {
                logger.warn("There is a new version of UUIDAuthenticator available! Please download the new version for the latest security features!");
            }
        }
    }

    public static SQL getSql() {
        return sql;
    }
}
