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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements ServiceConnection, AdapterView.OnItemClickListener {
    private TextView tvJID;

    private ArrayList<String> data;
    private ArrayAdapter<String> adapter;
    //服务调用接口
    private MyService.ChatController controller;
    private List<RosterEntry> rosterEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvJID=(TextView)findViewById(R.id.main_user_id);
        Intent intent=getIntent();
        String userJID=intent.getStringExtra("userJID");
        tvJID.setText(userJID);
        data=new ArrayList<String>();
        ListView listView=(ListView)findViewById(R.id.main_roster_list);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
   //Listview点击
        listView.setOnItemClickListener(this);
        //绑定聊天服务
        Intent service=new Intent(this,MyService.class);
        bindService(service,this,BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        //每次显示的时候，及时获取联系人列表，进行刷新操作
        super.onResume();
        //获取联系人信息
        updateRoster();
    }

    private void updateRoster() {
        if(controller!=null) {
            rosterEntries = controller.getRosterEntries();
            data.clear();
            for (RosterEntry entry : rosterEntries) {
                String user = entry.getUser();
                data.add(user);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        //解除绑定
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        controller=(MyService.ChatController)service;
        updateRoster();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        controller=null;
    }
    //点击联系人，启动会话
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取联系人的JID
        RosterEntry entry=rosterEntries.get(position);
        String userJID=entry.getUser();
        //开启聊天会话界面
        Intent intent=new Intent(this,ChatActivity.class);
        intent.putExtra("userJID",userJID);
        startActivity(intent);

    }
}
