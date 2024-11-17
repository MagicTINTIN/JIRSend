package com.JIRSend.controller;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.net.SocketException;
import java.util.ArrayList;

import com.JIRSend.model.Message;
import com.JIRSend.model.network.Net;
import com.JIRSend.model.user.BaseUser;
import com.JIRSend.model.user.Conversation;
import com.JIRSend.model.user.User;
import com.JIRSend.model.user.UserEntry;
import com.JIRSend.view.MainAbstractView;
import com.JIRSend.view.cli.CliTools;
import com.JIRSend.view.cli.MainCLI;
import com.JIRSend.view.gui.MainGUI;

public class MainController {
    private String controllerName;

    // View objects
    private MainAbstractView view;

    // Model objects
    protected BaseUser user;
    protected Net net;

    // Pipes
    public static Pipe<String> lostContact = new Pipe<>("Lost Contact");
    public static Pipe<String> localUsernameChange = new Pipe<>("Local Username Changed");
    public static Pipe<String> contactsChange = new Pipe<>("Contacts Changed");
    public static Pipe<Message> sendMessage = new Pipe<>("Sending Message");
    public static Pipe<Message> messageReceived = new Pipe<>("Message received");

    public MainController(String name, boolean usingGUI) throws SocketException {
        this.controllerName = name;
        if (usingGUI)
            if (!GraphicsEnvironment.isHeadless())
                this.view = new MainGUI(this);
            else {
                CliTools.printBigError("Not X11 display found. Starting Command Line Interface instead...");
                this.view = new MainCLI(this);
            }
        else
            this.view = new MainCLI(this);
        this.user = new User(this);
        // start UI when Net is setup
        this.net = new Net(this, () -> {
            startUI();
        });
    }

    public MainController(boolean usingGUI) throws SocketException {
        this("JIRSend Main", usingGUI);
    }

    public void startUI() {
        try {
            this.view.open();
        } catch (HeadlessException e) {
            System.err.println("Could not find X11 display. Opening a Command Line Interface instead. Consider using --cli option.");
            this.view = new MainCLI(this);
            this.view.open();
        }
    }

    public String getName() {
        return controllerName;
    }

    public void stopNet() {
        this.net.stop();
    }

    /**
     * Will stop the app
     */
    public void stoppingApp() {
        net.sendGoingOfflineMessage();
        stopNet();
        System.exit(0);
    }

    //////// VIEW
    /// Setters
    public String changeUsername(String username) {
        String res = this.net.usernameAvailable(username);
        if (res.equals(Net.okString)) {
            this.user.setUsername(username);
            return Net.okString;
        }
        return res;
    }

    /// Getters
    public String getUsername() {
        return this.user.getUsername();
    }

    public ArrayList<UserEntry> getContacts() {
        return net.getUserEntries();
    }

    public int getNumberConnected() {
        int connected = 0;
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.online)
                connected++;
        }
        return connected;
    }

    public boolean isConnected(String name) {
        boolean connected = false;
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.username == name) {
                connected = ue.online;
                break;
            }
        }
        return connected;
    }

    public ArrayList<String> getConnectedUsernames() {
        ArrayList<String> connected = new ArrayList<>();
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.online)
                connected.add(ue.username);
        }
        return connected;
    }

    public String getIPFromUsername(String username) {
        if (net == null)
            return null;
        return net.getIpFromUsername(username);
    }

    public String getUsernameFromIP(String ip) {
        UserEntry ue = net.getHashMap().get(ip);
        if (ue == null)
            return null;
        return ue.username;
    }

    public String getConversationName() {
        return user.getCurrentConversationName();
    }

    public Conversation getConversation() {
        String convIp = getConversationIP();
        if (convIp == null)
            return null;
        return user.getConversation(convIp);
    }

    public String getConversationIP(String convName) {
        if (convName == null)
            return null;
        return net.getIpFromUsername(convName);
    }

    public String getConversationIP() {
        return getConversationIP(user.getCurrentConversationName());
    }

    public int getConversationUnreadNumber(String name) {
        String ip = getConversationIP(name);
        if (ip == null)
            return 0;
        return this.user.getConversationUnreadNb(ip);
    }

    public Conversation getConversation(String name) {
        if (name == null)
            return null;
        String ip = net.getIpFromUsername(name);
        if (ip == null)
            return null;
        Conversation conv = user.getConversation(ip);
        if (conv != null)
            user.setCurrentConversationName(name);

        return conv;
    }

    public int getTotalUnread() {
        return user.getTotalUnread();
    }

    public void markConversationRead(String name) {
        String ip = net.getIpFromUsername(name);
        if (ip == null)
            return;
        user.markConversationRead(ip);
    }
}
