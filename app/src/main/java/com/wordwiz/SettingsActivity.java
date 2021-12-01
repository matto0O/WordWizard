package com.wordwiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT2;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT3;

public class SettingsActivity extends AppCompatActivity {

    Button save;
    SeekBar amount,precision,attempts;
    Switch deleteAcquired, caseSensitive,dailyMode;
    TextView TVdeleteAcquired, TVamount,TVprecision,TVattempts,TVcaseSensitive,TVdailyMode;

    public static final String SHARED_PREFERENCES = "shared_preferences";
    public static final String SB_AMOUNT = "amount";
    public static final String SB_PRECISION = "precision";
    public static final String SB_ATTEMPTS = "attempts";
    public static final String SWITCH = "switch";
    public static final String CASE_SENSITIVE = "case_sensitive";
    public static final String DAILY = "daily_mode";

    private int intAttempts, intAmount, intPrecision;
    private boolean isSwitched, isCaseSensitive, daily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        save = findViewById(R.id.buttonSaveChanges);
        amount = findViewById(R.id.seekBarAmount);
        precision = findViewById(R.id.seekBarPrecision);
        attempts = findViewById(R.id.seekBarAttemptsSaved);
        deleteAcquired = findViewById(R.id.switchDeleteAcquired);
        TVamount = findViewById(R.id.textViewSeekAmount);
        TVprecision = findViewById(R.id.textViewSeekPrecision);
        TVattempts = findViewById(R.id.textViewSeekAttemptsSaved);
        TVdeleteAcquired = findViewById(R.id.textViewSwitch);
        caseSensitive = findViewById(R.id.switchCaseSensitive);
        TVcaseSensitive = findViewById(R.id.textViewCaseSensitive);
        dailyMode = findViewById(R.id.switchDaily);
        TVdailyMode = findViewById(R.id.textViewDaily);

        dailyMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(dailyMode.isChecked()){
                    deleteAcquired.setChecked(false);
                    deleteAcquired.setClickable(false);
                    TVdeleteAcquired.setText(R.string.acquired_phrases_will_not_delete_themselves_automaticaly);
                    TVdailyMode.setText(R.string.daily_learning_mode_is_on);
                }
                else {
                    deleteAcquired.setClickable(true);
                    TVdailyMode.setText(R.string.daily_learning_mode_is_off);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {saveData();}
        });

        caseSensitive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    TVcaseSensitive.setText(R.string.answers_will_be_case_sensitive);
                else
                    TVcaseSensitive.setText(R.string.answers_will_not_be_case_sensitive);
            }
        });

        amount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TVamount.setText(getString(R.string.testSize,progress+5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        precision.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TVprecision.setText(getString(R.string.acquiredAfter,progress+50));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        attempts.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TVattempts.setText(getString(R.string.attempts,progress+5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        deleteAcquired.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    TVdeleteAcquired.setText(R.string.acquired_phrases_will_delete_themselves_automaticaly);
                else
                    TVdeleteAcquired.setText(R.string.acquired_phrases_will_not_delete_themselves_automaticaly);
            }
        });

        loadData();
        refreshText();

    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SWITCH,deleteAcquired.isChecked());
        editor.putBoolean(CASE_SENSITIVE,caseSensitive.isChecked());
        editor.putInt(SB_AMOUNT,amount.getProgress());
        editor.putInt(SB_PRECISION,precision.getProgress());
        editor.putInt(SB_ATTEMPTS,attempts.getProgress());
        editor.putBoolean(DAILY,dailyMode.isChecked());

        editor.apply();
        loadData();
        overwriteTerms(getFilesDir()+"/wordlist1.txt",UNACQUIRED_COUNT);
        overwriteTerms(getFilesDir()+"/wordlist2.txt",UNACQUIRED_COUNT2);
        overwriteTerms(getFilesDir()+"/wordlist3.txt",UNACQUIRED_COUNT3);

        Toast.makeText(SettingsActivity.this, R.string.savedSettings, Toast.LENGTH_SHORT).show();
    }

    public void overwriteTerms(String dir, String key){
        ArrayList<Term> terms = new ArrayList<>();

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dir))){
            Object a;
            while((a=ois.readObject())!=null){
                terms.add((Term)a);
            }
        } catch(EOFException e){} catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        for(Term a: terms){
            boolean b=a.isAcquired();
            int i=1;
            a.updateAcquisitionRate(intPrecision);
            a.changeAttemptsPrecision(intAttempts);
            if(b!=a.isAcquired()) {
                if(!b)
                    i=-1;
                getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit()
                        .putInt(key, getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
                                .getInt(key, 0) + i)
                        .apply();
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(dir))){
            pw.print("");
        } catch (IOException a) {Log.e("TAG",a.getMessage());}
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dir))) {
            while(!terms.isEmpty()){
                oos.writeObject(terms.remove(0));
            }
        } catch (IOException a) {Log.e("TAG",a.getMessage());}
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);

        intAmount = sharedPreferences.getInt(SB_AMOUNT,0);
        intPrecision = sharedPreferences.getInt(SB_PRECISION,45);
        intAttempts = sharedPreferences.getInt(SB_ATTEMPTS,0);
        isSwitched = sharedPreferences.getBoolean(SWITCH,false);
        isCaseSensitive = sharedPreferences.getBoolean(CASE_SENSITIVE,true);
        daily = sharedPreferences.getBoolean(DAILY,false);

        deleteAcquired.setChecked(isSwitched);
        amount.setProgress(intAmount);
        precision.setProgress(intPrecision);
        attempts.setProgress(intAttempts);
        caseSensitive.setChecked(isCaseSensitive);
        dailyMode.setChecked(daily);
    }

    public void refreshText(){
        if(isSwitched)
            TVdeleteAcquired.setText(R.string.acquired_phrases_will_delete_themselves_automaticaly);
        else
            TVdeleteAcquired.setText(R.string.acquired_phrases_will_not_delete_themselves_automaticaly);

        TVattempts.setText(getString(R.string.attempts,intAttempts+5));
        TVamount.setText(getString(R.string.testSize,intAmount+5));
        TVprecision.setText(getString(R.string.acquiredAfter,intPrecision+50));

        if(isCaseSensitive)
            TVcaseSensitive.setText(R.string.answers_will_be_case_sensitive);
        else
            TVcaseSensitive.setText(R.string.answers_will_not_be_case_sensitive);

        if(daily)
            TVdailyMode.setText(R.string.daily_learning_mode_is_on);
        else
            TVdailyMode.setText(R.string.daily_learning_mode_is_off);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}