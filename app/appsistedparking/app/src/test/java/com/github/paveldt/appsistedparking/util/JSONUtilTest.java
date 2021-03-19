package com.github.paveldt.appsistedparking.util;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JSONUtilTest {

    /**
     * Tests converting json array from string to list of Strings.
     */
    @Test
    public void testJsonToStringList() {
        String testJson = "['Cottrell', 'South', 'Pathfoot']";
        List<String> testList = JSONUtil.jsonToStringList(testJson);
        List<String> expectedList = Arrays.asList(new String[]{"Cottrell", "South", "Pathfoot"});

        assertEquals(expectedList, testList);
    }

    /**
     * Tests converting a json string to a json object
     */
    @Test
    public void testJsonStrToObject() throws Exception {
        String testJson = "{'location':'stirling', 'Site':'Cottrell', 'available': 30}";
        JSONObject testObject = JSONUtil.jsonStrToObject(testJson);
        JSONObject expected = new JSONObject(testJson);

        // have to stringify as the actuall objects in memory
        // are not the same exact object thus the evaluation fails
        // however the string format matching is enough.
        assertEquals(expected.toString(), testObject.toString());
    }
}
