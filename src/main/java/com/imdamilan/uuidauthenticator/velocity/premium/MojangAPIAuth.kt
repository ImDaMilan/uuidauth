package com.imdamilan.uuidauthenticator.velocity.premium

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object MojangAPIAuth {
    @Throws(IOException::class)
    fun getUUID(username: String): String {
        val url = URL("https://api.mojang.com/users/profiles/minecraft/$username")
        val connection = url.openConnection()
        val inputStream = connection.getInputStream()
        val inputString = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).readLine()
        return inputString.substring(inputString.indexOf("id\":\"") + 5, inputString.lastIndexOf("\""))
    }

    @Throws(IOException::class)
    fun isPremium(name: String): Boolean {
        val url = URL("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=$name&serverId=")
        val connection = url.openConnection() as HttpURLConnection
        return connection.responseCode == 204
    }

    fun parseUUID(uuid: String?): String {
        val uuidWithoutDashes = uuid!!.replace("-", "")
        return uuidWithoutDashes.substring(0, 8) + "-" + uuidWithoutDashes.substring(
            8, 12
        ) + "-" + uuidWithoutDashes.substring(12, 16) + "-" + uuidWithoutDashes.substring(
            16, 20
        ) + "-" + uuidWithoutDashes.substring(20)
    }
}