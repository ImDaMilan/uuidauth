package com.imdamilan.uuidauthenticator.velocity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.imdamilan.uuidauthenticator.velocity.UUIDAuthVelocity;

import java.io.File;
import java.io.IOException;

public class ConfigReader {

    public static Config getConfig() {
        final File configFile = new File(UUIDAuthVelocity.config.toFile().getAbsolutePath(),  "config.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Config config = new Config("localhost", 3306, "root", "", "uuid_auth", "uuid_auth", true);
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.writeValue(configFile, config);
                return config;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final ObjectMapper om = new ObjectMapper(new YAMLFactory());
        final Config configValues;

        try {
            configValues = om.readValue(configFile, Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return configValues;
    }
}
