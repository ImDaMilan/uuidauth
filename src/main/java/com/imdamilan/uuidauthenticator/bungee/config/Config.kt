package com.imdamilan.uuidauthenticator.bungee.config

import com.imdamilan.uuidauthenticator.bungee.UUIDAuth
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.nio.file.Path

class Config {

    companion object {
        fun initConfig() {
            val path: Path = File(UUIDAuth.instance.proxy.pluginsFolder.absolutePath + "\\UUIDAuth").toPath().toAbsolutePath()
            if (!path.toFile().exists()) {
                path.toFile().mkdir()
            }

            val file = File(path.toString(), "config.yml")
            if (!file.exists())
                file.createNewFile()
            UUIDAuth.config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(UUIDAuth.config, file)

            initValues()
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(UUIDAuth.config, file)
        }

        private fun initValues() {
            if (!UUIDAuth.config.contains("database.enabled")) UUIDAuth.config.set("database.enabled", false)
            if (!UUIDAuth.config.contains("database.address")) UUIDAuth.config.set("database.address", "localhost")
            if (!UUIDAuth.config.contains("database.port")) UUIDAuth.config.set("database.port", 3306)
            if (!UUIDAuth.config.contains("database.name")) UUIDAuth.config.set("database.name", "minecraft")
            if (!UUIDAuth.config.contains("database.username")) UUIDAuth.config.set("database.username", "root")
            if (!UUIDAuth.config.contains("database.password")) UUIDAuth.config.set("database.password", "")
            if (!UUIDAuth.config.contains("database.table-name")) UUIDAuth.config.set("database.table-name", "uuid_authenticator")
            if (!UUIDAuth.config.contains("file-auth.enabled")) UUIDAuth.config.set("file-auth.enabled", false)
            if (!UUIDAuth.config.contains("kick-message")) UUIDAuth.config.set("kick-message", "Your UUID is not matching to your username, are you trying to access someone else's account? If you believe this is a mistake, contact the server's admin.")
            if (!UUIDAuth.config.contains("autoupdate-enabled")) UUIDAuth.config.set("autoupdate-enabled", true)
        }
    }
}