package com.imdamilan.uuidauthenticator.velocity.config;

public class Config {
    public String address = "localhost";
    public int port = 3306;
    public String username = "root";
    public String password  = "";
    public String databaseName = "uuid_auth";
    public String tableName = "uuid_auth";
    public boolean autoupdateEnabled = false;

    public Config() {}

    public Config(String address, int port, String username, String password, String databaseName, String tableName, boolean autoupdateEnabled) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.autoupdateEnabled = autoupdateEnabled;
    }
}
