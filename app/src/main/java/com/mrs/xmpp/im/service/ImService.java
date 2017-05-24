package com.mrs.xmpp.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.mrs.xmpp.im.commlib.utils.SdUtils;
import com.mrs.xmpp.im.entry.ChatMsg;
import com.mrs.xmpp.im.im.MessageType;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by mrs on 2017/5/19.
 */

public class ImService extends Service implements ConnectionListener {
    private String userName = "syb_2017", userPwd = "123";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLoginThread();
        return START_STICKY;
    }

    private void startLoginThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                login();
            }
        }).start();
    }

    private void login() {
        XMPPTCPConnection imConn = xmppManager.getInstance().getImConnection();
        if (imConn == null)
            return;

        if (imConn.isConnected() || imConn.isAuthenticated()) {
            imConn.disconnect();
            Log.e("login: ", imConn.isConnected() + "---" + imConn.isAuthenticated());
        }
        try {
            imConn.addConnectionListener(this);
            imConn.connect();
            imConn.login(userName, userPwd);
        } catch (SmackException | IOException | InterruptedException | XMPPException e) {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.e("connected: ", "connected");
    }

    @Override
    public void authenticated(final XMPPConnection connection, boolean resumed) {
        Log.e("authenticated: ", "authenticated" + "--" + resumed);
        try {
            Stanza stanza = new Presence(Presence.Type.available);
            connection.sendStanza(stanza);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }


        FileTransferManager fileTransferManager = xmppManager.getInstance().getFileTransferManager();
        fileTransferManager.addFileTransferListener(new FileTransferListener() {
            @Override
            public void fileTransferRequest(FileTransferRequest request) {
                Log.e("fileTransferRequest: ", request.getFileName() + "---" + request.getMimeType() + "---" + request.getFileName() + "--" + request.getFileSize());
                try {
                    File reciveFile = SdUtils.getExternalStorageFile(request.getFileName());
                    request.accept().recieveFile(reciveFile);
                    ChatMsg fileMsg = new ChatMsg(MessageType.MESSAGE_IMG, request, reciveFile);
                    xmppManager.getInstance().dispatchMessages(fileMsg);
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("fileTransferRequest: ", request.getFileName() + "---" + request.getMimeType() + "---" + request.getFileName() + "--" + request.getFileSize());
            }
        });

        ChatManager chatManager = xmppManager.getInstance().getChatManager();
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                ChatMsg txtMsg = new ChatMsg(MessageType.MESSAGE_TXT, message);
                xmppManager.getInstance().dispatchMessages(txtMsg);
                Log.e("New message coming ", from + ": " + message.getBody());
            }
        });

        chatManager.addOutgoingListener(new OutgoingChatMessageListener() {
            @Override
            public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
                Log.e("New message go ", to + ": " + message.getBody());

            }

        });

    }

    @Override
    public void connectionClosed() {
        Log.e("connectionClosed: ", "connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.e("connectionClosedError: ", e.toString());
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
