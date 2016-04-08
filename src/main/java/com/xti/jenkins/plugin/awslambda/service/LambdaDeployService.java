package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.xti.jenkins.plugin.awslambda.eventsource.EventSourceConfig;
import com.xti.jenkins.plugin.awslambda.exception.LambdaDeployException;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.upload.UpdateModeValue;
import com.xti.jenkins.plugin.awslambda.util.LogUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

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

            //update configuration
            if(UpdateModeValue.Full.equals(updateModeValue) || UpdateModeValue.Config.equals(updateModeValue)){
                try {
                    updateConfigurationOnly(config);
                } catch (AmazonClientException ace){
                    logger.log(LogUtils.getStackTrace(ace));
                    return false;
                }
            }

            //update code
            if(UpdateModeValue.Full.equals(updateModeValue) || UpdateModeValue.Code.equals(updateModeValue)){
                if(functionCode != null) {
                    try {
                        String version = updateCodeOnly(config.getFunctionName(), functionCode, config.getPublish());
                        if(config.getPublish() && config.getCreateAlias()){
                            if(aliasExists(config.getAlias(), config.getFunctionName())) {
                                updateLambdaAlias(config, version);
                            } else {
                                createLambdaAliasFunction(config, version);
                            }
                        }
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

            return true;

        } else {
            if(functionCode != null) {
                try {
                    String functionVersion = createLambdaFunction(config, functionCode);
                    if(config.getPublish() && config.getCreateAlias()) {
                        createLambdaAliasFunction(config, functionVersion);
                    }
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
     *
     * @param config configuration to setup the createEventSourceMapping call
     * @return true if successful, false if not
     */
    public Boolean deployEventSource(EventSourceConfig config) {
        try {
            String functionArn = getFunctionArn(config.getFunctionName(), config.getFunctionAlias());
            createEventSourceMapping(config, functionArn);
            return true;
        }  catch(Exception e) {
            logger.log(LogUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * Locates the function ARN
     * @param functionName The name of the function to look up
     * @return The fully qualified ARN of the function
     */
    public String getFunctionArn(String functionName, String functionAlias) {
        GetFunctionRequest getFunctionRequest = new GetFunctionRequest()
                .withFunctionName(functionName);
        if (StringUtils.isNotEmpty(functionAlias)){
            getFunctionRequest = getFunctionRequest.withQualifier(functionAlias);
        }
        try {
            GetFunctionResult functionResult = client.getFunction(getFunctionRequest);
            logger.log("Lambda function exists:%n%s%n", functionResult.toString());
            return functionResult.getConfiguration().getFunctionArn();
        } catch(ResourceNotFoundException rnfe) {
            logger.log("Lambda function does not exist");
            throw new RuntimeException("Lambda function does not exist", rnfe);
        }
    }


    /**
     * This method calls the AWS Lambda createFunction method based on the given configuration and file.
     * @param config configuration to setup the createFunction call
     * @param functionCode FunctionCode containing either zipfile or s3 location.
     * @return Returns the version of the published function
     * @throws IOException
     */
    private String createLambdaFunction(DeployConfig config, FunctionCode functionCode) throws IOException {

        CreateFunctionRequest createFunctionRequest = new CreateFunctionRequest()
                .withDescription(config.getDescription())
                .withFunctionName(config.getFunctionName())
                .withHandler(config.getHandler())
                .withMemorySize(config.getMemorySize())
                .withPublish(config.getPublish())
                .withTimeout(config.getTimeout())
                .withRole(config.getRole())
                .withRuntime(config.getRuntime())
                .withCode(functionCode);

        if(config.getSubnets().size() > 0 && config.getSecurityGroups().size() > 0){
            VpcConfig vpcConfig = new VpcConfig()
                    .withSubnetIds(config.getSubnets())
                    .withSecurityGroupIds(config.getSecurityGroups());
            createFunctionRequest.withVpcConfig(vpcConfig);
        }

        logger.log("Lambda create function request:%n%s%n", createFunctionRequest.toString());

        CreateFunctionResult uploadFunctionResult = client.createFunction(createFunctionRequest);
        logger.log("Lambda create response:%n%s%n", uploadFunctionResult.toString());

        return uploadFunctionResult.getVersion();
    }

    private void createLambdaAliasFunction(DeployConfig config, String functionVersion) throws IOException {
        CreateAliasRequest createAliasRequest = new CreateAliasRequest()
                .withFunctionName(config.getFunctionName())
                .withName(config.getAlias())
                .withFunctionVersion(functionVersion);

        logger.log("Lambda create alias request:%n%s%n", createAliasRequest.toString());
        CreateAliasResult createAliasResponse = client.createAlias(createAliasRequest);
        logger.log("Lambda create alias response:%n%s%n", createAliasResponse.toString());
    }

    private void updateLambdaAlias(DeployConfig config, String functionVersion) throws IOException {
        UpdateAliasRequest updateAliasRequest = new UpdateAliasRequest()
                .withFunctionName(config.getFunctionName())
                .withName(config.getAlias())
                .withFunctionVersion(functionVersion);

        logger.log("Lambda update alias request:%n%s%n", updateAliasRequest.toString());
        UpdateAliasResult updateAliasResult = client.updateAlias(updateAliasRequest);
        logger.log("Lambda update alias result:%n%s%n", updateAliasResult.toString());
    }

    public void createEventSourceMapping(EventSourceConfig config, String functionArn) {
        String functionName = StringUtils.isNotEmpty(config.getFunctionAlias()) ? functionArn + ":" + config.getFunctionAlias() : config.getFunctionName();
        CreateEventSourceMappingRequest eventSourceMappingRequest = new CreateEventSourceMappingRequest()
                .withEventSourceArn(config.getEventSourceArn())
                .withFunctionName(functionName)
                .withStartingPosition(config.getStartingPosition())
                .withEnabled(true);


        // check for mapping first
        ListEventSourceMappingsRequest listMappingsRequest = new ListEventSourceMappingsRequest()
                .withEventSourceArn(eventSourceMappingRequest.getEventSourceArn())
                .withFunctionName(eventSourceMappingRequest.getFunctionName());

        ListEventSourceMappingsResult listMappingsResult = client.listEventSourceMappings(listMappingsRequest);
        if (listMappingsResult.getEventSourceMappings().isEmpty()) {
            logger.log("EventSource mapping request:%n%s%n", eventSourceMappingRequest.toString());
            CreateEventSourceMappingResult eventSourceMappingResult = client.createEventSourceMapping(eventSourceMappingRequest);
            logger.log("EventSource mapping response:%n%s%n", eventSourceMappingResult.toString());
        } else {
            logger.log("Skipping EventSource mapping (already exists): " + config.getEventSourceArn());
        }
    }

    /**
     * This method includes the Publish flag when there is a need to create a new version of the service
     *
     * @param functionName name of the function to update code for
     * @param functionCode FunctionCode containing either zipfile or s3 location.
     * @param publish True to publish a new version of the function
     * @return the new version of the function
     * @throws IOException
     */
    private String updateCodeOnly(String functionName, FunctionCode functionCode, Boolean publish) throws IOException  {
        UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest()
                .withFunctionName(functionName)
                .withZipFile(functionCode.getZipFile())
                .withS3Bucket(functionCode.getS3Bucket())
                .withS3Key(functionCode.getS3Key())
                .withPublish(publish)
                .withS3ObjectVersion(functionCode.getS3ObjectVersion());

        logger.log("Lambda update code request:%n%s%n", updateFunctionCodeRequest.toString());

        UpdateFunctionCodeResult updateFunctionCodeResult = client.updateFunctionCode(updateFunctionCodeRequest);
        logger.log("Lambda update code response:%n%s%n", updateFunctionCodeResult.toString());
        return updateFunctionCodeResult.getVersion();
    }

    /**
     * This method calls the AWS Lambda updateFunctionConfiguration method based on the given config.
     * @param config new configuration for the function
     * @return The new version of the function
     */
    private String updateConfigurationOnly(DeployConfig config){
        UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = new UpdateFunctionConfigurationRequest()
                .withFunctionName(config.getFunctionName())
                .withDescription(config.getDescription())
                .withHandler(config.getHandler())
                .withMemorySize(config.getMemorySize())
                .withTimeout(config.getTimeout())
                .withRuntime(config.getRuntime())
                .withRole(config.getRole());

        if(config.getSubnets().size() > 0 && config.getSecurityGroups().size() > 0){
            VpcConfig vpcConfig = new VpcConfig()
                    .withSubnetIds(config.getSubnets())
                    .withSecurityGroupIds(config.getSecurityGroups());
            updateFunctionConfigurationRequest.withVpcConfig(vpcConfig);
        }

        logger.log("Lambda update configuration request:%n%s%n", updateFunctionConfigurationRequest.toString());

        UpdateFunctionConfigurationResult updateFunctionConfigurationResult = client.updateFunctionConfiguration(updateFunctionConfigurationRequest);
        logger.log("Lambda update configuration response:%n%s%n", updateFunctionConfigurationResult.toString());
        return updateFunctionConfigurationResult.getVersion();
    }

    /**
     * Checks whether the function already exists on the user's account using the getFunction request.
     * @param functionName name of the function to be checked
     * @return true if exists, false if not.
     */
    private Boolean functionExists(String functionName){
        return functionExists(functionName, null);
    }

    /**
     * Checks whether the function with the given alias exists on the user's account using the getFunction request.
     * @param functionName name of the function to be checked
     * @param functionAlias name of the alias to match to the query
     * @return true if exists, false if not
     */
    private Boolean functionExists(String functionName, String functionAlias) {
        GetFunctionRequest getFunctionRequest = new GetFunctionRequest()
                .withFunctionName(functionName);

        if(functionAlias != null) {
            getFunctionRequest.withQualifier(functionAlias);
            logger.log("Lambda function existence with alias request:%n%s%n", getFunctionRequest.toString());
        } else {
            logger.log("Lambda function existence check:%n%s%n", getFunctionRequest.toString());
        }

        try {
            GetFunctionResult functionResult = client.getFunction(getFunctionRequest);
            logger.log("Lambda function exists:%n%s%n", functionResult.toString());
            return true;
        } catch(ResourceNotFoundException rnfe) {
            logger.log("Lambda function does not exist");
            return false;
        }
    }

    /**
     * Checks whether the function alias already exists on the users' account using the getAlias request
     * @param alias name of the alias to be checked
     * @param functionName name of the function the alias should be attached to
     * @return true if it exists, false if not.
     */
    private Boolean aliasExists(String alias, String functionName) {
        GetAliasRequest getAliasRequest = new GetAliasRequest()
                .withName(alias)
                .withFunctionName(functionName);
        logger.log("Lambda function alias existence check:%n%s%n%s%n", functionName, alias);
        try {
            GetAliasResult functionResult = client.getAlias(getAliasRequest);
            logger.log("Lambda function alias exists:%n%s%n", functionResult.toString());
            return true;
        } catch (ResourceNotFoundException rnfe) {
            logger.log("Lambda alias does not exist for function.");
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
            } catch (IOException | InterruptedException ioe){
                throw new LambdaDeployException("Error processing zip file.", ioe);
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