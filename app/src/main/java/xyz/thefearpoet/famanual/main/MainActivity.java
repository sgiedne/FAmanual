package xyz.thefearpoet.famanual.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import xyz.thefearpoet.famanual.R;
import xyz.thefearpoet.famanual.speechrec.SpeechActivity;

public class MainActivity extends AppCompatActivity {

    Button start;
    TextView intro;
    TextToSpeech speechText;
    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button)findViewById(R.id.btnStart);
        intro = (TextView)findViewById(R.id.txtIntro);
        intro.setText(getString(R.string.main_greeting));


        speechText=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speechText.setLanguage(Locale.US);
                    speechText.speak(
                            getString(R.string.main_greeting),
                            TextToSpeech.QUEUE_FLUSH,
                            null);
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuestion = (result.get(0));
                    String response = "I can help you with that. First, do this..";
                    String emergency = "Thermal Burn";
                    Intent intent =  new Intent(this, SpeechActivity.class);
                    intent.putExtra("QUERY", userQuestion);

                    startActivity(intent);
                    finish();

                    // go to speech rec activity
                }
                break;
            }

        }
    }
}
