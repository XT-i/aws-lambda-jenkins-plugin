package com.xti.jenkins.plugin.awslambda.publish;

import java.io.Serializable;

/**
 * Created by sulland on 27/07/16.
 */
public class LambdaPublishServiceResponse implements Serializable {
    private String functionVersion;
    private String functionAlias;
    private boolean success;

    public LambdaPublishServiceResponse(String functionVersion, String functionAlias, boolean success){
        this.functionAlias = functionAlias;
        this.functionVersion = functionVersion;
        this.success = success;
    }

    public String getFunctionVersion(){
        return this.functionVersion;
    }

    public String getFunctionAlias(){
        return this.functionAlias;
    }

    public boolean getSuccess(){
        return this.success;
    }
}
