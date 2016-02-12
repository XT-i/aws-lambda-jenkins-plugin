package com.xti.jenkins.plugin.awslambda.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Project: aws-lambda
 * Created by Michael Willemse on 12/02/2016.
 */
public class Tokenizer {

    public static List<String> split(String input){
        if(input == null){
            return new ArrayList<>();
        }
        List<String> tokens = new ArrayList<>(Arrays.asList(input.replaceAll("^[,\\s]+", "").split("[,\\s]+")));

        Iterator<String> tokenIterator = tokens.iterator();
        while (tokenIterator.hasNext()){
            String token = tokenIterator.next();
            if(StringUtils.isBlank(token)){
                tokenIterator.remove();
            }
        }

        return tokens;
    }
}
