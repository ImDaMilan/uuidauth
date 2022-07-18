package com.imdamilan.uuidauthenticator.spigot.listeners

import com.imdamilan.uuidauthenticator.spigot.UUIDAuthenticator
import com.imdamilan.uuidauthenticator.spigot.fileauth.FileAuth
import com.imdamilan.uuidauthenticator.spigot.premium.MojangAPIAuth
import com.imdamilan.uuidauthenticator.spigot.sql.SQL
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.sql.Connection
import java.sql.SQLNonTransientException
import java.util.*

class PlayerJoinListener : Listener {

    private var sql: SQL? = null
    private var connection: Connection? = null
    private var tableName = "uuid_authenticator"
    private val config: FileConfiguration? = UUIDAuthenticator.getConfig()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (config!!.getBoolean("database.enabled")) {
            try {
                tableName = config.getString("database.table-name").toString()
                sql = UUIDAuthenticator.getSQL()
                connection = sql!!.connection

                val player = event.player
                val name = player.name
                var uuid: UUID = player.uniqueId
                try {
                    if (MojangAPIAuth.isPremium(name)) {
                        uuid = UUID.fromString(parseUUID(MojangAPIAuth.getUUID(name)))
                    }
                } catch (e: Exception) {
                    uuid = player.uniqueId
                }

                if (!playerExists(player)) newPlayer(player)
                try {
                    val statement =
                        connection?.prepareStatement("SELECT uuid FROM $tableName WHERE username = '$name'")!!
                    val result = statement.executeQuery()
                    var uuidFromDatabase = ""

                    if (result != null) {
                        if (result.next()) {
                            uuidFromDatabase = result.getString("uuid")
                        }
                    }
                    if (uuidFromDatabase != uuid.toString()) {
                        player.kickPlayer("Your UUID is not matching to your username, are you trying to access someone else's account? If you believe this is a mistake, contact the server's admin.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: SQLNonTransientException) {
                if (sql?.isConnected() == true) sql!!.disconnect()
                sql?.connect()
                event.player.kickPlayer("The server had to reconnect to the database, please, try to join again in a few seconds.")
            }
        } else if (config.getBoolean("file-auth.enabled")) {
            FileAuth.reload()
            val player = event.player
            val name = player.name
            var uuid: UUID = player.uniqueId

            try {
                if (MojangAPIAuth.isPremium(name)) {
                    uuid = UUID.fromString(parseUUID(MojangAPIAuth.getUUID(name)))
                }
            } catch (e: Exception) {
                uuid = player.uniqueId
            }
            if (!playerExists(player)) newPlayer(player)

            val uuidFromFile = FileAuth.getAuthenticationFile()?.getString(name)
            if (uuidFromFile != uuid.toString()) {
                player.kickPlayer("Your UUID is not matching to your username, are you trying to access someone else's account? If you believe this is a mistake, contact the server's admin.")
            }
        }
    }

    private fun playerExists(player: Player): Boolean {
        val name = player.name
        if (config!!.getBoolean("database.enabled")) {
            try {
                val sql = "SELECT * FROM $tableName WHERE `username` = '$name'"
                val result = connection?.createStatement()?.executeQuery(sql) ?: return false
                if (result.next()) {
                    return true
                }
            } catch (e: Exception) {
                return false
            }
            return false
        } else if (config.getBoolean("file-auth.enabled")) {
            return FileAuth.getAuthenticationFile()?.contains(name) ?: false
        } else return false
    }

    private fun newPlayer(player: Player) {
        val name = player.name
        var uuid: UUID = player.uniqueId

        try {
            if (MojangAPIAuth.isPremium(name)) {
                uuid = UUID.fromString(parseUUID(MojangAPIAuth.getUUID(name)))
            }
        } catch (e: Exception) {
            uuid = player.uniqueId
        }

        if (config!!.getBoolean("database.enabled")) {

            val sql = "INSERT INTO $tableName (username, uuid) VALUES ('$name', '$uuid')"
            connection?.createStatement()?.executeUpdate(sql)

        } else if (config.getBoolean("file-auth.enabled")) {
            FileAuth.getAuthenticationFile()?.addDefault(name, uuid.toString())
            FileAuth.getAuthenticationFile()?.options()?.copyDefaults(true)
            FileAuth.save()
            FileAuth.reload()
        }
    }

    private fun parseUUID(uuid: String): String {
        val uuidWithoutDashes = uuid.replace("-", "")
        return uuidWithoutDashes.substring(0, 8) + "-" + uuidWithoutDashes.substring(8, 12
        ) + "-" + uuidWithoutDashes.substring(12, 16) + "-" + uuidWithoutDashes.substring(16, 20
        ) + "-" + uuidWithoutDashes.substring(20)
    }
}
