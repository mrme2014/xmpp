package com.mrs.xmpp.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
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


/**
 * Created by mrs on 2017/5/19.
 */

public class ImService extends Service implements ConnectionListener {
    private String userName = "syb", userPwd = "123";

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
        if (imConn == null || imConn.isConnected())
            return;

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
        try {
            Stanza stanza = new Presence(Presence.Type.available);
            connection.sendStanza(stanza);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("authenticated: ", "authenticated--" + resumed);
        EntityFullJid jidFull = null;
        try {
            jidFull = JidCreate.entityFullFrom("syb_2017@xmpp/Spark");
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }


        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        FileTransferManager fileTransferManager = FileTransferManager.getInstanceFor(connection);
        OutgoingFileTransfer fileTransfer = fileTransferManager.createOutgoingFileTransfer(jidFull);
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/download", "1.png");
            Log.e("authenticated: ", "file.exists" + file.exists());
            fileTransfer.sendFile(file, null);
        } catch (SmackException e) {
            e.printStackTrace();
        }
        fileTransferManager.addFileTransferListener(new FileTransferListener() {
            @Override
            public void fileTransferRequest(FileTransferRequest request) {
                IncomingFileTransfer accept = request.accept();
                try {
                    accept.recieveFile(new File(Environment.getExternalStorageDirectory(), request.getFileName()));
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
                Log.e("New message from ", from + ": " + message.getBody());
                OfflineMessageManager offlineManager = new OfflineMessageManager(connection);
                try {
                    offlineManager.deleteMessages();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        chatManager.addOutgoingListener(new OutgoingChatMessageListener() {
            @Override
            public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
                Log.e("New message go ", to + ": " + message.getBody());
            }
        });

        EntityBareJid jid = (EntityBareJid) jidFull.asBareJid();
        Chat chat = chatManager.chatWith(jid);
        Message message = new Message(jid, Message.Type.chat);

        try {
            chat.send(message);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionClosed() {
        Log.e("connectionClosed: ", "connectionClosed");
        startLoginThread();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.e("connectionClosedError: ", e.toString());
        startLoginThread();
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
