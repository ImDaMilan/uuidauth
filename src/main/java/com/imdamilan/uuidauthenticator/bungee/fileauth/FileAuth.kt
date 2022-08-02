package com.imdamilan.uuidauthenticator.bungee.fileauth

import com.imdamilan.uuidauthenticator.bungee.UUIDAuth
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.nio.file.Path

class FileAuth {

    companion object {
        lateinit var authFile: Configuration
        private lateinit var file: File

        fun initAuthFile() {
            val path: Path = File(UUIDAuth.instance.proxy.pluginsFolder.absolutePath + "\\UUIDAuth").toPath().toAbsolutePath()
            if (!path.toFile().exists()) {
                path.toFile().mkdir()
            }

            val file = File(path.toString(), "auth.yml")
            if (!file.exists())
                file.createNewFile()
            authFile = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(authFile, file)
        }

        fun addPlayer(player: String, uuid: String) {
            authFile.set("players.$player", uuid)
            save()
        }

        fun get(player: String): String {
            return authFile.getString("players.$player")
        }

        fun save() {
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(authFile, File(UUIDAuth.instance.dataFolder, "auth.yml"))
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
        }
    }
}
