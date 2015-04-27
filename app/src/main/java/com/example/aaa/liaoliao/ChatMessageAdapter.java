package com.example.aaa.liaoliao;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import model.ChatMessage;

/*
*
        * Created by aaa on 15-4-24.
//主要用于聊天信息的展现，分为左侧和右侧两部分
public class ChatMessageAdapter extends BaseAdapter {
    //当前聊天列表
    private List<ChatMessage>messages;
    private Context context;
    private LayoutInflater li;

    public ChatMessageAdapter(List<ChatMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
        li=LayoutInflater.from(context);
    }

    //获取所有的数据个数
    @Override
    public int getCount() {
        int ret=0;
        if (messages != null) {
            ret=messages.size();
        }
        return ret;
    }
    //获取指定索引的实际数据对象
    @Override
    public Object getItem(int position) {
        Object ret=null;
        if (messages != null) {
            ret=messages.get(position);
        }
        return ret;
    }
    //获取数据的ID，对于CursorAdapter这个方法 返回的是数据库记录的ID
    //另外一个应用就是Listview 设置为可以多选的情况
    @Override
    public long getItemId(int position) {
        return 0;
    }

    */
/**
 * 告诉Listview内部的布局一共有多少种
 * @return
 *//*

    @Override
    public int getViewTypeCount() {
        //对于2，主要是聊天，主要有发送的和接收的两种布局
        //左侧接收，右侧发送，类似微信
        return 2;
    }
    //每次listview显示Item的时候，都先问一下Adapter指定位置的Item是什么类型
    //注意：返回的数值必须是从 0 到 getviewtypecount返回值减一
    //根据位置，获取数据的类型
    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage=messages.get(position);
        int ret=0;
        int sourceType=chatMessage.getSourcetype();
        if (sourceType == ChatMessage.SOURCE_TYPE_SEND) {
            ret=1;
        }else if (sourceType == ChatMessage.SOURCE_TYPE_RECEIVED) {
            ret=0;
        }
        return ret;
    }

    */
/**
 *
 * @param position
 * @param convertView
 * @param parent
 * @return
 *//*

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret=null;
        //1.获取消息，判断消息的类型，根据类型来进行内容的设置
        ChatMessage chatMessage=messages.get(position);
        //获取来源类型，进行不同 布局的加载与显示
        int sourceType=chatMessage.getSourcetype();
        if (sourceType == ChatMessage.SOURCE_TYPE_RECEIVED) {
            if (convertView != null) {
                ret=convertView;
            }else{
                ret=li.inflate(R.layout.item_chat_left,parent,false);
            }
            TextView tv=(TextView)ret.findViewById(R.id.chat_message);
            tv.setText(chatMessage.getBody());

        }else if(sourceType==ChatMessage.SOURCE_TYPE_SEND){
            if (convertView != null) {
                ret=convertView;
            }else{
                ret=li.inflate(R.layout.item_chat_right,parent,false);
            }
            TextView tv=(TextView)ret.findViewById(R.id.chat_message_right);
            tv.setText(chatMessage.getBody());



        }
        return ret;
    }
}
*/
/**
 * 主要用于聊天信息的展现，分为左侧和右侧两部分
 */
public class ChatMessageAdapter extends BaseAdapter{
    //当前聊天信息的列表
    private List<ChatMessage> messages;
    private Context context;
    private LayoutInflater inflater;

    public ChatMessageAdapter(Context context,List<ChatMessage> messages) {
        this.messages = messages;
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    /**
     * 获取所有的数据个数
     * @return
     */
    @Override
    public int getCount() {
        int ret=0;
        if (messages != null) {
            ret=messages.size();
        }
        return ret;
    }

    /**
     * 获取指定索引的实际数据对象
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        Object ret=null;
        if (messages != null) {
            ret=messages.get(position);
        }
        return ret;
    }

    /**
     * 获取的数据的ID，对于CursorAdapter这个方法返回的是数据记录的ID
     * 另一种应用就是ListView 设置为可以多选的情况。
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 告诉ListView内部的布局一共有多少中。
     * @return
     */

    @Override
    public int getViewTypeCount() {
        //对于2 主要是因为聊天主要有发送的和接收的两种布局
        //左侧接收，右侧发送的。
        return 2;
    }

    /**
     * 每次ListView显示Item的时候，都先问一下Adapter指定位置的Item是什么类型
     *
     * @param position 根据位置获取数据的类型。
     * @return int 注意：返回的数值必须是从0到getViewTypeCount-1
     */
    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = messages.get(position);
        int ret=0;
        int sourceType = chatMessage.getSourceType();

        //对于发送出去的消息，显示在右侧，指定返回类型为1
        if(sourceType==ChatMessage.SOURCE_TYPE_SEND){
            ret=1;
        }
        else if(sourceType==ChatMessage.SOURCE_TYPE_RECEIVED)
        {
            //对于收到 的消息，显示在左侧，指定返回值为0；
            ret=0;
        }
        return ret;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret=null;

        //TODO 1.获取消息，判断消息的类型，根据类型来进行内容的设置
        ChatMessage chatMessage = messages.get(position);
        //获取来源类型,根据来源类型，进行不同的布局加载与显示
        int sourceType = chatMessage.getSourceType();
        if(sourceType==ChatMessage.SOURCE_TYPE_RECEIVED)
        {
            //TODO 收到 的显示在左侧

            if(convertView!=null)
            {
                ret=convertView;

            }
            else
            {
                //layoutInflater
                ret=inflater.inflate(R.layout.item_chat_left,parent,false);
            }
            //TODO 显示消息内容
            //左侧的TextView
            TextView textMessage = (TextView) ret.findViewById(R.id.chat_message);
            textMessage.setText(chatMessage.getBody());
            //聊天消息表情
            //1.找到字符串中所有的【】包含的内容
            String body=chatMessage.getBody();
            SpannableString str=new SpannableString(body);
        }
        else if(sourceType==ChatMessage.SOURCE_TYPE_SEND)
        {
            //TODO 发送的显示在右侧

            if (convertView != null) {
                ret=convertView;
            }
            else
            {
                ret=inflater.inflate(R.layout.item_chat_right,parent,false);
            }

            //TODO 显示消息（右侧的）
            TextView textMessage= (TextView) ret.findViewById(R.id.chat_message_right);
            textMessage.setText(chatMessage.getBody());

        }

        return ret;
    }
}