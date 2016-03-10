package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import java.util.logging.Handler;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/8.
 * Time: 00:47.
 */
public class AppStar extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start);
        handler.sendEmptyMessageDelayed(4, 1 * 1000);
    }

    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity(new Intent(AppStar.this, MainActivity.class));
            finish();
        }
    };


    void nothing(){

    }
}
