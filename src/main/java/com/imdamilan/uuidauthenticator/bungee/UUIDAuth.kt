package com.imdamilan.uuidauthenticator.bungee

import com.imdamilan.uuidauthenticator.bungee.config.Config
import com.imdamilan.uuidauthenticator.bungee.fileauth.FileAuth
import com.imdamilan.uuidauthenticator.bungee.listeners.PlayerJoinListener
import com.imdamilan.uuidauthenticator.bungee.sql.SQL
import com.imdamilan.uuidauthenticator.bungee.update.Update
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import org.bstats.bungeecord.Metrics

class UUIDAuth : Plugin() {

    companion object {
        lateinit var sql: SQL
        lateinit var instance: UUIDAuth
        lateinit var config: Configuration
    }

    override fun onEnable() {
        instance = this
        Config.initConfig()
        Metrics(this, 15919)
        logger.info("UUIDAuth is enabled!")
        proxy.pluginManager.registerListener(this, PlayerJoinListener())

        if (config.getBoolean("database.enabled")) {
            sql = SQL()
            sql.connect()
        } else if (config.getBoolean("file-auth.enabled")) {
            FileAuth.initAuthFile()
        }
        update()
    }

    override fun onDisable() {
        logger.info("UUIDAuth is disabled!")
    }

    private fun update() {
        if (!Update.isLatest(102870)) {
            if (config.getBoolean("autoupdate-enabled")) {
                Update.updatePlugin(102870)
            } else {
                logger.severe("You are using an outdated version of UUIDAuth. Please update to the latest version!")
            }
        }
    }
}
