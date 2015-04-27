package com.example.aaa.liaoliao;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.ArrayList;
import java.util.LinkedList;

import model.ChatMessage;


public class ChatActivity extends ActionBarActivity implements ServiceConnection, MessageListener, PacketListener {
    private String userJID,thread,body;
    private Chat chat;
    private EditText txtcontent;

    //聊天界面，从其他activity传递的参数：userJID，代表需要聊天的对象
    //从服务获取的Binder，用于消息的发送
    private MyService.ChatController controller;
    private ArrayList<ChatMessage> chatMessages;
    private ChatMessageAdapter adapter;
    private int start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //接收目标联系人
        Intent intent=getIntent();
        userJID=intent.getStringExtra("userJID");
        //在activity上面显示标题
        userJID=intent.getStringExtra("userJID");
        setTitle(userJID);
        //获取主题，可能为空，因为自己点击进入ChatActivity时，是没有的
        thread=intent.getStringExtra("thread");
        //只有收到消息的时候，才会有
        body=intent.getStringExtra("body");
        //绑定服务，用于发送消息
        Intent service = new Intent(this,MyService.class);
        //参数1：Intent 代表服务
        //参数2：服务绑定的回调接口
        //参数3：
        bindService(service,this,BIND_AUTO_CREATE);
        txtcontent=(EditText)findViewById(R.id.chat_et1);
        //listview显示，实现聊天的样式，左侧都是收到的消息，右侧都是发出的消息
        ListView listview=(ListView)findViewById(R.id.main_listview);
        chatMessages = new ArrayList<ChatMessage>();
        adapter = new ChatMessageAdapter(this,chatMessages);
        listview.setAdapter(adapter);



    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        controller=(MyService.ChatController)service;
        //1.绑定成功之后，进行聊天会话的创建
        chat=controller.openChat(userJID,null,this);
        //2.controller向内部的XMPPTCPConnection添加一个PacketListener进行消息检查
        controller.addPacketListener(this);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        //删除/停止 监听数据包的内容
        controller.removePacketListener(this);
        if (chat != null) {
            chat.close();
        }
        controller=null;
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }
///////////////////////////////////////////////////////
//点击事件代码部分
    //发送按钮点击事件
    public void btnSendOnClick(View v){
        String content=txtcontent.getText().toString();
        if (chat != null) {
            try {
                chat.sendMessage(content);
                //创建消息实体，显示在listview上面
                ChatMessage msg=new ChatMessage();
                //设置显示的文本
                msg.setBody(content);
                //类型是发送的类型
                msg.setSourceType(ChatMessage.SOURCE_TYPE_SEND);
                chatMessages.add(msg);
                adapter.notifyDataSetChanged();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }
    //点击表情事件
    public void btnFaceOnClick(View v){
        //EditText.setText();
        //1.获取输入框现有的文本内容
        Editable text=txtcontent.getText();
        //2.准备一个字符串：这个字符串类型不再是Stirng而是SpannableString
        SpannableStringBuilder sb=new SpannableStringBuilder(text);
        int id=v.getId();
        //添加表情
        //1.表情的文本显示（例如【偷笑】）
        //第一个参数 what就是各种span对象，也就是需要给字符串设置的样式
        //第二个参数 设置的字符串的起始位置例如“I Love Android”如果给LOVE设置样式，那么起始2
        //第三个参数 通常可以指定为 起始位置+需要设置样式的字符长度，因为第四个参数直接影响到这个值的设置
        //第四个参数 代表 第二个参数和第三个参数的使用方式
        //通常第四个参数 采用SPAN_INCLUSIVE_EXCLUSIVE,意思是包含起始位置，不包含结束位置，最终的范围是从起始位置到结束位置-1
        switch (id){
            case R.id.chat_image1:
                //获取即将添加的字符串的起始位置
                sb.append("[微笑]");
                start = sb.length();
                ImageSpan face1=new ImageSpan(this,R.drawable.face1);
                sb.setSpan(face1, start, start +4,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            case R.id.chat_image2:
                sb.append("[撇嘴]");
                start = sb.length();

                ImageSpan face2=new ImageSpan(this,R.drawable.face2);
                sb.setSpan(face2, start, start +4,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            case R.id.chat_image3:
                sb.append("[色]");
                start = sb.length();

                ImageSpan face3=new ImageSpan(this,R.drawable.face3);
                sb.setSpan(face3, start, start +3,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            case R.id.chat_image4:
                sb.append("[流汗]");
                start = sb.length();
                ImageSpan face4=new ImageSpan(this,R.drawable.face4);
                sb.setSpan(face4, start, start +4,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
        }
        txtcontent.setText(sb);
        //将光标移到文本的最后
        txtcontent.setSelection(sb.length());

    }

///////////////////////////////////////////////////////
    @Override
    public void processMessage(Chat chat, Message message) {
        //处理消息的发送与接收
        String from=message.getFrom();
        String to=message.getTo();
        String body=message.getBody();
        //显示信息，用于判断，发送出去的消息，方法是否回调
        //接收的消息能否取到
        Log.d("ChatActivity","message form"+from+"to"+to+"body"+body);
    }
    //接收消息，显示在listview
    @Override
    public void processPacket(Packet packet) throws SmackException.NotConnectedException {
        if (packet instanceof Message) {
            Message msg=(Message)packet;
            //1.检查消息的来源是否是当前的会话的人
            String from=msg.getFrom();
            //因为PacketLister会接收所有的消息
            //对于会话界面而言，就需要检查消息的来源，是否当前的聊天人
            if (from.startsWith(userJID)) {
                ChatMessage chatMessage=new ChatMessage();
                chatMessage.setBody(msg.getBody());
                chatMessage.setFrom(from);
                chatMessage.setTo(msg.getTo());
                chatMessage.setSourceType(ChatMessage.SOURCE_TYPE_RECEIVED);
                chatMessage.setTime(System.currentTimeMillis());
                //添加消息，更新listview
                chatMessages.add(chatMessage);

                //因为processPacket 执行在子线程中
                //Listview 更新应该主线程更新
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }


        }
    }
}
