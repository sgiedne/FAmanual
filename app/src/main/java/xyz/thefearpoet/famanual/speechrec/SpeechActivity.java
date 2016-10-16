package xyz.thefearpoet.famanual.speechrec;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import xyz.thefearpoet.famanual.R;

public class SpeechActivity extends Activity {
    private static final int REQUEST_CODE = 1234;
    Button Start;
    TextView Speech;
    Dialog match_text_dialog;
    ListView textlist;
    ArrayList<String> matches_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        Start = (Button)findViewById(R.id.start_reg);
        Speech = (TextView)findViewById(R.id.speech);

        Start.setOnClickListener(new OnClickListener() {
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
                }}

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

            match_text_dialog = new Dialog(SpeechActivity.this);
            match_text_dialog.setContentView(R.layout.activity_speech_dialoguematches);
            match_text_dialog.setTitle("Select Matching Text");
            textlist = (ListView)match_text_dialog.findViewById(R.id.list);
            matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter =    new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matches_text);
            textlist.setAdapter(adapter);
            textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Speech.setText("You have said " +matches_text.get(position));
                    match_text_dialog.hide();
                }
            });
            match_text_dialog.show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}