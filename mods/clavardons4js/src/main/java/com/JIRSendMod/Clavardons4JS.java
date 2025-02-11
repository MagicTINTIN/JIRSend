package com.JIRSendMod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;

import javax.swing.ImageIcon;
import com.JIRSendAPI.*;

/*
 *
 * NOTE:
 * MOST OF THIS CODE IS A COPY-PASTE 
 * FROM chatsystem-anglade-loubejac !
 * (as it is a mod to communicate with their chatsystem)
 * 
 */

public class Clavardons4JS implements JIRSendMod {

    private ModController controller = null;
    private Timer timer;
    private MyNetworkInterface net = null;
    static public final JIRSendModInformation MOD_INFO = new JIRSendModInformation(
            "clavardons4js",
            "Clavardons for JIRSend",
            "A mod to talk to JP from JIRSend",
            "Atsuyo64", // author
            1, // interface version
            0, // mod version
            new ImageIcon(Clavardons4JS.class.getResource("/assets/clavardons.png")));

    @Override
    public void initialize(ModController controller) {
        this.controller = controller;
        UserList.setInstanceController(controller);
        try {
            net = new MyNetworkInterface();
            net.startUDPListeningThread();
            net.subscribeOnUDPServer(UserList.getInstance());
            net.startTCPListeningThread();
            UserList.getInstance().subscribe(new OnReceive(this));
            try {
                net.getAllUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimeToLiveTask(net), 0, 5000);

        } catch (IOException e) {
            System.err.println("Could not create server socket /!\\ " + e);
        }
        System.out.println("Clavardons4JS initialized. Welcome!");
    }

    @Override
    public void stop() {
        this.timer.cancel();
        this.net.stopTCPListeningThread();
        this.net.stopUDPListeningThread();
        try {
            this.net.NetworkInterfaceDel(new User(controller.mainController.getUsername(),
                    MyNetworkInterface.getIpAddr(), true, true, "Clavardons"));
        } catch (IOException e) {
            System.err.println("Error on deleting Network Interface: " + e);
        }
        UserList.getInstance().UserListDel();
        System.out.println("Clavardons4JS is stopping. Goodbye!");
    }

    @Override
    public JIRSendModInformation getModInformation() {
        return MOD_INFO;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        // force username to match certain pattern matching and forbids the user to user
        // "forbidden" as a username
        return !username.equals("forbidden") && username.length() <= 20 && username.matches("^[a-zA-Z0-9_-]+$")
                && UserList.getInstance().usernameIsAvailable(username);
    }

    private boolean firstTime = true;
    @Override
    public void changeUsername(String username) {
        if(firstTime) { firstTime = false; return;}
        User user = new User(username, MyNetworkInterface.getIpAddr(), true, true, "Clavardons");
        UserList.getInstance().setMe(user);
        net.sendUpdateUsername(username);
    }

    @Override
    public void sendMessage(String recipientID, String message) {
        User receiver = UserList.getInstance().getUserByIpAddress(recipientID);
        if (receiver == null) {
            System.out.println("<Clavardons-Run> -> L'utilisateur est hors ligne ou inconnu");
            return;
        }
        if (receiver.getSocket() == null) {
            System.out.println(
                    "<Clavardons-Run> -> Demarrage d'une session de clavardage avec " + receiver.modUser.username);
            TCPClient client = new TCPClient(receiver.getIpAddress());
            client.start();
            receiver.setSocket(client);
        }
        if (receiver.getSocket() == null) {
            System.err.println("<Clavardons-Run> -> L'utilisateur n'a pas de session de clavardage ouverte");
            return;
        }
        ModMessage msg = new ModMessage(MOD_INFO, UserList.getInstance().getMe().modUser.userID,
                UserList.getInstance().getMe().modUser.username, receiver.modUser.userID, message, getTime(), false);
        receiver.addMessage(msg);
        receiver.getSocket().sendMessage(message);
        // System.out.println("Sending message to " + recipientID + ": " + message);
        ModController.storeMessage.put(msg);
    }

    @Override
    public void connected() {
        // System.out.println("Clavardons is now connected");
        User user = new User(controller.mainController.getUsername(), MyNetworkInterface.getIpAddr(), true, true,
                "Clavardons");
        UserList.getInstance().setMe(user);
        try {
            net.sendNewUser(user);
        } catch (IOException e) {
            System.err.println("Could not send New User [" + user + "] " + e);
        }
    }

    static public String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
