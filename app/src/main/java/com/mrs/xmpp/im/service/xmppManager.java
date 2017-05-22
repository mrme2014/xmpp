package com.mrs.xmpp.im.service;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by mrs on 2017/5/19.
 */

public class xmppManager {
    /*cofig*/
    private int port = 5222;
    private String serverHost = "10.0.4.162";
    private String xmppDomain = "xmpp";
    private int readTimeOut = 20000;

    private static xmppManager mXmppManager;

    private XMPPTCPConnection mXmppConnection;//连接管理

    private ChatManager mChatManager; //消息
    private MultiUserChatManager multiUserChatManager;//群聊管理者
    private FileTransferManager fileTransferManager;//文件发送接收管理制度


    public static xmppManager getInstance() {
        if (mXmppManager == null)
            synchronized (xmppManager.class) {
                if (mXmppManager == null)
                    mXmppManager = new xmppManager();
            }
        return mXmppManager;
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
                    .setSendPresence(false)
                    .build();
            mXmppConnection = new XMPPTCPConnection(cof);
        } catch (XmppStringprepException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public XMPPTCPConnection getImConnection() {
        if (mXmppConnection == null)
            creatXmppConnection();

        return mXmppConnection;
    }

    public ChatManager getChatManager() {
        if (mChatManager == null)
            mChatManager = ChatManager.getInstanceFor(mXmppConnection);
        return mChatManager;
    }

    public MultiUserChatManager getMultiUserChatManager() {
        if (multiUserChatManager == null)
            multiUserChatManager = MultiUserChatManager.getInstanceFor(mXmppConnection);
        return multiUserChatManager;
    }

    public FileTransferManager getFileTransferManager() {
        if (fileTransferManager == null)
            fileTransferManager = FileTransferManager.getInstanceFor(mXmppConnection);
        return fileTransferManager;
    }


}
