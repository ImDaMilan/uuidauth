package com.imdamilan.uuidauthenticator.velocity.config

class Config {
    var address = "localhost"
    var port = 3306
    var username = "root"
    var password = ""
    var databaseName = "uuid_auth"
    var tableName = "uuid_auth"
    var autoupdateEnabled = false

    constructor() {}
    constructor(
        address: String,
        port: Int,
        username: String,
        password: String,
        databaseName: String,
        tableName: String,
        autoupdateEnabled: Boolean
    ) {
        this.address = address
        this.port = port
        this.username = username
        this.password = password
        this.databaseName = databaseName
        this.tableName = tableName
        this.autoupdateEnabled = autoupdateEnabled
    }
}