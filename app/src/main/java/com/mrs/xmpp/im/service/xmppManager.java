package com.mrs.xmpp.im.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mrs.xmpp.im.entry.ChatMsg;
import com.mrs.xmpp.im.im.MsgNotificationInterface;

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
import java.util.ArrayList;
import java.util.List;

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
    private Handler uiHandler;


    private List<MsgNotificationInterface> interfaceList = new ArrayList<>();

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

    /*单聊管理者*/
    public ChatManager getChatManager() {
        if (mChatManager == null)
            mChatManager = ChatManager.getInstanceFor(mXmppConnection);
        return mChatManager;
    }

    /*群聊管理者*/
    public MultiUserChatManager getMultiUserChatManager() {
        if (multiUserChatManager == null)
            multiUserChatManager = MultiUserChatManager.getInstanceFor(mXmppConnection);
        return multiUserChatManager;
    }

    /*文件消息管理者*/
    public FileTransferManager getFileTransferManager() {
        if (fileTransferManager == null)
            fileTransferManager = FileTransferManager.getInstanceFor(mXmppConnection);
        return fileTransferManager;
    }

    /*添加消息监听MsgNotificationInterface*/
    public void addMsgNotificationListener(MsgNotificationInterface msgInterface) {
        if (msgInterface == null)
            return;
        if (uiHandler == null)
            uiHandler = new Handler(Looper.getMainLooper());
        interfaceList.add(msgInterface);
    }

    /*移除消息监听MsgNotificationInterface*/
    public void reMoveNotificationListener(MsgNotificationInterface msgInterface) {
        if (msgInterface == null)
            return;
        interfaceList.remove(msgInterface);
    }

    /*Imservice 接收到消息后会分发接受者*/
    public void dispatchMessages(final ChatMsg msg) {
        int size = interfaceList.size();
        for (int i = 0; i < size; i++) {
            final int finalI = i;
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    interfaceList.get(finalI).onReciveMsgText(msg);
                }
            });
        }
    }

    public void sendMsg(ChatMsg msg) {

    }
}
