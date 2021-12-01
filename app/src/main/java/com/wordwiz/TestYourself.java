package com.wordwiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;

import static com.wordwiz.SettingsActivity.SHARED_PREFERENCES;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT2;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT3;
import static com.wordwiz.MainActivity.STREAK;

public class TestYourself extends AppCompatActivity {

    Button translatedPhrases, pairUp, memory;
    Switch switchPhrases;
    RadioButton list1,list2,list3;
    public static final String WORD_ORDER = "word_order";
    public static final String CHECKED = "checked";
    public String list;
    public int checked;
    public static boolean language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_yourself);

        translatedPhrases =findViewById(R.id.buttonGuessTranslated);
        switchPhrases = findViewById(R.id.switchGuessing);
        memory = findViewById(R.id.buttonMemory);
        pairUp = findViewById(R.id.buttonPairUpPhrases);
        list1 = findViewById(R.id.radioButtonList1Test);
        list2 = findViewById(R.id.radioButtonList2Test);
        list3 = findViewById(R.id.radioButtonList3Test);

        load();

        translatedPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(checked){
                    case(1):
                        list=UNACQUIRED_COUNT;
                        break;
                    case(2):
                        list=UNACQUIRED_COUNT2;
                        break;
                    case(3):
                        list=UNACQUIRED_COUNT3;
                        break;
                }

                Log.v("unae","now: "+getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE)
                        .getInt(UNACQUIRED_COUNT,0));
                Log.v("unae","now: "+getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE)
                        .getInt(UNACQUIRED_COUNT2,0));
                Log.v("unae","now: "+getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE)
                        .getInt(UNACQUIRED_COUNT3,0));

                Log.v("unae",String.valueOf(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(list,0)));
                if(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(list,0)>0) {
                    SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
                    editor.putBoolean(WORD_ORDER,switchPhrases.isChecked());
                    editor.putInt(CHECKED,checked);
                    editor.apply();
                    Intent intent = new Intent(TestYourself.this, TranslatedPhrases.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(TestYourself.this, "There are no words to learn.", Toast.LENGTH_SHORT).show();
            }
        });

        pairUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(checked){
                    case(1):
                        list=UNACQUIRED_COUNT;
                        break;
                    case(2):
                        list=UNACQUIRED_COUNT2;
                        break;
                    case(3):
                        list=UNACQUIRED_COUNT3;
                        break;
                }
                if(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(list,0)>=5) {
                    Intent intent = new Intent(TestYourself.this, PairUp.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(TestYourself.this, "You need at least 5 terms on your list.", Toast.LENGTH_SHORT).show();
            }
        });

        memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(checked){
                    case(1):
                        list=UNACQUIRED_COUNT;
                        break;
                    case(2):
                        list=UNACQUIRED_COUNT2;
                        break;
                    case(3):
                        list=UNACQUIRED_COUNT3;
                        break;
                }
                if(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(list,0)>=8) {
                    Intent intent = new Intent(TestYourself.this, Memory.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(TestYourself.this, "You need at least 8 terms on your list.", Toast.LENGTH_SHORT).show();
            }
        });

        switchPhrases.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                save();
            }
        });

        list1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list1.setChecked(true);
                list2.setChecked(false);
                list3.setChecked(false);
                checked=1;
            }
        });

        list2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list2.setChecked(true);
                list1.setChecked(false);
                list3.setChecked(false);
                checked=2;
            }
        });

        list3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list3.setChecked(true);
                list1.setChecked(false);
                list2.setChecked(false);
                checked=3;
            }
        });
    }

    public void load(){
        language = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(WORD_ORDER,false);
        checked = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(CHECKED,1);
        switchPhrases.setChecked(language);
        switch(checked){
            case(1):
                list1.setChecked(true);
                list2.setChecked(false);
                list3.setChecked(false);
                break;
            case(2):
                list1.setChecked(false);
                list2.setChecked(true);
                list3.setChecked(false);
                break;
            case(3):
                list1.setChecked(false);
                list2.setChecked(false);
                list3.setChecked(true);
                break;
        }
    }

    public void save(){
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
        editor.putBoolean(WORD_ORDER,switchPhrases.isChecked());
        editor.putInt(CHECKED,checked);
        editor.apply();
        language = switchPhrases.isChecked();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}