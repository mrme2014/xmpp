package com.mrs.xmpp.im.service;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by mrs on 2017/5/19.
 */

public class ConnManger { /*cofig*/
    private int port = 5222;
    private String serverHost = "10.0.4.162";
    private String xmppDomain = "xmpp";
    private int readTimeOut = 20000;

    private static ConnManger manger;
    private XMPPTCPConnection connection;

    public static ConnManger getInstance() {
        if (manger == null)
            manger = new ConnManger();
        return manger;
    }

    public XMPPTCPConnection getImConn() {
        if (connection == null)
            creatXmppConnection();

        return connection;
    }

    private void creatXmppConnection() {
        XMPPTCPConnectionConfiguration cof = null;
        try {
            cof = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(JidCreate.from(xmppDomain).asDomainBareJid())
                    .setHostAddress(InetAddress.getByName(serverHost))
                    .setPort(port)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setConnectTimeout(readTimeOut)
                    .setDebuggerEnabled(true)
                    .build();
            connection = new XMPPTCPConnection(cof);
        } catch (XmppStringprepException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
