package com.wordwiz;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
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
import java.util.Date;

import static com.wordwiz.SettingsActivity.DAILY;
import static com.wordwiz.TranslatedPhrases.DATEdir;
import static com.wordwiz.SettingsActivity.SHARED_PREFERENCES;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT2;
import static com.wordwiz.TranslatedPhrases.UNACQUIRED_COUNT3;

public class WordWizard extends Application {

    public static NotificationHelper notificationHelper;
    static String stringDate;
    static Date now,then;

    @Override
    public void onCreate() {
        super.onCreate();
        stringDate = getFilesDir()+DATEdir;

        initializeDates();

        if(then.getDay()!=now.getDay()&&
                getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).getBoolean(DAILY,false)){
            deacquireLists();
        }
    }

    public static void initializeDates(){
        now = new Date(System.currentTimeMillis());
        then=loadDate();
        if(then==null)
            then=now;
    }

    public static Date loadDate(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(stringDate))) {
            return (Date)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deacquireLists(){
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE).edit();
        ArrayList<Term> arr = new ArrayList<>();
        int i=0;
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(getFilesDir()+"/wordlist1.txt"))){
            Object a;
            while((a=ois.readObject())!=null){
                Term t = (Term)a;
                t.makeUnacquired();
                arr.add(t);
                i++;
            }
        } catch(EOFException e){
            editor.putInt(UNACQUIRED_COUNT,i);
            i=0;
                    try (PrintWriter pw = new PrintWriter(new FileWriter(getFilesDir()+"/wordlist1.txt"))){
                        pw.print("");
                    } catch (IOException a) {Log.e("TAG",a.getMessage());}
                    try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilesDir()+"/wordlist1.txt"))) {
                        while(!arr.isEmpty()){
                            oos.writeObject(arr.remove(0));
                        }
                    } catch (IOException a) {Log.e("TAG",a.getMessage());}
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(getFilesDir()+"/wordlist2.txt"))){
            Object a;
            while((a=ois.readObject())!=null){
                Term t = (Term)a;
                t.makeUnacquired();
                i++;
                arr.add(t);
            }
        } catch(EOFException e){
            editor.putInt(UNACQUIRED_COUNT2,i);
            i=0;
                    try (PrintWriter pw = new PrintWriter(new FileWriter(getFilesDir()+"/wordlist2.txt"))){
                        pw.print("");
                    } catch (IOException a) {Log.e("TAG",a.getMessage());}
                    try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilesDir()+"/wordlist2.txt"))) {
                        while(!arr.isEmpty()){
                            oos.writeObject(arr.remove(0));
                        }
                    } catch (IOException a) {Log.e("TAG",a.getMessage());}
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(getFilesDir()+"/wordlist3.txt"))){
            Object a;
            while((a=ois.readObject())!=null){
                Term t = (Term)a;
                t.makeUnacquired();
                i++;
                arr.add(t);
            }
        } catch(EOFException e){
            editor.putInt(UNACQUIRED_COUNT3,i);
                    try (PrintWriter pw = new PrintWriter(new FileWriter(getFilesDir()+"/wordlist3.txt"))){
                        pw.print("");
                    } catch (IOException a) {Log.e("TAG",a.getMessage());}
                    try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilesDir()+"/wordlist3.txt"))) {
                        while(!arr.isEmpty()){
                            oos.writeObject(arr.remove(0));
                        }
                    } catch (IOException a) {Log.e("TAG",a.getMessage());}
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        editor.apply();
    }
}
