package com.wordwiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.wordwiz.SettingsActivity.DAILY;
import static com.wordwiz.SettingsActivity.SHARED_PREFERENCES;
import static com.wordwiz.WordWizard.initializeDates;
import static com.wordwiz.WordWizard.now;
import static com.wordwiz.WordWizard.then;

public class MainActivity extends AppCompatActivity {

    public static final String STREAK = "streak";
    TextView streak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDates();

        if(then.getDay()+1==now.getDay()){
            Toast.makeText(getApplicationContext(),R.string.prolong, Toast.LENGTH_SHORT).show();
        }
        else if(then.getDay()+1<now.getDay()){
            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
            editor.putInt(STREAK,0);
            editor.apply();
            Toast.makeText(getApplicationContext(),R.string.inactivity, Toast.LENGTH_SHORT).show();
        }


        streak = findViewById(R.id.TVcurrentStreak);
    }

    public void onResume() {
        super.onResume();
        if(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(DAILY,false)) {
            String a=getResources().getString(R.string.streakEquals1,
                    getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).getInt(STREAK, 0));
            streak.setText(a);
        }
        else streak.setText(R.string.toSeeStreak);
    }

    public void clickedTestYourself(View view){
        Intent intent = new Intent(this, TestYourself.class);
        startActivity(intent);
    }

    public void clickedAddVocab(View view){
        Intent intent = new Intent(this, AddVocabulary.class);
        startActivity(intent);
    }

    public void clickedSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void clickedStats(View view){
        Toast.makeText(MainActivity.this,R.string.stayTuned,Toast.LENGTH_SHORT).show();
       // Intent intent = new Intent(this, StatisticsActivity.class);
       // startActivity(intent);
    }

    public void clickedExit(View view){
        finish();
    }
}