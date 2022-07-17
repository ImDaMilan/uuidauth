package com.imdamilan.uuidauthenticator.velocity.listeners;

import com.imdamilan.uuidauthenticator.velocity.UUIDAuthVelocity;
import com.imdamilan.uuidauthenticator.velocity.config.Config;
import com.imdamilan.uuidauthenticator.velocity.config.ConfigReader;
import com.imdamilan.uuidauthenticator.velocity.premium.MojangAPIAuth;
import com.imdamilan.uuidauthenticator.velocity.sql.SQL;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.sql.*;
import java.util.Objects;
import java.util.UUID;

import static com.imdamilan.uuidauthenticator.velocity.premium.MojangAPIAuth.parseUUID;

public class PlayerJoinListener{

    private Connection connection = null;
    Config config = ConfigReader.getConfig();
    String tableName = config.tableName;
    String databaseName = config.databaseName;

    @Subscribe
    void onPlayerJoin(LoginEvent event) throws SQLException, ClassNotFoundException {
        SQL sql = UUIDAuthVelocity.getSql();
        try {
            connection = UUIDAuthVelocity.getSql().connection;

            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            String name = player.getUsername();

            try {
                if (MojangAPIAuth.isPremium(name)) {
                    uuid = UUID.fromString(parseUUID(MojangAPIAuth.getUUID(name)));
                }
            } catch (Exception e) {
                uuid = player.getUniqueId();
            }

            if (!playerExists(player)) newPlayer(player);
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM `" + tableName + "` WHERE username = ?");
                statement.setString(1, name);
                ResultSet result = statement.executeQuery();
                String uuidFromDatabase = "";

                if (result != null) {
                    if (result.next()) {
                        uuidFromDatabase = result.getString("uuid");
                    }
                }
                if (!Objects.equals(uuidFromDatabase, uuid.toString())) {
                    player.disconnect(Component.text("Your UUID is not matching to your username, are you trying to access someone else's account? If you believe this is a mistake, contact the server's admin."));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLNonTransientException e) {
            if (sql.isConnected()) sql.disconnect();
            sql.connect();
            event.getPlayer().disconnect(Component.text("The server had to reconnect to the database, please, try to join again in a few seconds."));
        }
    }

    private boolean playerExists(Player player) {
        String name = player.getUsername();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT `uuid` FROM `" + databaseName + "`.`" + tableName +"` WHERE `username` = ?;");
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (Exception e) {
            return false;
        }
    }

    private void newPlayer(Player player) throws SQLException {
        UUID uuid = player.getUniqueId();
        String name = player.getUsername();

        try {
            if (MojangAPIAuth.isPremium(name)) {
                uuid = UUID.fromString(parseUUID(MojangAPIAuth.getUUID(name)));
            }
        } catch (Exception e) {
            uuid = player.getUniqueId();
        }

        String sql = "INSERT INTO `" + tableName + "` (username, uuid) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.setString(2, uuid.toString());
        statement.executeUpdate();
        statement.close();
    }
}
