package com.imdamilan.uuidauthenticator.spigot

import com.imdamilan.uuidauthenticator.spigot.commands.ConnectCommand
import com.imdamilan.uuidauthenticator.spigot.commands.DisconnectCommand
import com.imdamilan.uuidauthenticator.spigot.fileauth.FileAuth
import com.imdamilan.uuidauthenticator.spigot.listeners.PlayerJoinListener
import com.imdamilan.uuidauthenticator.spigot.sql.SQL
import com.imdamilan.uuidauthenticator.spigot.update.Update
import org.bstats.bukkit.Metrics
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class UUIDAuthenticator : JavaPlugin() {

    companion object {
        private var configFile: FileConfiguration? = null
        private var sql: SQL? = null
        private var instance: UUIDAuthenticator? = null

        fun getConfig(): FileConfiguration? {
            return configFile
        }

        fun getSQL(): SQL? {
            return sql
        }
    }

    override fun onEnable() {
        instance = this

        this.saveDefaultConfig()
        configFile = this.config
        sql = SQL()

        server.pluginManager.registerEvents(PlayerJoinListener(), this)
        this.getCommand("connect")?.executor = ConnectCommand()
        this.getCommand("disconnect")?.executor = DisconnectCommand()

        if (configFile != null) {
            configFile!!.addDefault("database.enabled", true)
            configFile!!.addDefault("database.address", "localhost")
            configFile!!.addDefault("database.port", 3306)
            configFile!!.addDefault("database.name", "minecraft")
            configFile!!.addDefault("database.username", "root")
            configFile!!.addDefault("database.password", "")
            configFile!!.addDefault("database.table-name", "uuid_authenticator")
            configFile!!.addDefault("file-auth.enabled", true)
            configFile!!.addDefault("autoupdate-enabled", true)
            configFile!!.options().copyDefaults(true)
            this.saveConfig()
        } else {
            logger.severe("Config file not found!")
        }

        if (configFile!!.getBoolean("autoupdate-enabled")) {
            if (Update.isLatest(this, 102870)) {
                logger.info("§aYou are using the latest version of UUIDAuthenticator!")
            } else {
                logger.severe("There is a new version of UUIDAuthenticator available!")
                logger.info("§aSince autoupdate is enabled, we will download the latest version of UUIDAuthenticator for you!")
                Update.updatePlugin(this, 102870)
                logger.info("§aYou are now using the latest version of UUIDAuthenticator!")
            }
        } else {
            if (Update.isLatest(this, 102870)) {
                logger.info("§aYou are using the latest version of UUIDAuthenticator!")
            } else {
                logger.severe("There is a new version of UUIDAuthenticator available! Please download the new version for the latest security features!")
            }
        }
        Metrics(this, 15682)

        if (configFile!!.getBoolean("database.enabled")) {
            try { sql!!.connect() } catch (e: Exception) {
                logger.severe("Could not connect to database!")
                logger.severe(e.message)
                logger.severe("Please check if you have the correct credentials in the config.yml file and is your database turned on!")
            }

            if (sql!!.isConnected()) {
                logger.info("§aConnected to database! Authentication is now turned on!")
            } else {
                logger.severe("Could not connect to database!")
            }

        } else if (configFile!!.getBoolean("file-auth.enabled")) {

            FileAuth.setupFileAuth()
            FileAuth.getAuthenticationFile()?.options()?.copyDefaults(true)
            FileAuth.save()

        } else {
            logger.severe("§cYou haven't enabled any authentication method!")
        }
    }
}
