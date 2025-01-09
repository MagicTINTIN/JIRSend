package com.JIRSendMod;

import javax.swing.ImageIcon;
import com.JIRSendAPI.*;

public class Clavardons4JS implements JIRSendMod {

    private ModController controller;
    private String username;
    static public final JIRSendModInformation MOD_INFO = new JIRSendModInformation(
        "clavardons4js",
        "Clavardons for JIRSend",
        "A mod to talk to JP from JIRSend",
        "Atsuyo64", // author
        1, // interface version
        0, // mod version
        new ImageIcon(Clavardons4JS.class.getResource("/assets/clavardons.png"))
    );

    @Override
    public void initialize(ModController controller) {
        this.controller = controller;
        System.out.println("Clavardons4JS initialized. Welcome!");
    }

    @Override
    public void stop() {
        System.out.println("Clavardons4JS is stopping. Goodbye!");
    }

    @Override
    public JIRSendModInformation getModInformation() {
        return MOD_INFO;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        // force username to match certain pattern matching and forbids the user to user "forbidden" as a username
        return !username.equals("forbidden") && username.length() <= 20 && username.matches("^[a-zA-Z0-9_-]+$");
    }

    @Override
    public void changeUsername(String username) {
        this.username = username;
        System.out.println("Username changed to: " + username);
    }

    @Override
    public void sendMessage(String recipientID, String message) {
        System.out.println("Sending message to " + recipientID + ": " + message);
    }

    @Override
    public void connected() {
        System.out.println("User is now connected");
    }
}
