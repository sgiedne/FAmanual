package xyz.thefearpoet.famanual.speechrec;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

import xyz.thefearpoet.famanual.R;

public class SpeechActivity extends Activity implements TextToSpeech.OnInitListener{
    private static final int REQUEST_CODE = 1234;
    TextToSpeech t1;
    Button Start;
    TextView Speech;
    String words;
    TextView Result;
    String query;
    ArrayList<String> matches_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        Start = (Button)findViewById(R.id.start_reg);
        Speech = (TextView)findViewById(R.id.speech);
        Result = (TextView)findViewById(R.id.tvResult);

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    try {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
                        startActivityForResult(intent, REQUEST_CODE);
                    } catch(ActivityNotFoundException e) {
                        String appPackageName = "com.google.android.googlequicksearchbox";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
                }
          }

      });
    }

    public  boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
           query = matches_text.get(0);
           Speech.setText("QUERY : " + query);

            class MakePostRequest extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... urls){
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://www.famanual.com/manual_app/ask/");

                    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                    nameValuePair.add(new BasicNameValuePair("query", query));

                    //Encoding POST data
                    try {
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    try {
                        // write response to log
                        // this.response = httpClient.execute(httpPost);
                        // HttpEntity entity = response.getEntity();

                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String response = httpClient.execute(httpPost, responseHandler);

                        String result = new String(response);

                        Log.d("Http Post Response-->>",  response);
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
                protected void onPostExecute(String result) {
                    Result.setText(result);
                    //console.log(result);
                   // String words = Result.getText().toString();
                }

            }
            MakePostRequest task = new MakePostRequest();
            task.execute();

            super.onActivityResult(requestCode, resultCode, data);
        }

        words = Result.getText().toString();
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    t1.speak(Result.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });



    }

    @Override
    public void onInit(int i) {

    }
}