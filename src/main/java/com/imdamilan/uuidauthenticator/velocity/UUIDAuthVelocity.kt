package com.imdamilan.uuidauthenticator.velocity

import com.google.inject.Inject
import com.imdamilan.uuidauthenticator.velocity.config.ConfigReader
import com.imdamilan.uuidauthenticator.velocity.listeners.PlayerJoinListener
import com.imdamilan.uuidauthenticator.velocity.sql.SQL
import com.imdamilan.uuidauthenticator.velocity.update.Update
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.bstats.velocity.Metrics
import org.slf4j.Logger
import java.nio.file.Path
import java.sql.SQLException

@Plugin(id = "uuidauth", name = "UUIDAuth", version = "1.0", authors = ["ImDaMilan"])

class UUIDAuthVelocity @Inject constructor(
    server: ProxyServer,
    logger: Logger?,
    @DataDirectory dataDirectory: Path,
    metricsFactory: Metrics.Factory?){

    private val server: ProxyServer

    init {
        if (!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdir()
        }
        this.server = server
        Companion.logger = logger
        config = dataDirectory
        Companion.metricsFactory = metricsFactory
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent?) {
        metricsFactory!!.make(this, 15683)
        server.eventManager.register(this, PlayerJoinListener())
        sql = SQL()
        try {
            sql!!.connect()
        } catch (e: SQLException) {
            throw RuntimeException(e)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
        if (ConfigReader.config.autoupdateEnabled) {
            if (Update.isLatest(102870)) {
                logger!!.info("§aYou are using the latest version of UUIDAuthenticator!")
            } else {
                logger!!.info("There is a new version of UUIDAuthenticator available!")
                logger!!.info("§aSince autoupdate is enabled, we will download the latest version of UUIDAuthenticator for you!")
                Update.updatePlugin(102870)
                logger!!.info("§aYou are now using the latest version of UUIDAuthenticator!")
            }
        } else {
            if (Update.isLatest(102870)) {
                logger!!.info("§aYou are using the latest version of UUIDAuthenticator!")
            } else {
                logger!!.warn("There is a new version of UUIDAuthenticator available! Please download the new version for the latest security features!")
            }
        }
    }

    companion object {
        var sql: SQL? = null
        var logger: Logger? = null
        var config: Path? = null
        private var metricsFactory: Metrics.Factory? = null
    }
}
