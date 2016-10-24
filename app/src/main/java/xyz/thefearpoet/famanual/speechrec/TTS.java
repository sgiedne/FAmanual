package xyz.thefearpoet.famanual.speechrec;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TTS extends Activity implements
        TextToSpeech.OnInitListener {

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    //public Button speakButton;
    // TTS object
    public TextToSpeech myTTS;
    // status check code
    public int MY_DATA_CHECK_CODE = 0;

    // setup TTS
    public void onInit(int initStatus) {

        // check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle voiceinput) {
        super.onCreate(voiceinput);

        // Inflate our UI from its XML layout description.
        // setContentView(R.layout.voice_recognition);

        // check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }

    // speak the user text
    public void speakWords(String speech) {

        // speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }



    /**
     * Handle the results from the recognition activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                // no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent
                        .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }
}