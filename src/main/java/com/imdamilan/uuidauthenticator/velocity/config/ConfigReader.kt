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
                val configFile: File =
                    File(UUIDAuthVelocity.Companion.config!!.toFile().getAbsolutePath(), "config.yml")
                if (!configFile.exists()) {
                    try {
                        configFile.createNewFile()
                        val config = Config("localhost", 3306, "root", "", "uuid_auth", "uuid_auth", true)
                        val mapper = ObjectMapper(YAMLFactory())
                        mapper.writeValue(configFile, config)
                        return config
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                val om = ObjectMapper(YAMLFactory())
                val configValues: Config
                configValues = try {
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
