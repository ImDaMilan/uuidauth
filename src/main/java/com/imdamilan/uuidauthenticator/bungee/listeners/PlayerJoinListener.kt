package com.imdamilan.uuidauthenticator.bungee.listeners

import com.imdamilan.uuidauthenticator.bungee.UUIDAuth
import com.imdamilan.uuidauthenticator.bungee.fileauth.FileAuth
import com.imdamilan.uuidauthenticator.bungee.premium.MojangAPIAuth
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.sql.Connection
import java.util.*

class PlayerJoinListener : Listener {

    private val config = UUIDAuth.config
    private val tableName: String = config.getString("database.table-name")
    private lateinit var connection: Connection

    @EventHandler
    fun onLogin(event: PostLoginEvent) {
        val player = event.player
        var uuid = player.uniqueId

        try {
            if (MojangAPIAuth.isPremium(player.name))
                uuid = UUID.fromString(parseUUID(MojangAPIAuth.getUUID(player.name)))
        } catch (e: Exception) {
            uuid = player.uniqueId
        }

        if (!playerExists(player)) newPlayer(player)
        if (config.getBoolean("database.enabled")) {
            try {
                val statement =
                    connection.prepareStatement("SELECT uuid FROM $tableName WHERE username = '${player.name}'")!!
                val result = statement.executeQuery()
                var uuidFromDatabase = ""

                if (result != null) {
                    if (result.next()) {
                        uuidFromDatabase = result.getString("uuid")
                    }
                }
                if (uuidFromDatabase != uuid.toString()) {
                    player.disconnect(TextComponent(config.getString("kick-message")))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (config.getBoolean("file-auth.enabled")) {
            FileAuth.save()
            val uuidFromFile = FileAuth.authFile.getString(player.name)
            if (uuidFromFile != uuid.toString()) {
                player.disconnect(TextComponent(config.getString("kick-message")))
            }
        }
    }

    private fun playerExists(player: ProxiedPlayer): Boolean {
        val name = player.name
        if (config.getBoolean("database.enabled")) {
            connection = UUIDAuth.sql.connection

            return try {
                val sql = "SELECT * FROM $tableName WHERE `username` = '$name'"
                val result = connection.createStatement()?.executeQuery(sql) ?: return false
                result.next()
            } catch (e: Exception) { false }

        } else if (config.getBoolean("file-auth.enabled")) {
            return (FileAuth.authFile.contains(name) || FileAuth.authFile.contains("players.$name"))
        } else return false
    }

    private fun newPlayer(player: ProxiedPlayer) {
        val name = player.name
        var uuid: UUID = player.uniqueId

        try {
            if (MojangAPIAuth.isPremium(name)) {
                uuid = UUID.fromString(parseUUID(MojangAPIAuth.getUUID(name)))
            }
        } catch (e: Exception) {
            uuid = player.uniqueId
        }

        if (config.getBoolean("database.enabled")) {
            val sql = "INSERT INTO $tableName (username, uuid) VALUES ('$name', '$uuid')"
            connection.createStatement()?.executeUpdate(sql)
        } else if (config.getBoolean("file-auth.enabled")) {
            FileAuth.addPlayer(name, uuid.toString())
        }
    }

    private fun parseUUID(uuid: String): String {
        val dashes = uuid.replace("-", "")
        return dashes.substring(0, 8) + "-" + dashes.substring(8, 12
        ) + "-" + dashes.substring(12, 16) + "-" + dashes.substring(16, 20
        ) + "-" + dashes.substring(20)
    }
}