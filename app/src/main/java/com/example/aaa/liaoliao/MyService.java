package com.example.aaa.liaoliao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyService extends Service {
    //1.创建XMPP连接    HttpURLConnection
    //                XMPPTCPConnection
    private static XMPPTCPConnection conn;

    @Override
    public void onCreate() {
        super.onCreate();
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
            conn=null;
        }
        conn=new XMPPTCPConnection("10.0.154.195");

    }

    public class ChatController extends Binder{
        //停止监听器，不在接收消息（对于外部界面而言）
        public void removePacketListener(PacketListener listener){
            if (listener != null) {
                if (conn != null) {
                    conn.removePacketListener(listener);

                }
            }
        }
        //添加监听器接口(外部界面接收消息的时候，设置)
        public void addPacketListener(PacketListener listener){
            if (listener != null) {
                if (conn != null) {
                    conn.addPacketListener(listener,new MessageTypeFilter(org.jivesoftware.smack.packet.Message.Type.chat));

                }
            }
        }
        //用于开启聊天会话，主要在ChatActivity使用，用于发送和接收消息
        //target，需要和谁聊天
        //listener 用来监听消息
        //返回Chat对象，可以通过Chat调用sendMessage发送消息
        public Chat openChat(String target, String thread, MessageListener listener){
               Chat ret=null;
            if (target != null) {
                if (conn != null) {
                    if (conn.isAuthenticated()) {
                        //已经登录的情况　
                        ChatManager manger=ChatManager.getInstanceFor(conn);
                        //创建聊天会话
                        ret=manger.createChat(target,thread,listener);

                    }
                }
            }
            return ret;
        }
        //给外部的LoginActivity提供直接调用登录的功能
        public String login(String userName,String password){
            String ret=null;
            if (userName != null&&password!=null) {
                if (conn != null) {
                    //
                    boolean isAuth=conn.isAuthenticated();
                    try {
                        conn.login(userName,password);
                        ret=conn.getUser();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return ret;
        }
        public List<RosterEntry> getRosterEntries(){
            List<RosterEntry>ret=null;
            if (conn != null) {
                if (conn.isAuthenticated()) {
                    Roster roster=conn.getRoster();
                    if (roster != null) {
                        Collection<RosterEntry>entries=roster.getEntries();
                        ret=new LinkedList<RosterEntry>();
                        ret.addAll(entries);

                    }
                }
            }
            return ret;
        }
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return new ChatController();

    }
    private ChatThread chatThread;

    /**
     * 服务的启动
     * @param intent
     * @param flags
     * @param startId
     * @return
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //启动线程
        if (chatThread == null) {
            chatThread = new ChatThread();
            chatThread.start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(chatThread!=null)
        {
            chatThread.StopThread();
            chatThread=null;
        }
        super.onDestroy();
    }

    /**
     * 实际聊天的线程部分
     */

    class ChatThread extends Thread{


        //标志线程
        private boolean running;

        public void StopThread()
        {
            running=false;
        }

        @Override
        public void run() {
            running=true;
            //连接服务器

            //进行实际的连接服务器操作
            try {
                //Smark API 当中，大部分方法发生错误的时候
                //直接抛异常
                conn.connect();
    /*            //账号注册
                //获取账号管理器，进行注册操作
                AccountManager accountManager=AccountManager.getInstance(conn);
               *//* //注册账号
                try {
                    accountManager.createAccount("ll23", "ll23");
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                //登陆账号，上一个注册成功的内容
                //登陆都是在连接打开之后，因此登录方法在XMPPTCPConnection
                conn.login("ll23","ll23");

                //获取用户基本的信息，检查是否登陆成功！
                //返回已经登录的用户 JID（也就是发送消息时，收信人格式）
                String user=conn.getUser();
                Log.d("CharThread","Login user"+user);
                //获取联系人列表
                //getRoster() 会自动从服务器获取当前用户的联系人列表,返回Roster对象
                //所有的添加修改的操作，都会直接影响到账号实际的联系人内容
                Roster roster=conn.getRoster();
                int entityCount=roster.getEntryCount();
                Collection<RosterEntry> entries=roster.getEntries();

                //遍历每一个联系人信息
                for (RosterEntry entry : entries) {
                    //昵称
                    String name=entry.getName();
                    //收发信息时收到的内容
                    String user1=entry.getUser();
                    RosterPacket.ItemStatus status = entry.getStatus();
                    Log.d("ChatThread","Roster:"+user1);
                }
*//*
                //创建联系人
                //第一个参数 JID形式的 也就是用户名@域名
                //第二个参数 添加联系人时的备注名称
                //第三个参数 属于哪组
            //    roster.createEntry("vhly@10.0.154.195","Zhang Sir",null);
                //接收消息
                //向连接中添加数据包的监听器，当服务器给客户端发送消息的时候
                //XMPPTCPConnection 会自动调用PackerListener 的回调
                //两个参数：第一个：数据包监听器，用于处理数据
                //          第二个：监听器要监听哪些类型的数据
                //          因为conn内部所有的操作都是数据包，例如获取联系人*/
                PacketListener packetListener=new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                        //处理消息类型的数据包
                        //因为Message类型继承了Packet 所以检查是否是Message
                        if (packet instanceof org.jivesoftware.smack.packet.Message) {
                            org.jivesoftware.smack.packet.Message msg=(org.jivesoftware.smack.packet.Message)packet;
                            //消息内容
                            String body=msg.getBody();
                            //回话主题
                            String subject=msg.getSubject();
                            String form=msg.getFrom();
                            String to=msg.getTo();
                            //聊天会话的主题，通过这个主题，就可以确定另一个发送者创建的Chat 对象
                            //这个thread 类似于对讲机之间的联系
                            String thread=msg.getThread();
                            Log.d("Chatthread","has a mensage form"+form);
                            //当收到消息，就模拟一下QQ的通知栏信息
                            NotificationManagerCompat mangercompat=NotificationManagerCompat.from(MyService.this);
                            NotificationCompat.Builder builder=new NotificationCompat.Builder(MyService.this);
                            builder.setContentTitle("您有新消息");
                            builder.setContentText(body);
                            //设置点击之后，直接进入聊天
                            Intent chatIntent=new Intent(getApplicationContext(),ChatActivity.class);
                            chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            //使用userJID给谁回复
                            chatIntent.putExtra("userJID",form);
                            //主题标示，进行两个账号之间的联系
                            chatIntent.putExtra("thread",thread);
                            //内容
                            chatIntent.putExtra("body",body);

                            //如果应用启动了，并且ChatActivity在任务栈中，那么直接启动
                            //如果没有启动，就开一个新的栈
                            PendingIntent pendingIntent=PendingIntent.getActivity(MyService.this,
                                    998,
                                    chatIntent,
                                    PendingIntent.FLAG_ONE_SHOT);

                            builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.drawable.ic_launcher);
                            Notification nofition=builder.build();
                            mangercompat.notify((int)(System.currentTimeMillis()),nofition);

                        }
                    }
                };

                //在开始会话之前，进行PackageListener的位置;
                //conn.addPacketListener(packetListener,new MessageTypeFilter(org.jivesoftware.smack.packet.Message.Type.chat));
                //ChatManager chaatmangger =ChatManager.getInstanceFor(conn);
                //创建对话，需要给其他人发消息
               /* if ((entries == null)&&!entries.isEmpty()) {
                    Iterator<RosterEntry>iterator=entries.iterator();
                    RosterEntry rosterEntry=iterator.next();
                    String jid=rosterEntry.getUser();
                    //创建聊天会话，有一个chat的对象，进行会话的管理
                    //当使用chat进行发送消息的时候，会自动的，通过底层的XMPP

                    String jid1="";
                    Chat chat=chaatmangger.createChat(jid,new MessageListener() {
                        @Override
                        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                            //处理
                            String body=message.getBody();
                            //回话主题
                            String subject=message.getSubject();
                            String form=message.getFrom();
                            String to=message.getTo();
                            Log.d("Chatthread","has a mensage form");
                        }
                    });
                    chat.sendMessage("Hello world");
                }*/

                //进行循环，等待消息内容，以及要进行发送处理
                while (running)
                {
                    Thread.sleep(300);
                }

            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        //关闭连接
                        conn.disconnect();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    conn=null;
                }
            }

        }
    }
}