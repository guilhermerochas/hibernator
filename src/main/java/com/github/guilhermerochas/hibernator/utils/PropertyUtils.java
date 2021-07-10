package com.github.guilhermerochas.hibernator.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtils {
    public static Properties readPropertiesFromFile(String filename) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;

        try {
            fis = new FileInputStream(filename);
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                fis.close();
        }

        return prop;
    }
}
