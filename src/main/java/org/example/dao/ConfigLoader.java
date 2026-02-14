package org.example.dao;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

        static {
            try (InputStream input = ConfigLoader.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties")) {

                if (input == null) {
                    throw new RuntimeException("application.properties not found");
                }

                properties.load(input);

            } catch (Exception e) {
                throw new RuntimeException("Failed to load properties", e);
            }
        }

        public static String getProperty(String key) {
            return properties.getProperty(key);
        }
    }

