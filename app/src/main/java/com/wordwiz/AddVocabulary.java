package com.wordwiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import static com.wordwiz.SettingsActivity.SHARED_PREFERENCES;
import static com.wordwiz.SettingsActivity.SB_PRECISION;
import static com.wordwiz.SettingsActivity.SB_ATTEMPTS;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT2;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT3;

public class AddVocabulary extends AppCompatActivity {

    public static ArrayList<Term> terms1,terms2,terms3;

    EditText textTerm1,textTerm2;
    Button addPhrase,clearList,deletePhrase;
    RadioButton list1,list2,list3;
    String list;
    public static String listDir1,listDir2,listDir3;
    int checked=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vocabulary);

        listDir1= getFilesDir()+"/wordlist1.txt";
        listDir2= getFilesDir()+"/wordlist2.txt";
        listDir3= getFilesDir()+"/wordlist3.txt";

        textTerm1 = findViewById(R.id.textTerm1);
        textTerm2 = findViewById(R.id.textTerm2);
        addPhrase = findViewById(R.id.buttonAddPhrase);
        clearList = findViewById(R.id.buttonClear);
        deletePhrase = findViewById(R.id.buttonDeletePhrase);
        list1=findViewById(R.id.radioButtonList1);
        list2=findViewById(R.id.radioButtonList2);
        list3=findViewById(R.id.radioButtonList3);

        terms1 = new ArrayList<>();
        terms2 = new ArrayList<>();
        terms3 = new ArrayList<>();

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

        addPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Term term = new Term(textTerm1.getText().toString(),textTerm2.getText().toString(),
                        getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(SB_PRECISION,45),
                        getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(SB_ATTEMPTS,5));
                textTerm1.setText("");
                textTerm2.setText("");

                switch(checked) {
                    default:
                        list=UNACQUIRED_COUNT;
                        addToTheList(terms1, term);
                        break;
                    case (2):
                        list=UNACQUIRED_COUNT2;
                        addToTheList(terms2, term);
                        break;
                    case (3):
                        list=UNACQUIRED_COUNT3;
                        addToTheList(terms3, term);
                        break;
                }
                Log.v("unae","now: "+getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE)
                        .getInt(list,0));
            }
        });

        clearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(checked){
                    default:
                        list=UNACQUIRED_COUNT;
                        clearAList(listDir1,terms1);
                        break;
                    case(2):
                        list=UNACQUIRED_COUNT2;
                        clearAList(listDir2,terms2);
                        break;
                    case(3):
                        list=UNACQUIRED_COUNT3;
                        clearAList(listDir3,terms3);
                        break;
                }
                Toast.makeText(AddVocabulary.this, getResources().getString(R.string.succesfullyCleared) +checked+".", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
                editor.putInt(list,0);
                editor.apply();
            }
        });

        deletePhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddVocabulary.this, DeleteVocabulary.class);
                startActivity(intent);
            }
        });

        textTerm1.addTextChangedListener(textWatcher);
        textTerm2.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text1 = textTerm1.getText().toString().trim();
            String text2 = textTerm2.getText().toString().trim();
            addPhrase.setEnabled(!text1.isEmpty()&&!text2.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void addToTheList(ArrayList<Term> arr, Term term){
        if(!arr.contains(term)) {
            SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
            Toast.makeText(AddVocabulary.this, "Successfully added " + term.getTerm1() +
                    " - " + term.getTerm2() + " to the list "+checked+".", Toast.LENGTH_LONG).show();
            arr.add(term);
            editor.putInt(list,getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE)
                    .getInt(list,0)+1);
            editor.apply();
            Log.v("unae","put"+getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE)
                    .getInt(list,0));
        }
        else
            Toast.makeText(AddVocabulary.this, "Phrase already exists on this list!",
                    Toast.LENGTH_SHORT).show();
    }

    public void loadAllLists(){
        loadAList(listDir1,terms1);
        loadAList(listDir2,terms2);
        loadAList(listDir3,terms3);
    }

    public void saveAllLists(){
        saveAList(listDir1,terms1);
        saveAList(listDir2,terms2);
        saveAList(listDir3,terms3);
    }

    public void loadAList(String listDir,ArrayList<Term> arr){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(listDir))){
            Object a;
            while((a=ois.readObject())!=null){
                arr.add((Term)a);
            }
        } catch(EOFException e){}
        catch (IOException | ClassNotFoundException a) {Log.e("TAG",a.getClass().toString());}
    }

    public void saveAList(String listDir, ArrayList<Term> arr){
        try (PrintWriter pw = new PrintWriter(new FileWriter(listDir))){
            pw.print("");
        } catch (IOException a) {Log.e("TAG",a.getMessage());}
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(listDir))) {
            while(!arr.isEmpty()){
                oos.writeObject(arr.remove(0));
            }
        } catch (IOException a) {Log.e("TAG",a.getMessage());}
    }

    public void clearAList(String listDir, ArrayList<Term> arr){
        arr.clear();
        try (PrintWriter pw = new PrintWriter(new FileWriter(listDir))){
            pw.print("");
        } catch (IOException a) {Log.e("TAG",a.getMessage());}
    }

    @Override
    public void onPause() {
        saveAllLists();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllLists();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}