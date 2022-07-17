package com.imdamilan.uuidauthenticator.velocity.premium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class MojangAPIAuth {
    public static String getUUID(String username) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
        URLConnection connection = url.openConnection();
        InputStream inputStream = connection.getInputStream();
        String inputString = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).readLine();
        return inputString.substring(inputString.indexOf("id\":\"") + 5, inputString.lastIndexOf("\""));
    }

    public static Boolean isPremium(String name) throws IOException {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + name + "&serverId=");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection.getResponseCode() == 204;
    }

    public static String parseUUID(String uuid) {
        String uuidWithoutDashes = uuid.replace("-", "");
        return uuidWithoutDashes.substring(0, 8) + "-" + uuidWithoutDashes.substring(8, 12
        ) + "-" + uuidWithoutDashes.substring(12, 16) + "-" + uuidWithoutDashes.substring(16, 20
        ) + "-" + uuidWithoutDashes.substring(20);
    }
}
