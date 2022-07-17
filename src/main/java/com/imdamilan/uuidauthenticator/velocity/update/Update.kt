package com.imdamilan.uuidauthenticator.velocity.update

import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.*
import java.net.URL

class Update {

    companion object {

        private var current = "1.0"

        @JvmStatic
        fun isLatest(projectID: Int): Boolean {
            return current == getLatest(projectID)
        }

        @JvmStatic
        private fun getVersionFromJSON(jsonString: String?): String {
            val `object`: JSONObject = JSONValue.parse(jsonString) as JSONObject
            return `object`["current_version"].toString()
        }

        @JvmStatic
        private fun getLatest(projectID: Int): String {
            return try {
                val url =
                    URL("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=$projectID")
                val connection = url.openConnection()
                val fromSite =
                    BufferedReader(InputStreamReader(connection.getInputStream())).readLine()
                getVersionFromJSON(fromSite)
            } catch (var6: Exception) {
                var6.printStackTrace()
                current
            }
        }

        @JvmStatic
        fun updatePlugin(projectID: Int): Boolean? {
            val downloadURL = "https://api.spiget.org/v2/resources/$projectID/download"
            val version: String = try {
                getLatest(projectID)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return if (!version.equals(current, ignoreCase = true)) startDownload(downloadURL) else null
        }

        @JvmStatic
        private fun startDownload(downloadURL: String): Boolean {
            return try {
                val url = URL(downloadURL)
                var `in`: BufferedInputStream? = null
                var out: FileOutputStream? = null
                try {
                    `in` = BufferedInputStream(url.openStream())
                    out = FileOutputStream("plugins" + File.separator + "UUIDAuth.jar")
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
