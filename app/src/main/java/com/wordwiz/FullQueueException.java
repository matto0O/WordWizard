package com.wordwiz;

import android.util.Log;

public class FullQueueException extends Exception {
    @Override
    public void printStackTrace(){
        Log.e("FullQueueException","FullQueueException");
    }
}
