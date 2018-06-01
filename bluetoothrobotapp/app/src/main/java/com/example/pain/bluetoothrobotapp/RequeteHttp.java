/**
 * @author theo paris et mathias martinez
 *
 * classe permettant d'envoyer des requetes http
 *
 *
 */
package com.example.pain.bluetoothrobotapp;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequeteHttp extends AsyncTask<String,Void,String>{


    private String data="";

    public void HttpRequest() {

    }

    @Override
    protected String doInBackground(String... strings){
        this.data = strings[0];
        return send();

    }

   /**@Override
    protected void onPostExecute(String results){
        this.data = results;

    }**/

    /**
     * envoie la requette
     *
     *
     *
     * @return
     */

    public String send() {
        HttpURLConnection urlConnection = null;
        String webcontent = null;
        try {
            URL url = new URL(this.data);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            webcontent = generateString(in);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return webcontent;
    }

    private String generateString(InputStream stream){
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader buffer = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        try{
            String cur;
            while((cur=buffer.readLine())!=null){
                sb.append(cur+System.getProperty("line.separator"));
            }
            stream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

}
