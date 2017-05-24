package com.mrs.xmpp.im.entry;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;

import java.io.File;

/**
 * Created by mrs on 2017/5/22.
 */

public class ChatMsg {
    public String fromJid;
    public int messageType;
    public Message message;

    public FileTransferRequest request;
    public File reciveFile;

    public ChatMsg(int messageType, Message message) {
        this.message = message;
        this.message = message;

        if (message.getFrom() != null)
            fromJid = message.getFrom().getLocalpartOrNull().toString();
    }

    public ChatMsg(int messageType, FileTransferRequest request, File file) {
        this(messageType, request);
        this.reciveFile = file;
        if (request!=null&&request.getRequestor() != null)
            fromJid = request.getRequestor().getLocalpartOrNull().toString();
    }

    public ChatMsg(int messageType, FileTransferRequest request) {
        this.messageType = messageType;
        this.request = request;

        if (request!=null&&request.getRequestor() != null)
            fromJid = request.getRequestor().getLocalpartOrNull().toString();
    }
}
