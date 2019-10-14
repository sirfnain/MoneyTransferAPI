package com.revolut.interview.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import spark.utils.IOUtils;

import static org.junit.Assert.fail;

public class CommonUtil {

    private static final String BASE_URL = "http://localhost:4567/";

    public static Response request(final String method, final String path) {
        return request(method, path, null);
    }
    
    public static Response request(final String method, final String path, final String jsonBody) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL( BASE_URL + path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            if(jsonBody != null){
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json");
                byte[] outputInBytes = jsonBody.getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write( outputInBytes );
                os.close();
            } else{
                conn.setDoInput(true);
            }
            conn.connect();
            if(conn.getResponseCode() == 200){
                String body = IOUtils.toString(conn.getInputStream());
                return new Response(conn.getResponseCode(), body);
            } else {
                return new Response(conn.getResponseCode());
            }
        } catch (IOException ex) {
            fail("Sending request failed: " + ex.getMessage());
            return null;
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }
    }
}
