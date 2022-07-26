package com.imdamilan.uuidauthenticator.bungee

import com.imdamilan.uuidauthenticator.bungee.fileauth.FileAuth
import com.imdamilan.uuidauthenticator.bungee.listeners.PlayerJoinListener
import com.imdamilan.uuidauthenticator.bungee.sql.SQL
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import org.bstats.bungeecord.Metrics
import java.io.File

class UUIDAuth : Plugin() {

    companion object {
        lateinit var sql: SQL
        lateinit var instance: UUIDAuth
        lateinit var config: Configuration
    }

    override fun onEnable() {
        instance = this
        initConfig()
        Metrics(this, 15919)
        logger.info("UUIDAuth is enabled!")
        proxy.pluginManager.registerListener(this, PlayerJoinListener())

        if (config.getBoolean("database.enabled")) {
            sql = SQL()
            sql.connect()
        } else if (config.getBoolean("file-auth.enabled")) {
            FileAuth.initAuthFile()
        }
    }

    override fun onDisable() {
        logger.info("UUIDAuth is disabled!")
    }

    private fun initConfig() {
        val file = File(dataFolder, "config.yml")
        if (!file.exists())
            file.createNewFile()
        config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(config, file)

        config.set("database.enabled", true)
        config.set("database.address", "localhost")
        config.set("database.port", 3306)
        config.set("database.name", "minecraft")
        config.set("database.username", "root")
        config.set("database.password", "")
        config.set("database.table-name", "uuid_authenticator")
        config.set("file-auth.enabled", true)
        config.set("kick-message", "Your UUID is not matching to your username, are you trying to access someone else's account? If you believe this is a mistake, contact the server's admin.")
        config.set("autoupdate-enabled", true)
        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(config, file)
    }
}
