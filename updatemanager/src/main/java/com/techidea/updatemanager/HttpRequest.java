package com.techidea.updatemanager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zchao on 2016/6/22.
 */
public class HttpRequest {


    public HttpRequest() {
    }

    /**
     *
     * @param url
     * @param param  deviceId=123123&deviceName=HANDPOS
     * @return
     */
    public UpdateInfo postUpdate(String url, String param) {
        HttpURLConnection connection = null;
        UpdateInfo updateInfo = null;
        try {
            URL postUrl = new URL(url);
            String para = new String(param);
            connection = (HttpURLConnection) postUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("contentType", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(para.getBytes().length));
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.getOutputStream().write(para.getBytes());
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                String result = StreamToString(is);
                Gson gson = new Gson();
                updateInfo = gson.fromJson(result, UpdateInfo.class);
            }
        } catch (Exception e) {

        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return updateInfo;
    }

    public String StreamToString(InputStream is) {
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
