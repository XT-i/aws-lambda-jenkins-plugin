package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
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
     * @param zipFile zipfile containing code to be updated or added
     * @param updateModeValue Full, Code or Config, only used if function does not already exists.
     * @return true if successful, false in case of failure.
     */
    public Boolean deployLambda(DeployConfig config, File zipFile, UpdateModeValue updateModeValue){
        if(functionExists(config.getFunctionName())){

            //update code
            if(UpdateModeValue.Full.equals(updateModeValue) || UpdateModeValue.Code.equals(updateModeValue)){
                if(zipFile != null) {
                    try {
                        updateCodeOnly(config.getFunctionName(), zipFile);
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
            if(zipFile != null) {
                try {
                    createLambdaFunction(config, zipFile);
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
     * @param zipFile zipfile that will be uploaded
     * @throws IOException
     */
    private void createLambdaFunction(DeployConfig config, File zipFile) throws IOException {

        FunctionCode functionCode = new FunctionCode().withZipFile(getFunctionZip(zipFile));

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
     * @param zipFile zipfile that will be uploaded
     * @throws IOException
     */
    private void updateCodeOnly(String functionName, File zipFile) throws IOException {
        ByteBuffer functionCode = getFunctionZip(zipFile);

        UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest()
                .withFunctionName(functionName)
                .withZipFile(functionCode);

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
