package com.wordwiz;

import androidx.annotation.ArrayRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import static com.wordwiz.SettingsActivity.SHARED_PREFERENCES;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT2;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT3;

public class DeleteVocabulary extends AppCompatActivity {

    private ListView listView;
    static ArrayAdapter<Term> arrayAdapter;
    private RadioButton list1,list2,list3;
    public static String listDir1,listDir2,listDir3;
    ArrayList<Term> arr1,arr2,arr3;
    int checked=1;
    String list;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_vocabulary);

        Button makeUnacquired = findViewById(R.id.buttonMakeNotAcquired);
        Button clearAll = findViewById(R.id.buttonClearTotalHistory);

        listDir1= getFilesDir()+"/wordlist1.txt";
        listDir2= getFilesDir()+"/wordlist2.txt";
        listDir3= getFilesDir()+"/wordlist3.txt";

        arr1 = initializeAnArrayList(listDir1);
        arr2 = initializeAnArrayList(listDir2);
        arr3 = initializeAnArrayList(listDir3);

        listView =findViewById(R.id.listView);
        list1=findViewById(R.id.radioButtonList1Delete);
        list2=findViewById(R.id.radioButtonList2Delete);
        list3=findViewById(R.id.radioButtonList3Delete);
        refreshArAdapter(arr1);

        list1.setOnClickListener(v -> {
            list1.setChecked(true);
            list2.setChecked(false);
            list3.setChecked(false);
            checked=1;
            arr1 = initializeAnArrayList(listDir1);
            refreshArAdapter(arr1);
        });

        list2.setOnClickListener(v -> {
            list2.setChecked(true);
            list1.setChecked(false);
            list3.setChecked(false);
            checked=2;
            arr2 = initializeAnArrayList(listDir2);
            refreshArAdapter(arr2);
        });

        list3.setOnClickListener(v -> {
            list3.setChecked(true);
            list1.setChecked(false);
            list2.setChecked(false);
            checked=3;
            arr3 = initializeAnArrayList(listDir3);
            refreshArAdapter(arr3);
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createDialog(position);
            }
        });

        makeUnacquired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
                int i=0;

                switch(checked) {
                    default:
                        for(Term a:arr1){
                            if(a.isAcquired()) {
                                a.makeUnacquired();
                                i++;
                            }
                        }
                        refreshArAdapter(arr1);
                        saveAList(listDir1, (ArrayList<Term>) arr1.clone());
                        list=UNACQUIRED_COUNT;
                        break;
                    case (2):
                        for(Term a:arr2){
                            if(a.isAcquired()) {
                                a.makeUnacquired();
                                i++;
                            }
                        }
                        refreshArAdapter(arr2);
                        saveAList(listDir2, (ArrayList<Term>) arr2.clone());
                        list=UNACQUIRED_COUNT2;
                        break;
                    case (3):
                        for(Term a:arr3){
                            if(a.isAcquired()) {
                                a.makeUnacquired();
                                i++;
                            }
                        }
                        refreshArAdapter(arr3);
                        saveAList(listDir3, (ArrayList<Term>) arr3.clone());
                        list=UNACQUIRED_COUNT3;
                        break;
                }

                editor.putInt(list,getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(list,0)+i);
                editor.apply();

                if(i!=0)
                    Toast.makeText(DeleteVocabulary.this, R.string.clearedHistory+i+R.string.clearedHistory2, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(DeleteVocabulary.this, R.string.noTermsAffectedd, Toast.LENGTH_SHORT).show();

                listView.refreshDrawableState();
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Term> termArrayList;
                String listdir;

                switch(checked){
                    default:
                        termArrayList=arr1;
                        listdir=listDir1;
                        list=UNACQUIRED_COUNT;
                        break;
                    case(2):
                        termArrayList=arr2;
                        listdir=listDir2;
                        list=UNACQUIRED_COUNT2;
                        break;
                    case(3):
                        termArrayList=arr3;
                        listdir=listDir3;
                        list=UNACQUIRED_COUNT3;
                        break;
                }

                for(Term a:termArrayList)
                    a.makeUnacquired();

                SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
                editor.putInt(list,termArrayList.size());
                editor.apply();

                refreshArAdapter(termArrayList);
                saveAList(listdir, (ArrayList<Term>) termArrayList.clone());
                listView.refreshDrawableState();
            }
        });
    }

    public void createDialog(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.popup,null);

        TextView term = popup.findViewById(R.id.textViewTerm);
        Button delete = popup.findViewById(R.id.buttonDeleteTerm);
        Button clearHistory = popup.findViewById(R.id.buttonClearHistory);

        builder.setView(popup);
        dialog = builder.create();
        dialog.show();

        ArrayList<Term> arrayList;
        String listDir;

        switch(checked){
            default:
                arrayList=arr1;
                list=UNACQUIRED_COUNT;
                listDir = listDir1;
                break;
            case(2):
                arrayList=arr2;
                list=UNACQUIRED_COUNT2;
                listDir = listDir2;
                break;
            case(3):
                arrayList=arr3;
                list=UNACQUIRED_COUNT3;
                listDir = listDir3;
                break;
        }

        term.setText(arrayList.get(position).toString());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeleteVocabulary.this,
                        R.string.removed+arrayList.get(position).getTerm1() + " - "
                                +arrayList.remove(position).getTerm2()
                                +R.string.fromList+checked+".", Toast.LENGTH_SHORT).show();
                refreshArAdapter(arrayList);
                saveAList(listDir,(ArrayList<Term>) arrayList.clone());
                listView.refreshDrawableState();

                SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
                editor.putInt(list,Math.max(getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(list,0)-1,0));
                editor.apply();
                dialog.dismiss();
            }
        });

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.get(position).isAcquired()){
                    SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
                    editor.putInt(list,getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getInt(list,0)+1);
                    editor.apply();
                }
                arrayList.get(position).makeUnacquired();
                Toast.makeText(DeleteVocabulary.this,
                        R.string.clearedHistory+arrayList.get(position).getTerm1() + " - "
                                +arrayList.get(position).getTerm2()+".", Toast.LENGTH_SHORT).show();
                refreshArAdapter(arrayList);
                saveAList(listDir,(ArrayList<Term>) arrayList.clone());
                listView.refreshDrawableState();
                dialog.dismiss();
            }
        });
    }

    public void refreshArAdapter(ArrayList<Term> arr){
        arrayAdapter = new ArrayAdapter<>(DeleteVocabulary.this, android.R.layout.simple_list_item_1,arr);
        listView.setAdapter(arrayAdapter);
    }

    public static ArrayList<Term> initializeAnArrayList(String fileDir){
        ArrayList<Term> terms = new ArrayList<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileDir))){
            Object a;
            while((a=ois.readObject())!=null) {
                terms.add((Term) a);
            }
        } catch(IOException | ClassNotFoundException e){}
        return terms;
    }

    @Override
    public void onStop() {
        saveAList(listDir1, arr1);
        saveAList(listDir2, arr2);
        saveAList(listDir3, arr3);
        super.onStop();
    }

    public void saveAList(String listDir, ArrayList<Term> arr){
        try (PrintWriter pw = new PrintWriter(new FileWriter(listDir))){
            pw.print("");
        } catch (IOException a) {Log.e("TAG",a.getMessage());}
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(listDir))) {
            while(!arr.isEmpty()){
                Log.v("unae","Saved "+arr.get(0).toString());
                oos.writeObject(arr.remove(0));
            }
        } catch (IOException a) {Log.e("TAG",a.getMessage());}
    }
}