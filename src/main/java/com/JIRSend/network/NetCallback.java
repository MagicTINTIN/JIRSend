package com.JIRSend.network;

import java.net.InetAddress;

public abstract class NetCallback {
    public abstract void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast);
}
