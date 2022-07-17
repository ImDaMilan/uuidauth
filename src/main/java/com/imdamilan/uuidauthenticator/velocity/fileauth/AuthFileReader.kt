package com.imdamilan.uuidauthenticator.velocity.fileauth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.imdamilan.uuidauthenticator.velocity.UUIDAuthVelocity
import java.io.File

class AuthFileReader {
    companion object {
        val file = File(UUIDAuthVelocity.config!!.toFile().absolutePath, "auth.yml")
        val authFile: AuthFile
            get() {
                val mapper = ObjectMapper(YAMLFactory())
                return mapper.readValue(file, AuthFile::class.java)
            }

        fun setup() {
            if (!file.exists()) {
                file.createNewFile()
                val placeholder: LinkedHashMap<String, String> = LinkedHashMap()
                val authFile = AuthFile(placeholder)
                val mapper = ObjectMapper(YAMLFactory())
                mapper.writeValue(file, authFile)
            }
        }
    }
}
