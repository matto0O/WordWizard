package com.wordwiz;

import android.util.Log;

import java.io.Serializable;
import java.util.Objects;

public class Term implements Serializable{

    private String term1, term2;
    private boolean[] attempts;
    private boolean gotAcquired;
    private int acquisitionRate;
    private int attemptsPrecision;

    public Term(String term1, String term2, int acqRate, int attPrecision){
        this.term1 = term1.trim();
        this.term2 = term2.trim();
        acquisitionRate = acqRate+50;
        attemptsPrecision = attPrecision+5;
        attempts = new boolean[attemptsPrecision];
        for(int i=0; i<attemptsPrecision; i++) attempts[i] = false;
        gotAcquired=false;
    }

    public void changeAttemptsPrecision(int value){
        if(attemptsPrecision==value+5) return;

        boolean[] attempts1;

        if(attemptsPrecision>value+5){
            attempts1=new boolean[value+5];
            int v=value+5;
            for(int i=attemptsPrecision-1;i>=attemptsPrecision-value-5;i--){
                attempts1[v-1]=attempts[i];
                v--;
            }
        }

        else {
            attempts1 = new boolean[value+5];
            int e=0;
            while(e<attempts.length){
                attempts1[e]=attempts[e];
                e++;
            }
            while(e<attempts1.length){
                attempts1[e]=false;
                e++;
            }
        }

        attempts=attempts1;
        attemptsPrecision = value+5;
    }

    public void updateAcquisitionRate(int val){
        acquisitionRate = val+50;
        if(!updateAcquisition()){
            gotAcquired=false;
        }
    }

    public void gotRight(){
        moveResults();
        attempts[attemptsPrecision-1]=true;
        updateAcquisition();
    }

    public void gotWrong(){
        moveResults();
        attempts[attemptsPrecision-1]=false;
        updateAcquisition();
    }

    public void moveResults(){
        for(int i=0; i<attemptsPrecision-1; i++)
            attempts[i]=attempts[i+1];
    }

    public int getWeight(){
        int weight=0;
        for(int i=0; i<attemptsPrecision; i++)
            if(!attempts[i]) weight+=(i+1);
        return weight;
    }

    public int getMaxWeight(){
        return (1+attemptsPrecision)*attemptsPrecision/2;
    }

    public boolean updateAcquisition(){
        if(getWeight()<=((double)((100-acquisitionRate)*getMaxWeight()))/100) {
            makeAcquired();
            return true;
        }
        return false;
    }

    public String getTerm1() {
        return term1;
    }

    public void setTerm1(String term1) {
        this.term1 = term1;
    }

    public String getTerm2() {
        return term2;
    }

    public void setTerm2(String term2) {
        this.term2 = term2;
    }

    public boolean isAcquired() {
        return gotAcquired;
    }

    public void makeAcquired() {
        gotAcquired = true;
    }

    public void makeUnacquired() {
        gotAcquired = false;
        for(int a=0;a<attempts.length;a++){
            attempts[a]=false;
        }
    }

    @Override
    public boolean equals(Object obj){
        Term t = (Term) obj;
        return t.term2.equals(this.term2) && t.term1.equals(this.term1);
    }

    @Override
    public String toString(){
        String a = term1+" - "+term2+" <> "+((getMaxWeight()-getWeight())*100/getMaxWeight())+"%";
        if(isAcquired())
            a+=" (ACQUIRED)";
        return a;
    }

    @Override
    public int hashCode() {
        return Objects.hash(term1, term2);
    }
}
