package com.xti.jenkins.plugin.awslambda.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Project: aws-lambda
 * Created by Michael Willemse on 12/02/2016.
 */
public class TokenizerTest {

    @Test
    public void testValidExpectedString(){
        String input = "subnet1, subnet2, subnet3";
        List<String> fixture = Arrays.asList("subnet1", "subnet2", "subnet3");

        assertEquals(fixture, Tokenizer.split(input));
    }

    @Test
    public void testValidMuchWhitespace(){
        String input = "subnet1, ,   subnet2,     subnet3,    ";
        List<String> fixture = Arrays.asList("subnet1", "subnet2", "subnet3");

        assertEquals(fixture, Tokenizer.split(input));
    }

    @Test
    public void testValidStartEndWhitespace(){
        String input = "       subnet1, subnet2, subnet3    ";
        List<String> fixture = Arrays.asList("subnet1", "subnet2", "subnet3");

        assertEquals(fixture, Tokenizer.split(input));
    }

    @Test
    public void testValidEmpty(){
        String input = "";

        assertEquals(0, Tokenizer.split(input).size());
    }

    @Test
    public void testValidNull(){
        assertEquals(0, Tokenizer.split(null).size());
    }
}
