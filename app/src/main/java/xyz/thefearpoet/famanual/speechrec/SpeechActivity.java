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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.thefearpoet.famanual.R;
import xyz.thefearpoet.famanual.searchgoogle.SearchGoogle;

public class SpeechActivity extends Activity implements TextToSpeech.OnInitListener{
    private static final int REQUEST_CODE = 1234;
    TextToSpeech t1, read;

    // start button
    Button start;

    String picres;

    // question/speech by the user
    TextView question;

    // welcome text
    TextView initial;

    // response to the question
    TextView answer;

    // title of the first-aid procedure
    TextView emergency;

    // image for definitions
    ImageView imageView;

    String words;
    String logTag;
    String query;
    static Boolean renderImage;
    ArrayList<String> matches_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        logTag = "First Aid Manual";
        renderImage = false;
        start = (Button)findViewById(R.id.btnStart);
        question = (TextView)findViewById(R.id.txtQuestion);
        answer = (TextView)findViewById(R.id.txtAnswer);
        emergency = (TextView)findViewById(R.id.txtEmergency);
        initial = (TextView)findViewById(R.id.txtInit);
        imageView = (ImageView) findViewById(R.id.imageView);

        answer.setMovementMethod(new ScrollingMovementMethod());

        initial.setText("How can I help you?");


        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    t1.speak("How can I help you", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    try {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
                        startActivityForResult(intent, REQUEST_CODE);

                    } catch(ActivityNotFoundException e) {
                        String appPackageName = "com.google.android.googlequicksearchbox";
                        try {
                            startActivity(
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                    "https://play.google.com/store/apps/details?id="
                                                            + appPackageName)));
                        }
                    }
                }
                else{
                    Toast.makeText(
                            getApplicationContext(),
                            "Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
          }

      });
        // on create, start listening automatically
//        start.performClick();
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

    // get the api endpoint based on the query
    private String getEndPoint(String query){

        //ask4
        Matcher m1 = Pattern.compile("^what is the place to.*").matcher(query);
        Matcher m2 = Pattern.compile("^what is the location to.*").matcher(query);
        Matcher m3 = Pattern.compile("^what is the area to.*").matcher(query);
        Matcher m4 = Pattern.compile("^what are the signs of.*").matcher(query);
        Matcher m5 = Pattern.compile("^what are the details for this step.*").matcher(query);

        //ask1
        Matcher m6 = Pattern.compile("^what is.*").matcher(query);
        Matcher m7 = Pattern.compile("^what are.*").matcher(query);

        //ask2
        Matcher m8 = Pattern.compile("^next$").matcher(query);
        Matcher m9 = Pattern.compile("^previous$").matcher(query);
        Matcher m11 = Pattern.compile("^what's next$").matcher(query);
        Matcher m12 = Pattern.compile("^what is next$").matcher(query);
        Matcher m13 = Pattern.compile("^what else$").matcher(query);
        Matcher m14 = Pattern.compile("^previous$").matcher(query);
        Matcher m15 = Pattern.compile("^what is the previous step$").matcher(query);
        Matcher m16 = Pattern.compile("^what is the next step$").matcher(query);
        Matcher m17 = Pattern.compile("^go back$").matcher(query);
        Matcher m18 = Pattern.compile("^what was the last step$").matcher(query);
        Matcher m19 = Pattern.compile("^what did you say previously$").matcher(query);
        Matcher m20 = Pattern.compile("^repeat$").matcher(query);
        Matcher m21 = Pattern.compile("^what$").matcher(query);
        Matcher m22 = Pattern.compile("^say again$").matcher(query);
        Matcher m23 = Pattern.compile("^come again$").matcher(query);
        Matcher m24 = Pattern.compile("^can you say that again$").matcher(query);
        Matcher m25 = Pattern.compile("^how do you treat.*").matcher(query);

        //ask5
        Matcher m10 = Pattern.compile("^can you repeat the warnings$").matcher(query);




        if(m1.find() || m2.find() || m3.find() || m4.find() || m5.find()){
            Log.d("test","INSIDE IF 1");
            return "http://Sample-env-2.pfnhucvzdb.us-west-2.elasticbeanstalk.com/manual_app/ask4/";
        } else if(m6.find() || m7.find()){
            Log.d("test","INSIDE IF 2");

           SearchGoogle searchGoogle = new SearchGoogle();
            String def = searchGoogle.searchFor(query);
            Log.d("test",def);
//            answer.setText(def);

            renderImage = true;
            return def;
        }else if(m8.find() || m9.find() || m11.find() || m12.find() || m13.find() || m14.find()
                || m15.find() || m16.find() || m17.find() || m18.find() || m19.find() || m20.find()
                || m21.find() || m22.find() || m23.find() || m24.find() || m25.find()){
            Log.d("test", "INSIDE IF 3");
            return "http://Sample-env-2.pfnhucvzdb.us-west-2.elasticbeanstalk.com/manual_app/ask2/";
        }else if(m10.find()){
            Log.d("test", "INSIDE IF 3");
            return "http://Sample-env-2.pfnhucvzdb.us-west-2.elasticbeanstalk.com/manual_app/ask5/";
        }else{
            Log.d("test", "INSIDE ELSE");
            return "http://Sample-env-2.pfnhucvzdb.us-west-2.elasticbeanstalk.com/manual_app/ask4/";
        }
    }

    public String bingSearch(final String q){
        Log.d("test",  "inside bing search");
        String res="";
        class MakePostRequest extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls){
                //picres = "";
                HttpClient httpClient = new DefaultHttpClient();
                String qtemp = q;
                qtemp = qtemp.replaceAll("\\s+","+");
                Log.d("test",qtemp);
                String query = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q=" + qtemp;
                String res;
                Log.d("test",query);

                HttpPost httpPost = new HttpPost(query);

                httpPost.setHeader("Content-Type", "multipart/form-data");
                httpPost.setHeader("Ocp-Apim-Subscription-Key", "0ddffac498bc45e59d932b20370381ab");

                //Log.d("test",q);
//                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
//                nameValuePair.add(new BasicNameValuePair("q", "what is a band aid"));

//                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                nameValuePairs.add(new BasicNameValuePair("q", "what is a band aid"));

                //Encoding POST data
//                try {
//                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//                }catch (UnsupportedEncodingException e){
//                    e.printStackTrace();
//                }
                try {
                    // write response to log
                    // this.response = httpClient.execute(httpPost);
                    // HttpEntity entity = response.getEntity();
                    Log.d("test","Before RH");
                    ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                        @Override
                        public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                            //JSONObject t = (JSONObject) httpResponse.ge;
                            String body = EntityUtils.toString(httpResponse.getEntity()).toString();
                            String ultimateResult="";
                            try{
                                JSONObject jsonBody = new JSONObject(body);
                                JSONArray value = jsonBody.optJSONArray("value");
                                JSONObject firstIndex = (JSONObject) value.get(0);
                                ultimateResult = firstIndex.optString("thumbnailUrl");
                                Log.d("test", firstIndex.optString("thumbnailUrl"));
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                            Log.d("test", body);
                            return ultimateResult;
                        }
                    };
//                    Log.d("test",responseHandler.toString());
                    String response = httpClient.execute(httpPost, responseHandler);
//                    Log.d("test",response);
//                    String result = new String(response);
//
                    Log.d("test2",  response);
                  //  picres = response;
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
                //Log.d("test2",result);
                //picres = result;
            }

        }
        MakePostRequest task = new MakePostRequest();
        try {
            res = task.execute().get();
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }
        return res;
        //super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // set the welcome text to null
        initial.setText("");

        // set the imageview to null
        imageView.setImageResource(android.R.color.transparent);

        String response;

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            query = matches_text.get(0);

            // render the query
            question.setText("\"" + query+ "\"");

            class MakePostRequest extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... urls){

                   // SearchGoogle searchGoogle = new SearchGoogle();
                   // String def = searchGoogle.searchFor("what is christmas");

                    //Log.d("test", def);

                    HttpClient httpClient = new DefaultHttpClient();


                    String endPoint = getEndPoint(query);
                    Log.d("test",endPoint);
                    if(renderImage)
                        return endPoint;
                    HttpPost httpPost = new HttpPost(endPoint);

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

                        Log.d("test",  response.toString());
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
                    if(!renderImage)
                        answer.setText(result);

                    //if the query is a "what is" question, then render an image
                    if(renderImage){

                        String imageUrl = bingSearch(query);
                        //picres = "";
                        Log.d("test1", imageUrl);

                        // set the image
                        Picasso.with(getApplicationContext())
                                .load(imageUrl)
                                .into(imageView);
                        answer.setText(result);
                        answer.setText(result);

//                        SearchGoogle searchGoogle = new SearchGoogle();
//                        String def = searchGoogle.searchFor(query);
//                        answer.setText(def);

                        renderImage = false;
                    }


                    t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                t1.setLanguage(Locale.US);
                                t1.speak(answer.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    });
                }

            }
            MakePostRequest task = new MakePostRequest();
            task.execute();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onInit(int i) {

    }
}