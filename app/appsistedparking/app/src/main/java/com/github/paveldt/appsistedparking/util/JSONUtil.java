package com.github.paveldt.appsistedparking.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONUtil {

    /**
     * Converts json array in string format to list of strings
     * @param json - json array data
     * @return - list of strings representing each object in the array
     */
    public static List<String> jsonToStringList(String json) {
        try {
            JSONArray data = new JSONArray(json);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                list.add(data.getString(i));
            }

            return list;
        } catch (JSONException ex) {
            Log.e("JSON PARSE ERROR", ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Converts json string to a json object
     * @param json - string json
     * @return - json object
     */
    public static JSONObject jsonStrToObject(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj;
        } catch (JSONException ex) {
            Log.e("JSON OBJ PARSE ERROR", ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }
}
