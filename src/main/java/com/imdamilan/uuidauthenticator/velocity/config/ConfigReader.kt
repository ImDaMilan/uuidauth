package com.imdamilan.uuidauthenticator.velocity.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.imdamilan.uuidauthenticator.velocity.UUIDAuthVelocity
import java.io.File
import java.io.IOException

class ConfigReader {
    companion object {
        val config: Config
            get() {
                val configFile = File(UUIDAuthVelocity.config!!.toFile().absolutePath, "config.yml")
                if (!configFile.exists()) {
                    try {
                        configFile.createNewFile()
                        val config = Config(false,"localhost", 3306, "root", "", "uuid_auth", "uuid_auth",
                            fileAuthEnabled = false,
                            autoupdateEnabled = true
                        )
                        val mapper = ObjectMapper(YAMLFactory())
                        mapper.writeValue(configFile, config)
                        return config
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                val om = ObjectMapper(YAMLFactory())
                val configValues: Config = try {
                    om.readValue(
                        configFile,
                        Config::class.java
                    )
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
                return configValues
            }
    }
}
