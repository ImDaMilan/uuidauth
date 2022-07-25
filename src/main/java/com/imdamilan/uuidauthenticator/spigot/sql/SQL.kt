package com.imdamilan.uuidauthenticator.spigot.sql

import com.imdamilan.uuidauthenticator.spigot.UUIDAuthenticator
import org.bukkit.configuration.file.FileConfiguration
import java.sql.Connection

class SQL {

    lateinit var connection: Connection

    fun isConnected(): Boolean {
        return connection.isValid(0)
    }

    fun connect() {
        val config: FileConfiguration = UUIDAuthenticator.getConfig()

        val address = config.getString("database.address")
        val port = config.getString("database.port")
        val database = config.getString("database.name")
        val username = config.getString("database.username")
        val password = config.getString("database.password")
        val name = config.getString("database.table-name")

        connection = java.sql.DriverManager.getConnection("jdbc:mysql://$address:$port/$database?useSSL=false", username, password)
        val connection2 = connection

        val statement = connection2.createStatement()
        statement?.executeUpdate("CREATE TABLE IF NOT EXISTS $name (username VARCHAR(16) NOT NULL, uuid VARCHAR(36) NOT NULL, PRIMARY KEY (username))")
        statement?.close()
        connection2.close()
        connection = java.sql.DriverManager.getConnection("jdbc:mysql://$address:$port/$database?useSSL=false", username, password)
    }

    fun disconnect() {
        if (isConnected()) connection.close()
    }
}
