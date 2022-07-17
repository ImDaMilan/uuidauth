package com.imdamilan.uuidauthenticator.velocity.config

class Config {
    var databaseAuthEnabled = false
    var databaseAddress = "localhost"
    var databasePort = 3306
    var databaseUsername = "root"
    var databasePassword = ""
    var databaseName = "uuid_auth"
    var databaseTable = "uuid_auth"
    var fileAuthEnabled = false
    var autoupdateEnabled = false

    constructor()
    constructor(
        databaseAuthEnabled: Boolean,
        address: String,
        port: Int,
        username: String,
        password: String,
        databaseName: String,
        tableName: String,
        fileAuthEnabled: Boolean,
        autoupdateEnabled: Boolean
    ) {
        this.databaseAuthEnabled = databaseAuthEnabled
        this.databaseAddress = address
        this.databasePort = port
        this.databaseUsername = username
        this.databasePassword = password
        this.databaseName = databaseName
        this.databaseTable = tableName
        this.fileAuthEnabled = fileAuthEnabled
        this.autoupdateEnabled = autoupdateEnabled
    }
}
