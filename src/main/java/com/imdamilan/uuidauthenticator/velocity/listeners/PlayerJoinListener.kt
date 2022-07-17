package com.imdamilan.uuidauthenticator.velocity.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.imdamilan.uuidauthenticator.velocity.UUIDAuthVelocity
import com.imdamilan.uuidauthenticator.velocity.config.ConfigReader
import com.imdamilan.uuidauthenticator.velocity.fileauth.AuthFile
import com.imdamilan.uuidauthenticator.velocity.fileauth.AuthFileReader
import com.imdamilan.uuidauthenticator.velocity.premium.MojangAPIAuth
import com.imdamilan.uuidauthenticator.velocity.sql.SQL
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import java.sql.*
import java.util.*

class PlayerJoinListener {

    private var connection: Connection? = null
    private var config = ConfigReader.config
    private var tableName = config.databaseTable
    private var databaseName = config.databaseName

    @Subscribe
    fun onPlayerJoin(event: LoginEvent) {
        val player = event.player
        var uuid = player.uniqueId
        if (!playerExists(player)) newPlayer(player)

        try {
            if (MojangAPIAuth.isPremium(player.username)) {
                uuid = UUID.fromString(MojangAPIAuth.parseUUID(MojangAPIAuth.getUUID(player.username)))
            }
        } catch (e: Exception) {
            uuid = player.uniqueId
        }

        if (ConfigReader.config.databaseAuthEnabled) {
            val sql: SQL? = UUIDAuthVelocity.sql
            try {
                connection = UUIDAuthVelocity.sql?.connection
                val name = player.username

                try {
                    val statement = connection!!.prepareStatement("SELECT uuid FROM `$tableName` WHERE username = ?")
                    statement.setString(1, name)
                    val result = statement.executeQuery()
                    var uuidFromDatabase: String? = ""

                    if (result != null) {
                        if (result.next()) {
                            uuidFromDatabase = result.getString("uuid")
                        }
                    }

                    if (uuidFromDatabase != uuid.toString()) {
                        player.disconnect(Component.text("Your UUID is not matching to your username, are you trying to access someone else's account? If you believe this is a mistake, contact the server's admin."))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: SQLNonTransientException) {
                if (sql?.isConnected == true) sql.disconnect()
                sql?.connect()
                event.player.disconnect(Component.text("The server had to reconnect to the database, please, try to join again in a few seconds."))
            }
        } else if (ConfigReader.config.fileAuthEnabled) {
            val authFile = ObjectMapper(YAMLFactory()).readValue(AuthFileReader.file, AuthFile::class.java).players
            if (authFile.containsKey(player.username)) {
                if (authFile[player.username] != uuid.toString()) {
                    player.disconnect(Component.text("Your UUID is not matching to your username, are you trying to access someone else's account? If you believe this is a mistake, contact the server's admin."))
                }
            }
        }
    }

    private fun playerExists(player: Player): Boolean {
        val name = player.username

        if (ConfigReader.config.databaseAuthEnabled) {
            return try {
                val statement =
                    connection!!.prepareStatement("SELECT `uuid` FROM `$databaseName`.`$tableName` WHERE `username` = ?;")
                statement.setString(1, name)
                val result = statement.executeQuery()
                result.next()
            } catch (e: Exception) { false }

        } else if (ConfigReader.config.fileAuthEnabled) {
            val file = AuthFileReader.file
            val lines = file.readLines()
            for (line in lines) {
                if (line.contains(name)) {
                    return true
                }
            }
            return false
        }
        return false
    }

    private fun newPlayer(player: Player) {
        var uuid = player.uniqueId
        val name = player.username

        try {
            if (MojangAPIAuth.isPremium(name)) {
                uuid = UUID.fromString(MojangAPIAuth.parseUUID(MojangAPIAuth.getUUID(name)))
            }
        } catch (e: Exception) {
            uuid = player.uniqueId
        }

        if (ConfigReader.config.databaseAuthEnabled) {
            val sql = "INSERT INTO `$tableName` (username, uuid) VALUES (?, ?)"
            val statement = connection!!.prepareStatement(sql)
            statement.setString(1, name)
            statement.setString(2, uuid.toString())
            statement.executeUpdate()
            statement.close()

        } else if (ConfigReader.config.fileAuthEnabled) {
            AuthFileReader.addPlayer(name, uuid.toString())
        }
    }
}
