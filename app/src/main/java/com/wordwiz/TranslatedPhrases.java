package com.wordwiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import static com.wordwiz.SettingsActivity.DAILY;
import static com.wordwiz.SettingsActivity.SB_AMOUNT;
import static com.wordwiz.SettingsActivity.SWITCH;
import static com.wordwiz.SettingsActivity.CASE_SENSITIVE;
import static com.wordwiz.SettingsActivity.SHARED_PREFERENCES;
import static com.wordwiz.TestYourself.CHECKED;
import static com.wordwiz.TestYourself.WORD_ORDER;
import static com.wordwiz.WordWizard.loadDate;
import static com.wordwiz.MainActivity.STREAK;

public class TranslatedPhrases extends AppCompatActivity {

    TextView result,phrase;
    EditText answer;
    Button getCorrect, justCheck;

    ArrayList<Term> terms;
    HashSet<Term> termsReturned;

    ArrayQueue<Term> testQueue;

    boolean delete,csens,wordOrder;

    int acquiredAmount=0;
    int list;

    public static final String UNACQUIRED_COUNT = "unacquired_count";
    public static final String UNACQUIRED_COUNT2 = "unacquired_count2";
    public static final String UNACQUIRED_COUNT3 = "unacquired_count3";
    public static final String DATEdir = "/date.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translated_phrases);

        result = findViewById(R.id.textViewPreviousResult);
        phrase = findViewById(R.id.textViewPhrase);
        answer = findViewById(R.id.editTextAnswer);
        getCorrect = findViewById(R.id.buttonWithCorrectAnswer);
        justCheck = findViewById(R.id.buttonWithoutAnswer);

        delete = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(SWITCH,false);
        csens = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(CASE_SENSITIVE,true);
        wordOrder = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(WORD_ORDER,false);

        list = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(CHECKED,1);

        terms = new ArrayList<>();
        loadAllTerms();

        testQueue = new ArrayQueue<>(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(SB_AMOUNT,0)+5);
        termsReturned = getTestedTerms(
                getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(SB_AMOUNT,0)+5);

        for(Term a: termsReturned){
            try { testQueue.enqueue(a); }
            catch (FullQueueException e) { e.printStackTrace(); }
        }

        termsReturned.clear();
        try {
            test(wordOrder);
        } catch (EmptyQueueException e) {
            e.printStackTrace();
        }
    }

    public HashSet<Term> getTestedTerms(int amountOfTerms){
        int totalWeight=0;
        for(Term a:terms)
            totalWeight+=a.getWeight();

        Random random = new Random();
        int i=0;

        HashSet<Term> termHashSet = new HashSet<>();

        while(termHashSet.size()<amountOfTerms&&termHashSet.size()<(terms.size()-acquiredAmount)){
            i=random.nextInt(totalWeight);
            for(int j=0; j<terms.size();j++){
                i-=terms.get(j).getWeight();
                if(i<=0){
                    if(!terms.get(j).isAcquired()) {
                        termHashSet.add(terms.get(j));
                        break;
                    }
                }
            }
        }
        return termHashSet;
    }

    public void test(boolean b) throws EmptyQueueException {
        Term a = testQueue.first();

        if(!b) phrase.setText(a.getTerm1());
        else phrase.setText(a.getTerm2());

        getCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerUser,answerCorrect;
                if(!b) {
                    answerUser = answer.getText().toString().trim();
                    answerCorrect = a.getTerm2();
                }
                else{
                    answerUser = answer.getText().toString().trim();
                    answerCorrect = a.getTerm1();
                }

                if(!csens){
                    answerUser=answerUser.toLowerCase();
                    answerCorrect=answerCorrect.toLowerCase();
                }
                if (answerUser.trim().equals(answerCorrect)) {
                    result.setText(R.string.correct);
                    result.setTextColor(Color.parseColor("#79FF09"));
                    answer.setText("");
                    a.gotRight();
                    termsReturned.add(a);
                    try {
                        testQueue.dequeue();
                    } catch (EmptyQueueException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    if(!b) result.setText(getResources().getString(R.string.wrongAndAnswer) + a.getTerm2());
                    else result.setText(getResources().getString(R.string.wrongAndAnswer) + a.getTerm1());
                    result.setTextColor(Color.parseColor("#FE1403"));
                    answer.setText("");
                    a.gotWrong();
                    try {
                        testQueue.enqueue(testQueue.dequeue());
                    } catch (FullQueueException | EmptyQueueException e) {
                        e.printStackTrace();
                    }
                }

                if(testQueue.isEmpty()) closeOff();
                else{
                    try {
                        test(wordOrder);
                    } catch (EmptyQueueException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        justCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerUser,answerCorrect;
                if(!b) {
                    answerUser = answer.getText().toString().trim();
                    answerCorrect = a.getTerm2();
                }
                else{
                    answerUser = answer.getText().toString().trim();
                    answerCorrect = a.getTerm1();
                }

                if(!csens){
                    answerUser=answerUser.toLowerCase();
                    answerCorrect=answerCorrect.toLowerCase();
                }
                if (answerUser.trim().equals(answerCorrect)) {
                    result.setText(R.string.correct);
                    result.setTextColor(Color.parseColor("#79FF09"));
                    answer.setText("");
                    a.gotRight();
                    termsReturned.add(a);
                    try {
                        testQueue.dequeue();
                    } catch (EmptyQueueException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    result.setText(R.string.wrongTryAgain);
                    result.setTextColor(Color.parseColor("#FE1403"));
                    answer.setText("");
                    a.gotWrong();
                }
                if(testQueue.isEmpty()) closeOff();
                else{
                    try {
                        test(wordOrder);
                    } catch (EmptyQueueException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void loadAllTerms(){
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(getFilesDir()+"/wordlist"+list+".txt"))){
            Object a;
            while((a=ois.readObject())!=null){
                Term term = (Term) a;
                if(!delete&&term.isAcquired())
                        acquiredAmount++;
                terms.add(term);
            }
        } catch(EOFException e){} catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveAllTerms(){
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilesDir()+"/wordlist"+String.valueOf(list)+".txt"));){
            pw.print("");
        } catch (IOException a) {
            Log.e("TAG",a.getMessage());}
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilesDir()+"/wordlist"+String.valueOf(list)+".txt"))) {
           terms.clear();
           terms.addAll(termsReturned);

            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();

           if(delete) {
               for (Term a : terms)
                   if (a.isAcquired())
                       terms.remove(a);

               switch(list){
                   case(1):
                       editor.putInt(UNACQUIRED_COUNT,terms.size());
                       break;
                   case(2):
                       editor.putInt(UNACQUIRED_COUNT2,terms.size());
                       break;
                   case(3):
                       editor.putInt(UNACQUIRED_COUNT3,terms.size());
                       break;

               }
               while(!terms.isEmpty()){
                   oos.writeObject(terms.remove(0));
               }
           }
            else{
                int i=0;
                while(!terms.isEmpty()){
                    Term t = terms.remove(0);
                    if(!t.isAcquired()) i++;
                    oos.writeObject(t);
                }
               switch(list){
                   case(1):
                       editor.putInt(UNACQUIRED_COUNT,i);
                       break;
                   case(2):
                       editor.putInt(UNACQUIRED_COUNT2,i);
                       break;
                   case(3):
                       editor.putInt(UNACQUIRED_COUNT3,i);
                       break;

               }
           }
            editor.apply();

        } catch (IOException a) {Log.e("TAG",a.getMessage());}

    }

    @Override
    public void onBackPressed(){
        Toast.makeText(TranslatedPhrases.this, R.string.progressNotSaved, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void saveDate(){
        Date loaded = WordWizard.loadDate();
        if(loaded==null) loaded=new Date(System.currentTimeMillis());

        Calendar c = Calendar.getInstance();

        Date date = c.getTime();
        c.setTime(loaded);

        if(loaded.getDay()==date.getDay()){
            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
            editor.putInt(STREAK,1);
            editor.apply();
        }

        else {
            c.add(Calendar.DATE, 1);
            loaded = c.getTime();

            if (loaded.getDay() == date.getDay()) {
                SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit();
                editor.putInt(STREAK, getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).getInt(STREAK, 0) + 1);
                editor.apply();
            }
        }

        if(date!=null){
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilesDir()+DATEdir))) {
                oos.writeObject(date);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeOff(){
        Toast.makeText(TranslatedPhrases.this, R.string.everythingRight, Toast.LENGTH_SHORT).show();

        while(!terms.isEmpty()){
            termsReturned.add(terms.remove(0));
        }

        saveDate();
        hurryUp();
        saveAllTerms();
        finish();
    }

    public void hurryUp(){
        Calendar c = Calendar.getInstance();
        Date date = loadDate();
        if(date!=null){
            c.setTime(date);
            stopAlarm(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(DAILY,false));
            c.add(Calendar.HOUR_OF_DAY,18);
            startAlarm(c,getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(DAILY,false));
        }
    }

    private void startAlarm(Calendar c, boolean channel){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent=null;
        if(channel) intent = new Intent(this, AlertReceiver.class);
        else intent = new Intent(this, AlertReceiver2.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void stopAlarm(boolean channel){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = null;
        if(channel) intent = new Intent(this, AlertReceiver.class);
        else intent = new Intent(this, AlertReceiver2.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,0);
        alarmManager.cancel(pendingIntent);
    }

}