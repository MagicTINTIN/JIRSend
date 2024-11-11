package com.JIRSend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.JIRSend.view.cli.Log;

public class TCPClient {
    public final String hostname;
    public final int port;
    private Socket socket;
    private PrintWriter sender;
    private BufferedReader receiver;
    private NetCallback callback;
    private MessageHandlerThread thread;

    public TCPClient(String hostname, int port, NetCallback callback) {
        this.hostname = hostname;
        this.port = port;
        this.callback = callback;
        Log.l("Creating socket for "+hostname+":"+port,Log.DEBUG);
        try {
            socket = new Socket(hostname, port);
            sender = new PrintWriter(socket.getOutputStream(), true);
            receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            thread = new MessageHandlerThread();
            thread.start();
        } catch (Exception e) {
            sender = null;
            receiver = null;
            thread = null;
            Log.e("Error at socket creation (" + hostname + ":" + port + "): " + e);
        }
        Log.l("Socket created "+hostname+":"+port,Log.DEBUG);
    }

    protected TCPClient(Socket socket, NetCallback callback) {
        this.hostname = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.callback = callback;
        this.socket = socket;
        try {
            sender = new PrintWriter(socket.getOutputStream(), true);
            receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            thread = new MessageHandlerThread();
            thread.start();
        } catch (IOException e) {
            sender = null;
            receiver = null;
            Log.e("Failed to create socket IO (" + hostname + ":" + port + ") " + e);
        }
    }

    public boolean send(String string) {
        sender.println(string);
        return true;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("Could not close the socket to " + hostname + ":" + port);
        }
    }

    private class MessageHandlerThread extends Thread {
        @Override
        public void run() {
            this.setName(hostname + "-ClientHandler");
            while (true) {
                // TODO meilleure condition d'arret ^^' (or not)
                try {
                    String string = receiver.readLine();
                    if (string == null) {
                        Log.l("Connection ended by "+hostname+":"+port);
                        break;
                    }
                    callback.execute(socket.getInetAddress(), port, string, false, false);
                } catch (IOException e) {
                    Log.l("Msg receiver closed for " + hostname + ":" + port);
                    break;
                }
            }
        }

    }
}
