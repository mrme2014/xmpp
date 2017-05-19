package com.mrs.xmpp.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by mrs on 2017/5/19.
 */

public class ImService extends Service {
    private String userName = "syb", userPwd = "123";
    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                login();
            }
        }).start();
    }

    private void login() {
        XMPPTCPConnection imConn = ConnManger.getInstance().getImConn();
        if (imConn == null)
            return;

        try {
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("call me");
            imConn.addConnectionListener(new ImConnectionListener());
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

}
