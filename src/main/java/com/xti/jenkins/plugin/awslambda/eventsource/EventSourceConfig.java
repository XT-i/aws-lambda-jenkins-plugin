package com.xti.jenkins.plugin.awslambda.eventsource;

/**
 * Created by anthonyikeda on 25/11/2015.
 *
 */
public class EventSourceConfig {

    private String functionName;
    private String functionAlias;
    private String eventSourceArn;
    private String startingPosition;

    public EventSourceConfig(String functionName, String functionAlias, String eventSourceArn) {
        this.functionName = functionName;
        this.functionAlias = functionAlias;
        this.eventSourceArn = eventSourceArn;
        this.startingPosition = "LATEST";
    }

    public String getEventSourceArn() {
        return eventSourceArn;
    }

    public void setEventSourceArn(String eventSourceArn) {
        this.eventSourceArn = eventSourceArn;
    }

    public String getFunctionAlias() {
        return functionAlias;
    }

    public void setFunctionAlias(String alias) {
        this.functionAlias = alias;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(String startingPosition) {
        this.startingPosition = startingPosition;
    }

}
