package com.example.cis_carpool;

import android.os.AsyncTask;

import com.example.cis_carpool.abstraction.ParameterizedRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class helps with downloading data off of a URL.
 * @author joshuachasnov
 * @version 0.1
 */
public class DownloadRunnable implements Runnable {
    private final String urlLocation;
    private final ParameterizedRunnable onCompleted;

    public DownloadRunnable(String url, ParameterizedRunnable onCompleted) {
        this.urlLocation = url;
        this.onCompleted = onCompleted;
    }

    @Override
    public void run() {
        String data = downloadURL();
        onCompleted.run(data);
    }

    /**
     * Downloads the data from a URL.
     * @return the downloaded data
     */
    private String downloadURL() {
        String data = "";
        InputStream stream = null;
        try {
            // connects to the url
            URL url = new URL(urlLocation);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                stream = connection.getInputStream();
            } else {
                stream = connection.getErrorStream();
            }

            // reads data
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
