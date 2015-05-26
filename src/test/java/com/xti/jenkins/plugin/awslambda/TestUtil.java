package com.xti.jenkins.plugin.awslambda;

import java.io.File;
import java.net.URL;

public class TestUtil {
    public File getResource(String resourcePath){
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(resourcePath);
        if(resource != null){
            return new File(resource.getFile());
        } else {
            throw new IllegalStateException("Could not load " + resourcePath);
        }
    }
}
