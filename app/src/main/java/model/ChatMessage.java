package model;

/**
 * Created by aaa on 15-4-24.
 */
public class ChatMessage {
    //发出去的
    public static final int SOURCE_TYPE_SEND=0;
    //收到的
    public static final int SOURCE_TYPE_RECEIVED=1;

    public static int getSourceTypeSend() {
        return SOURCE_TYPE_SEND;
    }

    public static int getSourceTypeReceived() {
        return SOURCE_TYPE_RECEIVED;
    }

    /**
     * 发消息的人
     */
    private static  String from;
    //消息发给谁
    private static String to;
    //消息内容
    private static String body;
    //接收/发送的时间
    private static long time;

    public static int getSourceType() {
        return SourceType;
    }

    public static void setSourceType(int sourcetype) {
        SourceType = sourcetype;
    }

    //消息的来源类型，代表是发出去的，还是收到的
    //可选值0：发出去的，1 收到的
    private static int SourceType;

    public static String getFrom() {
        return from;
    }

    public static void setFrom(String from) {
        ChatMessage.from = from;
    }

    public static String getTo() {
        return to;
    }

    public static void setTo(String to) {
        ChatMessage.to = to;
    }

    public static String getBody() {
        return body;
    }

    public static void setBody(String body) {
        ChatMessage.body = body;
    }

    public static long getTime() {
        return time;
    }

    public static void setTime(long time) {
        ChatMessage.time = time;
    }
}
