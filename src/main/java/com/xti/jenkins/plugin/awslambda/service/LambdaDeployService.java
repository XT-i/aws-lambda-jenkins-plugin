package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.xti.jenkins.plugin.awslambda.exception.LambdaDeployException;
import com.xti.jenkins.plugin.awslambda.upload.UpdateModeValue;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.util.LogUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LambdaDeployService {
    private AWSLambdaClient client;
    private JenkinsLogger logger;

    public LambdaDeployService(AWSLambdaClient client, JenkinsLogger logger) {
        this.client = client;
        this.logger = logger;
    }

    /**
     * Processes the Lambda deployment base on the UpdateMode and the existence of the function.
     * - Function exists:
     *   - Mode on Full or Code: update code
     *   - Mode on Full or Config: update configuration
     * - No function found:
     *   - Always call createFunction regardless of UpdateMode value
     * @param config configuration to be updated or added
     * @param functionCode FunctionCode containing either zipfile or s3 location.
     * @param updateModeValue Full, Code or Config, only used if function does not already exists.
     * @return true if successful, false in case of failure.
     */
    public Boolean deployLambda(DeployConfig config, FunctionCode functionCode, UpdateModeValue updateModeValue){
        if(functionExists(config.getFunctionName())){

            //update code
            if(UpdateModeValue.Full.equals(updateModeValue) || UpdateModeValue.Code.equals(updateModeValue)){
                if(functionCode != null) {
                    try {
                        updateCodeOnly(config.getFunctionName(), functionCode);
                    } catch (IOException e) {
                        logger.log(LogUtils.getStackTrace(e));
                        return false;
                    } catch (AmazonClientException ace){
                        logger.log(LogUtils.getStackTrace(ace));
                        return false;
                    }
                }else {
                    logger.log("Could not find file to upload.");
                    return false;
                }
            }

            //update configuration
            if(UpdateModeValue.Full.equals(updateModeValue) || UpdateModeValue.Config.equals(updateModeValue)){
                try {
                    updateConfigurationOnly(config);
                } catch (AmazonClientException ace){
                    logger.log(LogUtils.getStackTrace(ace));
                    return false;
                }
            }
            return true;

        }else {
            if(functionCode != null) {
                try {
                    createLambdaFunction(config, functionCode);
                    return true;
                } catch (IOException e) {
                    logger.log(LogUtils.getStackTrace(e));
                    return false;
                } catch (AmazonClientException ace){
                    logger.log(LogUtils.getStackTrace(ace));
                    return false;
                }
            } else {
                logger.log("Could not find file to upload.");
                return false;
            }
        }
    }

    /**
     * This method calls the AWS Lambda createFunction method based on the given configuration and file.
     * @param config configuration to setup the createFunction call
     * @param functionCode FunctionCode containing either zipfile or s3 location.
     * @throws IOException
     */
    private void createLambdaFunction(DeployConfig config, FunctionCode functionCode) throws IOException {

        CreateFunctionRequest createFunctionRequest = new CreateFunctionRequest()
                .withDescription(config.getDescription())
                .withFunctionName(config.getFunctionName())
                .withHandler(config.getHandler())
                .withMemorySize(config.getMemorySize())
                .withTimeout(config.getTimeout())
                .withRole(config.getRole())
                .withRuntime(config.getRuntime())
                .withCode(functionCode);
        logger.log("Lambda create function request:%n%s%n", createFunctionRequest.toString());

        CreateFunctionResult uploadFunctionResult = client.createFunction(createFunctionRequest);
        logger.log("Lambda create response:%n%s%n", uploadFunctionResult.toString());
    }

    /**
     * This method calls the AWS Lambda updateFunctionCode method based for the given file.
     * @param functionName name of the function to update code for
     * @param functionCode FunctionCode containing either zipfile or s3 location.
     * @throws IOException
     */
    private void updateCodeOnly(String functionName, FunctionCode functionCode) throws IOException {

        UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest()
                .withFunctionName(functionName)
                .withZipFile(functionCode.getZipFile())
                .withS3Bucket(functionCode.getS3Bucket())
                .withS3Key(functionCode.getS3Key())
                .withS3ObjectVersion(functionCode.getS3ObjectVersion());

        logger.log("Lambda update code request:%n%s%n", updateFunctionCodeRequest.toString());

        UpdateFunctionCodeResult updateFunctionCodeResult = client.updateFunctionCode(updateFunctionCodeRequest);
        logger.log("Lambda update code response:%n%s%n", updateFunctionCodeResult.toString());
    }

    /**
     * This method calls the AWS Lambda updateFunctionConfiguration method based on the given config.
     * @param config new configuration for the function
     */
    private void updateConfigurationOnly(DeployConfig config){
        UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = new UpdateFunctionConfigurationRequest()
                .withFunctionName(config.getFunctionName())
                .withDescription(config.getDescription())
                .withHandler(config.getHandler())
                .withMemorySize(config.getMemorySize())
                .withTimeout(config.getTimeout())
                .withRole(config.getRole());
        logger.log("Lambda update configuration request:%n%s%n", updateFunctionConfigurationRequest.toString());

        UpdateFunctionConfigurationResult updateFunctionConfigurationResult = client.updateFunctionConfiguration(updateFunctionConfigurationRequest);
        logger.log("Lambda update configuration response:%n%s%n", updateFunctionConfigurationResult.toString());
    }

    /**
     * Checks whether the function already exists on the user's account using the getFunction request.
     * @param functionName name of the function to be checked
     * @return true if exists, false if not.
     */
    private Boolean functionExists(String functionName){
        GetFunctionRequest getFunctionRequest = new GetFunctionRequest()
                .withFunctionName(functionName);
        logger.log("Lambda function existence check:%n%s%n", getFunctionRequest.toString());
        try {
            GetFunctionResult functionResult = client.getFunction(getFunctionRequest);
            logger.log("Lambda function exists:%n%s%n", functionResult.toString());
            return true;
        } catch (ResourceNotFoundException rnfe) {
            logger.log("Lambda function does not exist.");
            return false;
        }
    }

    public FunctionCode getFunctionCode(String artifactLocation, WorkSpaceZipper workSpaceZipper){
        if(artifactLocation.startsWith("s3://")){
            String bucket = null;
            String key = null;
            String versionId = null;

            String s3String = artifactLocation.substring(5);
            int versionIndex = s3String.indexOf("?versionId=");
            if(versionIndex != -1){
                versionId = s3String.substring(versionIndex + 11);
                s3String = s3String.substring(0, versionIndex);
            }
            int separatorIndex = s3String.indexOf("/");
            if(separatorIndex != -1){
                bucket = s3String.substring(0, separatorIndex);
                if(s3String.length() > separatorIndex + 1) {
                    key = s3String.substring(separatorIndex + 1);
                }
            }

            return new FunctionCode()
                    .withS3Bucket(bucket)
                    .withS3Key(key)
                    .withS3ObjectVersion(versionId);

        } else {
            try {
                File zipFile = workSpaceZipper.getZip(artifactLocation);
                return new FunctionCode()
                        .withZipFile(getFunctionZip(zipFile));
            } catch (IOException ioe){
                throw new LambdaDeployException("Error processing zip file.", ioe);
            } catch (InterruptedException ie){
                throw new LambdaDeployException("Error processing zip file.", ie);
            }

        }
    }

    /**
     * Get ByteBuffer from zip file.
     * @param zipFile file to be wrapped.
     * @return ByteBuffer containing zip file.
     * @throws IOException
     */
    private ByteBuffer getFunctionZip(File zipFile) throws IOException {
        return ByteBuffer.wrap(FileUtils.readFileToByteArray(zipFile));
    }
}
