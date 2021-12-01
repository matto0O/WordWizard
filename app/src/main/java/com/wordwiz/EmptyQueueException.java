package com.wordwiz;

import android.util.Log;

public class EmptyQueueException extends Exception {
    @Override
    public void printStackTrace(){
        Log.e("EmptyQueueException","EmptyQueueException");
    }
}
