package com.gestionProjet.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.gestionProjet.ui.Log;

/**
 * ioUDP is a package that commmunicates in UDP with recovery of losses
 * 
 * Communication template
 * 
 * -JIRSENDPACK>$TYPE$<$TIMESTAMP$|$PAYLOAD$
 * $TYPE$ = (A:Ack|B:Broadcast|M:Message)
 */
public class NetworkIO {
    public static final String APP_HEADER = "-JIRSENDPACKET>";
    public static final int RECV_PORT = 24671;
    // public static final int sendPort = 24672;
    public static final int TIMEOUT = 2000; // milliseconds
    public static final int MAX_TRIES = 10;

    private final UDPCallback onReceive = new UDPCallback();
    private final UDPReceiver rcv;
    private final UDPSender snd;
    private final NetCallback callback;

    public NetworkIO(NetCallback callback) {
        this.rcv = new UDPReceiver(RECV_PORT, onReceive);
        this.snd = new UDPSender();
        this.rcv.start();
        this.callback = callback;
    }

    public boolean send(String destAddress, String value) {
        return snd.sendAndWaitForAck(destAddress, RECV_PORT, value, TIMEOUT, MAX_TRIES);
    }

    protected class UDPCallback extends NetCallback {

        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value) {
            String[] messageParts = value.split("\\|", 2);
            // if the message is not a valid request
            if (!value.startsWith(APP_HEADER) || messageParts.length != 2 || messageParts[0].split("\\<", 2).length != 2) {
                Log.l("Received an unknown message '" + value + "' sent by " + senderAddress.getHostAddress() + ":" + senderPort + "");
                return;
            }
            // if we receive an ack we ignore it
            if (messageParts[0].startsWith(APP_HEADER + "A<"))
                return;
            // if we receive a broadcasted message
            if (messageParts[0].startsWith(APP_HEADER + "B<")) {
                // TODO: handle broadcasted messages
                Log.e("CAS DE MESSAGE NON IMPLEMENTÉ");
                return;
            }
            sendAck(senderAddress, senderPort, messageParts[0]);
            callback.execute(senderAddress, senderPort, messageParts[1]);
        }

    }

    private void sendAck(InetAddress sAddress, int sPort, String rcvdHeader) {
        String sentTimestamp = rcvdHeader.split("\\<", 2)[1];

        try {
            String ackMsg = APP_HEADER + "A<" + sentTimestamp + "|" + Inet4Address.getLocalHost().getHostAddress();
            snd.send(sAddress, sPort, ackMsg);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}