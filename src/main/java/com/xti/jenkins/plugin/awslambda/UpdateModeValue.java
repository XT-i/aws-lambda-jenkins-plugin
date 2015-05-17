package com.xti.jenkins.plugin.awslambda;

import org.apache.commons.lang.StringUtils;

public enum UpdateModeValue {
    Full("full", "Code and configuration"), Code("code", "Code"), Config("config", "Configuration");

    private String mode;
    private String displayName;

    UpdateModeValue(String mode, String display) {
        this.mode = mode;
        this.displayName = display;
    }

    public String getMode() {
        return mode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UpdateModeValue fromString(String mode){
        if(StringUtils.isEmpty(mode)){
            return Full;
        }

        if(Code.mode.equals(mode)){
            return Code;
        }

        if(Config.mode.equals(mode)){
            return Config;
        }

        return Full;
    }
}
