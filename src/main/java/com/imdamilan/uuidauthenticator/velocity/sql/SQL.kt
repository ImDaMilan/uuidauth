package com.imdamilan.uuidauthenticator.velocity.sql;

import com.imdamilan.uuidauthenticator.velocity.config.Config;
import com.imdamilan.uuidauthenticator.velocity.config.ConfigReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQL {
    public Connection connection = null;
    Config config = ConfigReader.getConfig();
    String address = config.address;
    int port = config.port;
    String database = config.databaseName;
    String tableName = config.tableName;
    String username = config.username;
    String password = config.password;

    public boolean isConnected() {
        return connection != null;
    }

    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + database + "?useSSL=false", username, password);

        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + tableName + "` (`username` VARCHAR(16) NOT NULL , `uuid` VARCHAR(36) NOT NULL, PRIMARY KEY (`username`)) ENGINE = InnoDB;");
        statement.executeUpdate();
        statement.close();
    }

    public void disconnect() throws SQLException {
        if (isConnected()) connection.close();
    }
}
