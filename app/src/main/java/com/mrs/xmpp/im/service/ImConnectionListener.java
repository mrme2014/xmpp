package com.mrs.xmpp.im.service;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;

import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * Created by mrs on 2017/5/19.
 */

public class ImConnectionListener implements ConnectionListener {

    @Override
    public void connected(XMPPConnection connection) {
        Log.e("connected: ", "connected");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.e("authenticated: ", "authenticated--" + resumed);
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                Log.e("New message from ", from + ": " + message.getBody());
            }
        });
        chatManager.addOutgoingListener(new OutgoingChatMessageListener() {
            @Override
            public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
                Log.e("New message go ", to + ": " + message.getBody());
            }
        });

        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom("admin@xmpp");
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        Chat chat = chatManager.chatWith(jid);
        try {
            chat.send("hello ");
        } catch (SmackException.NotConnectedException |InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionClosed() {
        Log.e("connectionClosed: ", "connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.e("connClosedOnError: ", "connectionClosedOnError" + e.toString());
    }

    @Override
    public void reconnectionSuccessful() {
        Log.e("reconnectionSuc: ", "reconnectionSuccessful");
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.e("reconnectingIn: ", "reconnectingIn" + seconds);
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.e("reconnectionFailed: ", "reconnectionFailed" + e.toString());
    }
}