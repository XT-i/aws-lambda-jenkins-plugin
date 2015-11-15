package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.xti.jenkins.plugin.awslambda.exception.LambdaDeployException;
import com.xti.jenkins.plugin.awslambda.upload.AliasConfig;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.upload.PublishConfig;
import com.xti.jenkins.plugin.awslambda.upload.UpdateModeValue;
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
    public DeployResult deployLambda(DeployConfig config, FunctionCode functionCode, UpdateModeValue updateModeValue){
        if(functionExists(config.getFunctionName())){

            DeployResult lastDeployResult = null;
            //update code
            if(UpdateModeValue.Full.equals(updateModeValue) || UpdateModeValue.Code.equals(updateModeValue)){
                if(functionCode != null) {
                    lastDeployResult = updateCodeOnly(config.getFunctionName(), functionCode);
                }else {
                    logger.log("Could not find file to upload.");
                    return new DeployResult(false, config.getFunctionName(), null);
                }
            }

            //update configuration
            if(UpdateModeValue.Full.equals(updateModeValue) || UpdateModeValue.Config.equals(updateModeValue)){
                lastDeployResult = updateConfigurationOnly(config);
            }

            return lastDeployResult;

        }else {
            if(functionCode != null) {
                return createLambdaFunction(config, functionCode);
            } else {
                logger.log("Could not find file to upload.");
                return new DeployResult(false, config.getFunctionName(), null);
            }
        }
    }

    /**
     * This method calls the AWS Lambda createFunction method based on the given configuration and file.
     * @param config configuration to setup the createFunction call
     * @param functionCode FunctionCode containing either zipfile or s3 location.
     * @throws IOException
     */
    private DeployResult createLambdaFunction(DeployConfig config, FunctionCode functionCode) {

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

        try {
            CreateFunctionResult uploadFunctionResult = client.createFunction(createFunctionRequest);
            logger.log("Lambda create response:%n%s%n", uploadFunctionResult.toString());
            return new DeployResult(true, uploadFunctionResult.getFunctionName(), uploadFunctionResult.getVersion());
        } catch (AmazonClientException ace){
            logger.log(LogUtils.getStackTrace(ace));
            return new DeployResult(false, config.getFunctionName(), null);
        }
    }

    /**
     * This method calls the AWS Lambda updateFunctionCode method based for the given file.
     * @param functionName name of the function to update code for
     * @param functionCode FunctionCode containing either zipfile or s3 location.
     */
    private DeployResult updateCodeOnly(String functionName, FunctionCode functionCode)  {

        UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest()
                .withFunctionName(functionName)
                .withZipFile(functionCode.getZipFile())
                .withS3Bucket(functionCode.getS3Bucket())
                .withS3Key(functionCode.getS3Key())
                .withS3ObjectVersion(functionCode.getS3ObjectVersion());

        logger.log("Lambda update code request:%n%s%n", updateFunctionCodeRequest.toString());

        try{
            UpdateFunctionCodeResult updateFunctionCodeResult = client.updateFunctionCode(updateFunctionCodeRequest);
            logger.log("Lambda update code response:%n%s%n", updateFunctionCodeResult.toString());
            return new DeployResult(true, updateFunctionCodeResult.getFunctionName(), updateFunctionCodeResult.getVersion());
        } catch (AmazonClientException ace){
            logger.log(LogUtils.getStackTrace(ace));
            return new DeployResult(false, functionName, null);
        }
    }

    /**
     * This method calls the AWS Lambda updateFunctionConfiguration method based on the given config.
     * @param config new configuration for the function
     */
    private DeployResult updateConfigurationOnly(DeployConfig config){
        UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = new UpdateFunctionConfigurationRequest()
                .withFunctionName(config.getFunctionName())
                .withDescription(config.getDescription())
                .withHandler(config.getHandler())
                .withMemorySize(config.getMemorySize())
                .withTimeout(config.getTimeout())
                .withRole(config.getRole());
        logger.log("Lambda update configuration request:%n%s%n", updateFunctionConfigurationRequest.toString());

        try {
            UpdateFunctionConfigurationResult updateFunctionConfigurationResult = client.updateFunctionConfiguration(updateFunctionConfigurationRequest);
            logger.log("Lambda update configuration response:%n%s%n", updateFunctionConfigurationResult.toString());
            return new DeployResult(true, updateFunctionConfigurationResult.getFunctionName(), updateFunctionConfigurationResult.getVersion());
        } catch (AmazonClientException ace){
            logger.log(LogUtils.getStackTrace(ace));
            return new DeployResult(false, config.getFunctionName(), null);
        }
    }

    public AliasResult createAlias(AliasConfig aliasConfig){
        GetAliasRequest getAliasRequest = new GetAliasRequest()
                .withFunctionName(aliasConfig.getFunctionName())
                .withName(aliasConfig.getAliasName());
        try {
            client.getAlias(getAliasRequest);
            UpdateAliasRequest updateAliasRequest = new UpdateAliasRequest()
                    .withName(aliasConfig.getAliasName())
                    .withDescription(aliasConfig.getAliasDescription())
                    .withFunctionName(aliasConfig.getFunctionName())
                    .withFunctionVersion(aliasConfig.getFunctionVersion());
            try {
                UpdateAliasResult updateAliasResult = client.updateAlias(updateAliasRequest);
                logger.log("Lambda update alias response:%n%s%n", updateAliasResult.toString());
                return new AliasResult(true, aliasConfig.getFunctionName(), aliasConfig.getFunctionVersion(), updateAliasResult.getName());
            } catch (AmazonClientException ace){
                logger.log(LogUtils.getStackTrace(ace));
                return new AliasResult(false, aliasConfig.getFunctionName(), aliasConfig.getFunctionVersion(), aliasConfig.getAliasName());
            }

        } catch (ResourceNotFoundException e){
            CreateAliasRequest createAliasRequest = new CreateAliasRequest()
                    .withName(aliasConfig.getAliasName())
                    .withDescription(aliasConfig.getAliasDescription())
                    .withFunctionName(aliasConfig.getFunctionName())
                    .withFunctionVersion(aliasConfig.getFunctionVersion());

            try {
                CreateAliasResult createAliasResult = client.createAlias(createAliasRequest);
                logger.log("Lambda create alias response:%n%s%n", createAliasResult.toString());
                return new AliasResult(true, aliasConfig.getFunctionName(), aliasConfig.getFunctionVersion(), createAliasResult.getName());
            } catch (AmazonClientException ace){
                logger.log(LogUtils.getStackTrace(ace));
                return new AliasResult(false, aliasConfig.getFunctionName(), aliasConfig.getFunctionVersion(), aliasConfig.getAliasName());
            }
        }


    }

    public PublishResult publishVersion(PublishConfig publishConfig) {
        PublishVersionRequest publishVersionRequest = new PublishVersionRequest()
                .withDescription(publishConfig.getPublishDescription())
                .withFunctionName(publishConfig.getFunctionName());

        try {
            PublishVersionResult publishVersionResult = client.publishVersion(publishVersionRequest);
            logger.log("Lambda publish version response:%n%s%n", publishVersionResult.toString());
            return new PublishResult(true, publishVersionResult.getFunctionName(), publishVersionResult.getVersion());
        } catch (AmazonClientException ace){
            logger.log(LogUtils.getStackTrace(ace));
            return new PublishResult(false, publishConfig.getFunctionName(), null);
        }
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
