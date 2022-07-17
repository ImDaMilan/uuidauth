package com.imdamilan.uuidauthenticator.spigot.fileauth

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class FileAuth {

    companion object {
        private var authFile: FileConfiguration? = null
        private var file: File? = null

        fun setupFileAuth() {
            file = File(Bukkit.getServer().pluginManager.getPlugin("UUIDAuthenticator").dataFolder, "auth.yml")
            if (!file!!.exists()) {
                file!!.createNewFile()
            }
            authFile = YamlConfiguration.loadConfiguration(file)
        }

        fun getAuthenticationFile(): FileConfiguration? {
            return authFile
        }

        fun save() {
            try {
                authFile!!.save(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun reload() {
            authFile = YamlConfiguration.loadConfiguration(file)
        }
    }
}
