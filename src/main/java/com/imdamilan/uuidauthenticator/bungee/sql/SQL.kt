package com.imdamilan.uuidauthenticator.bungee.sql

import com.imdamilan.uuidauthenticator.bungee.UUIDAuth
import java.sql.Connection
import java.sql.DriverManager

class SQL {

    private var address = UUIDAuth.config.getString("database.enabled")
    private var port = UUIDAuth.config.getString("database.port")
    private var tableName = UUIDAuth.config.getString("database.table-name")
    private var username = UUIDAuth.config.getString("database.username")
    private var password = UUIDAuth.config.getString("database.password")

    var database: String = UUIDAuth.config.getString("database.name")
    lateinit var connection: Connection

    fun isConnected(): Boolean {
        return connection.isValid(0)
    }

    fun connect() {
        Class.forName("com.mysql.cj.jdbc.Driver")
        connection =
            DriverManager.getConnection("jdbc:mysql://$address:$port/$database?useSSL=false", username, password)
        val statement =
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `$tableName` (`username` VARCHAR(16) NOT NULL , `uuid` VARCHAR(36) NOT NULL, PRIMARY KEY (`username`)) ENGINE = InnoDB;")
        statement?.executeUpdate()
        statement?.close()
    }

    fun disconnect() {
        if (isConnected()) connection.close()
    }
}