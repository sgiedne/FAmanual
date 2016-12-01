package xyz.thefearpoet.famanual.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haridu on 11/13/16.
 */

public class MakePostRequest extends AsyncTask<String, Void, String> {

    private String url, query, result;

    public void setUrl(String u){
        url = u;
    }

    public void setQuery(String q){
        query = q;
    }

    public String getResult(){
        return result;
    }

    @Override
    protected String doInBackground(String... urls){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        Log.d("HTTP response",  url);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("query", query));

        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        try {
            Log.d("TAG", "Inside Try");
            // write response to log
            // this.response = httpClient.execute(httpPost);
            // HttpEntity entity = response.getEntity();

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = httpClient.execute(httpPost, responseHandler);

            String result = new String(response);

            Log.d("HTTP response",  response);
            return response;

        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
        return "Sorry, I didn't understand that";
    }

    @Override
    protected void onPostExecute(String r) {
        result = r;
    }
}
