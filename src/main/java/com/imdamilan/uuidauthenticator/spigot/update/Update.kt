package com.imdamilan.uuidauthenticator.spigot.update

import org.bukkit.plugin.Plugin
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.*
import java.net.URL

class Update {

    companion object {

        @JvmStatic
        fun isLatest(plugin: Plugin, projectID: Int): Boolean {
            return plugin.description.version == getLatest(plugin, projectID)
        }

        @JvmStatic
        private fun getVersionFromJSON(jsonString: String?): String {
            val `object`: JSONObject = JSONValue.parse(jsonString) as JSONObject
            return `object`["current_version"].toString()
        }

        @JvmStatic
        private fun getLatest(plugin: Plugin, projectID: Int): String {
            return try {
                val url =
                    URL("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=$projectID")
                val connection = url.openConnection()
                val fromSite =
                    BufferedReader(InputStreamReader(connection.getInputStream())).readLine()
                getVersionFromJSON(fromSite)
            } catch (var6: Exception) {
                var6.printStackTrace()
                plugin.description.version
            }
        }

        @JvmStatic
        fun updatePlugin(plugin: Plugin, projectID: Int): Boolean? {
            val downloadURL = "https://api.spiget.org/v2/resources/$projectID/download"
            val version: String = try {
                getLatest(plugin, projectID)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return if (!version.equals(plugin.description.version, ignoreCase = true)) startDownload(plugin, downloadURL) else null
        }

        @JvmStatic
        private fun startDownload(plugin: Plugin, downloadURL: String): Boolean {
            return try {
                val url = URL(downloadURL)
                var `in`: BufferedInputStream? = null
                var out: FileOutputStream? = null
                try {
                    `in` = BufferedInputStream(url.openStream())
                    out = FileOutputStream("plugins" + File.separator + plugin.name + ".jar")
                    val data = ByteArray(1024)
                    var count: Int
                    while (`in`.read(data, 0, 1024).also { count = it } != -1) {
                        out.write(data, 0, count)
                    }
                    return true
                } catch (_: Exception) {
                } finally {
                    `in`?.close()
                    out?.close()
                }
                false
            } catch (e: IOException) {
                false
            }
        }
    }
}
