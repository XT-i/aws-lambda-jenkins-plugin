package com.xti.jenkins.plugin.awslambda;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestUtil {
    public File getResource(String resourcePath){
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(resourcePath);
        if(resource != null){
            try {
                File tempEcho = File.createTempFile("aws-lambda-plugin", "echo.zip");
                FileUtils.copyFile(new File(resource.getFile()), tempEcho);
                tempEcho.deleteOnExit();
                return tempEcho;
            } catch (IOException e) {
                throw new IllegalStateException("Could not load " + resourcePath);
            }

        } else {
            throw new IllegalStateException("Could not load " + resourcePath);
        }
    }
}
