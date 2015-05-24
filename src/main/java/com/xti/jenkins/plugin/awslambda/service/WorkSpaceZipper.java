package com.xti.jenkins.plugin.awslambda.service;

import hudson.FilePath;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorkSpaceZipper {
    private FilePath workSpace;
    private JenkinsLogger logger;

    public WorkSpaceZipper(FilePath workSpace, JenkinsLogger logger) {
        this.workSpace = workSpace;
        this.logger = logger;
    }

    public File getZip(String artifactLocation) throws IOException, InterruptedException {
        FilePath artifactFilePath = null;

        if(StringUtils.isNotEmpty(artifactLocation)) {
            artifactFilePath = new FilePath(workSpace, artifactLocation);
        }
        File zipFile = null;
        if(artifactFilePath != null){
            zipFile = getArtifactZip(artifactFilePath);
        }

        return zipFile;
    }

    private File getArtifactZip(FilePath artifactLocation) throws IOException, InterruptedException {
        File resultFile = File.createTempFile("awslambda-", ".zip");

        if (!artifactLocation.isDirectory()) {
            logger.log("Copying zip file");
            artifactLocation.copyTo(new FileOutputStream(resultFile));
        } else {
            logger.log("Zipping folder ..., copying zip file");
            artifactLocation.zip(new FileOutputStream(resultFile));
        }

        logger.log("File Name: %s%nAbsolute Path: %s%nFile Size: %d", resultFile.getName(), resultFile.getAbsolutePath(), resultFile.length());
        return resultFile;
    }
}
