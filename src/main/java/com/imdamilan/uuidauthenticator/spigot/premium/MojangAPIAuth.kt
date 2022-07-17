package com.imdamilan.uuidauthenticator.spigot.premium

import java.net.HttpURLConnection
import java.net.URL

class MojangAPIAuth {
    companion object {
        fun getUUID(username: String): String {
            val url = URL("https://api.mojang.com/users/profiles/minecraft/$username")
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val inputString = inputStream.bufferedReader().use { it.readText() }
            val json = inputString.substringAfter("{").substringBefore("}")
            return json.substringAfter("id\":\"").substringBefore("\"")
        }

        fun isPremium(name: String): Boolean {
            val url = URL("https://sessionserver.mojang.com/session/minecraft/hasJoined?username=$name&serverId=")
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            return connection.responseCode == 204
        }
    }
}
