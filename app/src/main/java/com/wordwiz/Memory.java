package com.wordwiz;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import static com.wordwiz.SettingsActivity.SHARED_PREFERENCES;
import static com.wordwiz.TestYourself.CHECKED;

public class Memory extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Term> terms;
    private int list;
    private TextView[] textViews;
    private Button[] imageViews;
    private TextView uncoveredTV;
    private Button uncoveredIV, second;
    private int turns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        turns=0;

        terms = new ArrayList<>();
        list=getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(CHECKED,1);
        terms.addAll(loadTerms());

        Collections.shuffle(terms);

        initialiseTiles();
        fillTheTiles();

        uncoveredTV=null;
        uncoveredIV=null;
        second=null;

    }

    private void initialiseTiles(){
        textViews = new TextView[16];
        imageViews = new Button[16];

        TextView m1 = findViewById(R.id.memory1);
        TextView m2 = findViewById(R.id.memory2);
        TextView m3 = findViewById(R.id.memory3);
        TextView m4 = findViewById(R.id.memory4);
        TextView m5 = findViewById(R.id.memory5);
        TextView m6 = findViewById(R.id.memory6);
        TextView m7 = findViewById(R.id.memory7);
        TextView m8 = findViewById(R.id.memory8);
        TextView m9 = findViewById(R.id.memory9);
        TextView m10 = findViewById(R.id.memory10);
        TextView m11 = findViewById(R.id.memory11);
        TextView m12 = findViewById(R.id.memory12);
        TextView m13 = findViewById(R.id.memory13);
        TextView m14 = findViewById(R.id.memory14);
        TextView m15 = findViewById(R.id.memory15);
        TextView m16 = findViewById(R.id.memory16);

        textViews[0]=m1;
        textViews[1]=m2;
        textViews[2]=m3;
        textViews[3]=m4;
        textViews[4]=m5;
        textViews[5]=m6;
        textViews[6]=m7;
        textViews[7]=m8;
        textViews[8]=m9;
        textViews[9]=m10;
        textViews[10]=m11;
        textViews[11]=m12;
        textViews[12]=m13;
        textViews[13]=m14;
        textViews[14]=m15;
        textViews[15]=m16;

        for(TextView t: textViews){
            t.setText("");
        }

        Button i1 = findViewById(R.id.imageView1);
        Button i2 = findViewById(R.id.imageView2);
        Button i3 = findViewById(R.id.imageView3);
        Button i4 = findViewById(R.id.imageView4);
        Button i5 = findViewById(R.id.imageView5);
        Button i6 = findViewById(R.id.imageView6);
        Button i7 = findViewById(R.id.imageView7);
        Button i8 = findViewById(R.id.imageView8);
        Button i9 = findViewById(R.id.imageView9);
        Button i10 = findViewById(R.id.imageView10);
        Button i11 = findViewById(R.id.imageView11);
        Button i12 = findViewById(R.id.imageView12);
        Button i13 = findViewById(R.id.imageView13);
        Button i14 = findViewById(R.id.imageView14);
        Button i15 = findViewById(R.id.imageView15);
        Button i16 = findViewById(R.id.imageView16);

        imageViews[0]=i1;
        imageViews[1]=i2;
        imageViews[2]=i3;
        imageViews[3]=i4;
        imageViews[4]=i5;
        imageViews[5]=i6;
        imageViews[6]=i7;
        imageViews[7]=i8;
        imageViews[8]=i9;
        imageViews[9]=i10;
        imageViews[10]=i11;
        imageViews[11]=i12;
        imageViews[12]=i13;
        imageViews[13]=i14;
        imageViews[14]=i15;
        imageViews[15]=i16;

        i1.setOnClickListener(this);
        i2.setOnClickListener(this);
        i3.setOnClickListener(this);
        i4.setOnClickListener(this);
        i5.setOnClickListener(this);
        i6.setOnClickListener(this);
        i7.setOnClickListener(this);
        i8.setOnClickListener(this);
        i9.setOnClickListener(this);
        i10.setOnClickListener(this);
        i11.setOnClickListener(this);
        i12.setOnClickListener(this);
        i13.setOnClickListener(this);
        i14.setOnClickListener(this);
        i15.setOnClickListener(this);
        i16.setOnClickListener(this);
    }

    private void fillTheTiles(){
        ArrayList<Integer> arrInt = new ArrayList<>(16);
        for(int i=0; i<=15;i++)
            arrInt.add(i);
        Collections.shuffle(arrInt);

        for(int i=0; i<=7; i++){
            textViews[arrInt.get(i)].setText(terms.get(i).getTerm1());
            textViews[arrInt.get(8+i)].setText(terms.get(i).getTerm2());
        }
    }

    private String whatTerm(TextView textView){
        for(Term a:terms){
            if(textView.getText().toString().equals(a.getTerm1()))
                return a.getTerm2();
            if(textView.getText().toString().equals(a.getTerm1()))
                return a.getTerm1();
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        if (v.getAlpha() == 0 || second !=null) return;
        v.setAlpha((float) 0.0);
        if(uncoveredTV==null){
            for(int i=0; i<16;i++){
                if(imageViews[i].equals(v)){
                    uncoveredIV=imageViews[i];
                    uncoveredTV=textViews[i];
                    break;
                }
            }
        }
        else{
            turns++;
            TextView help=null;
            second=(Button)v;
            for(int i=0; i<imageViews.length;i++){
                if(imageViews[i].equals((Button) v)) {
                    help = textViews[i];
                    break;
                }
            }
            if(whatTerm(uncoveredTV).equals((help).getText().toString())||
            whatTerm(help).equals(uncoveredTV.getText().toString())){
                uncoveredTV.setTextColor(Color.GREEN);
                help.setTextColor(Color.GREEN);
                help.setClickable(false);
                uncoveredIV.setClickable(false);
                uncoveredIV.setVisibility(View.INVISIBLE);
                second.setVisibility(View.INVISIBLE);
                uncoveredIV=null;
                uncoveredTV=null;
                second=null;

                if(isDone()){
                    String a = getResources().getString(R.string.memoryDone1) +turns+getResources().getString(R.string.memoryDone2);
                    Toast.makeText(Memory.this,a, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else cover();
        }
    }

    private void cover(){
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        uncoveredTV=null;
                        uncoveredIV=null;
                        second=null;
                        for(Button a:imageViews) {
                            if(a.getVisibility()!=View.INVISIBLE)
                                a.setAlpha((float) 1.0);
                        }
                    }
                },
                1600
        );
    }

    private boolean isDone(){
        for(Button a:imageViews){
            if(a.getVisibility()==View.VISIBLE)
                return false;
        }
        return true;
    }

    public HashSet<Term> loadTerms(){
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(getFilesDir()+"/wordlist"+list+".txt"))){
            Object a;
            while((a=ois.readObject())!=null){
                terms.add((Term) a);
            }
        } catch(EOFException e){} catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        int totalWeight=0;
        for(Term a:terms)
            totalWeight+=a.getWeight();

        Random random = new Random();
        int i=0;

        HashSet<Term> termHashSet = new HashSet<>();

        while(termHashSet.size()<8){
            i=random.nextInt(totalWeight);
            Log.v("unae",String.valueOf(terms.size()));
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
        terms.clear();
        return termHashSet;
    }
}