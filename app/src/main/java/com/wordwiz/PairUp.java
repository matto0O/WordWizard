package com.wordwiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import static com.wordwiz.MainActivity.STREAK;
import static com.wordwiz.SettingsActivity.SHARED_PREFERENCES;
import static com.wordwiz.SettingsActivity.SWITCH;
import static com.wordwiz.TestYourself.CHECKED;
import static com.wordwiz.TestYourself.WORD_ORDER;
import static com.wordwiz.TranslatedPhrases.DATEdir;

public class PairUp extends AppCompatActivity {

    boolean delete,lang;

    ArrayList<Term> randomized, terms;
    int checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_up);

        Button pairUp = findViewById(R.id.buttonCheckPairs);
        Spinner spinner1 = findViewById(R.id.spinner1);
        Spinner spinner2 = findViewById(R.id.spinner2);
        Spinner spinner3 = findViewById(R.id.spinner3);
        Spinner spinner4 = findViewById(R.id.spinner4);
        Spinner spinner5 = findViewById(R.id.spinner5);
        TextView textView1 = findViewById(R.id.textView1);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);
        TextView textView4 = findViewById(R.id.textView4);
        TextView textView5 = findViewById(R.id.textView5);

        delete = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(SWITCH,false);
        lang = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(WORD_ORDER,false);
        checked = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(CHECKED,1);

        terms = new ArrayList<>();
        loadAllTerms();

        randomized = randomizeList();
        ArrayList<String> randomizedStrings = new ArrayList<>(randomized.size());

        if(lang) {
            textView1.setText(randomized.get(0).getTerm2());
            textView2.setText(randomized.get(1).getTerm2());
            textView3.setText(randomized.get(2).getTerm2());
            textView4.setText(randomized.get(3).getTerm2());
            textView5.setText(randomized.get(4).getTerm2());

            for(Term a:randomized)
                randomizedStrings.add(a.getTerm1());
        }
        else{
            textView1.setText(randomized.get(0).getTerm1());
            textView2.setText(randomized.get(1).getTerm1());
            textView3.setText(randomized.get(2).getTerm1());
            textView4.setText(randomized.get(3).getTerm1());
            textView5.setText(randomized.get(4).getTerm1());

            for(Term a:randomized)
                randomizedStrings.add(a.getTerm2());
        }

        Collections.shuffle(randomizedStrings);
        randomizedStrings.add(0," ");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(PairUp.this, android.R.layout.simple_list_item_1,randomizedStrings);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);
        spinner4.setAdapter(adapter);
        spinner5.setAdapter(adapter);

        pairUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean results[] = new boolean[5];

                if(!lang) {
                    results[0] = (spinner1.getSelectedItem()).equals(randomized.get(0).getTerm2());
                    results[1] = (spinner2.getSelectedItem()).equals(randomized.get(1).getTerm2());
                    results[2] = (spinner3.getSelectedItem()).equals(randomized.get(2).getTerm2());
                    results[3] = (spinner4.getSelectedItem()).equals(randomized.get(3).getTerm2());
                    results[4] = (spinner5.getSelectedItem()).equals(randomized.get(4).getTerm2());
                }
                else{
                    results[0] = (spinner1.getSelectedItem()).equals(randomized.get(0).getTerm1());
                    results[1] = (spinner2.getSelectedItem()).equals(randomized.get(1).getTerm1());
                    results[2] = (spinner3.getSelectedItem()).equals(randomized.get(2).getTerm1());
                    results[3] = (spinner4.getSelectedItem()).equals(randomized.get(3).getTerm1());
                    results[4] = (spinner5.getSelectedItem()).equals(randomized.get(4).getTerm1());
                }

                String a ="";
                a+=getResources().getString(R.string.results);
                boolean b = true;
                for(int i=0;i<4;i++){
                    if(results[i]) a+=getResources().getString(R.string.right1);
                    else{
                        a+=getResources().getString(R.string.wrong1);
                        b=false;
                    }
                }
                if(results[4])a+=getResources().getString(R.string.right2);
                else{
                    a+=getResources().getString(R.string.wrong2);
                    b=false;
                }
                if(b) {
                    Toast.makeText(PairUp.this, R.string.everythingRight, Toast.LENGTH_SHORT).show();
                    saveDate();
                    finish();
                }
                else
                    Toast.makeText(PairUp.this, a, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadAllTerms(){
        String directory;
        switch(checked){
            default:
                directory=getFilesDir()+"/wordlist1.txt";
                break;
            case(2):
                directory=getFilesDir()+"/wordlist2.txt";
                break;
            case(3):
                directory=getFilesDir()+"/wordlist3.txt";
                break;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(directory))){
            Object a;
            while((a=ois.readObject())!=null)
                terms.add((Term) a);
        } catch(EOFException e){} catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Term> randomizeList(){
        int totalWeight=0;
        for(Term a:terms)
            totalWeight+=a.getWeight();

        Random random = new Random();
        int i=0;

        HashSet<Term> termHashSet = new HashSet<>();

        while(termHashSet.size()<5){
            Log.v("unae",String.valueOf(totalWeight));
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
        ArrayList<Term> termArrayList = new ArrayList<>(termHashSet.size());
        termArrayList.addAll(termHashSet);
        return termArrayList;
    }

    public void saveDate(){
        Date loaded = WordWizard.loadDate();
        if(loaded==null) loaded=new Date(System.currentTimeMillis());

        Calendar c = Calendar.getInstance();

        Date date = c.getTime();
        c.setTime(loaded);
        c.add(Calendar.DATE,1);
        loaded = c.getTime();

        if(loaded.getDay()==date.getDay()){
            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
            editor.putInt(STREAK,getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(STREAK,0)+1);
            editor.apply();
        }

        if(date!=null){
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilesDir()+DATEdir))) {
                oos.writeObject(date);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}