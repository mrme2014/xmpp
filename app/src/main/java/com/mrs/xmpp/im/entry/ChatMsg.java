package com.mrs.xmpp.im.entry;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by mrs on 2017/5/22.
 */

public class ChatMsg {
    public int messageType;
    public Message message;

    public ChatMsg(int messageType,Message message){
        this.message=message;
        this.message=message;
    }

}
