package com.example.aaa.liaoliao;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity implements ServiceConnection {
    private  MyService.ChatController controller;
    private EditText et1,et2;
    private TextView tv1,tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent=new Intent(this,MyService.class);
        startService(intent);
        //启动之后，再绑定一下
        bindService(intent,this,BIND_AUTO_CREATE);
        et1=(EditText)findViewById(R.id.et1);
        et2=(EditText)findViewById(R.id.et2);
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        controller=(MyService.ChatController)service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
           controller=null;
    }
    public void btnloginonclick(View v){
        if (controller != null) {
            String userName=et1.getText().toString().trim();
            String password=et1.getText().toString().trim();
            String userJID=controller.login(userName,password);
            if (userJID != null) {
                //登录成功
                Intent intent=new Intent(this,MainActivity.class);
                intent.putExtra("userJID",userJID);
                startActivity(intent);
                finish();
            }else{
                //提示错误
                Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
