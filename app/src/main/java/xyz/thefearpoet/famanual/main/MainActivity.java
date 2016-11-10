package xyz.thefearpoet.famanual.main;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import xyz.thefearpoet.famanual.R;

public class MainActivity extends AppCompatActivity {

    TextView intro;
    TextToSpeech speechText;
    String introText = "How can I help you?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intro = (TextView)findViewById(R.id.txtIntro);
        intro.setText(introText);


        speechText=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speechText.setLanguage(Locale.US);
                    speechText.speak(introText, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }
}
