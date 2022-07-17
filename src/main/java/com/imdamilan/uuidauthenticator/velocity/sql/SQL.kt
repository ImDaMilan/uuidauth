package com.imdamilan.uuidauthenticator.velocity.sql

import com.imdamilan.uuidauthenticator.velocity.config.ConfigReader
import java.sql.Connection
import java.sql.DriverManager

class SQL {

    private var config = ConfigReader.config
    private var address = config.address
    private var port = config.port
    private var tableName = config.tableName
    private var username = config.username
    private var password = config.password

    var database = config.databaseName
    var connection: Connection? = null

    val isConnected: Boolean
        get() = connection != null

    fun connect() {
        Class.forName("com.mysql.cj.jdbc.Driver")
        connection =
            DriverManager.getConnection("jdbc:mysql://$address:$port/$database?useSSL=false", username, password)
        val statement =
            connection?.prepareStatement("CREATE TABLE IF NOT EXISTS `$tableName` (`username` VARCHAR(16) NOT NULL , `uuid` VARCHAR(36) NOT NULL, PRIMARY KEY (`username`)) ENGINE = InnoDB;")
        statement?.executeUpdate()
        statement?.close()
    }

    fun disconnect() {
        if (isConnected) connection!!.close()
    }
}