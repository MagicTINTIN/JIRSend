package com.JIRSend.model.network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.JIRSend.controller.MainController;
import com.JIRSend.model.user.UserEntry;
import com.JIRSend.view.cli.Log;

/*
*  Protocols:
*      - Connection protocol:
*          Broadcast "GetUser"
*          Response: "GetUserResponse $username$" -> store usernames and IPs
*      - GetUser
*      - GetUserResponse username
*      - NewUser username
*      - SetOfflineUser username
*      - UpdateUsername new
*      - SendMessage msg
 */
public class Net {
    private NetworkIO netIO;
    private HashMap<String, UserEntry> ipToUserEntry;
    private final MainController controller;
    private final CountDownLatch setupLatch;

    public Net(MainController controller, VoidCallback onSetup) {
        this.setupLatch = new CountDownLatch(1);
        this.ipToUserEntry = new HashMap<>();
        this.controller = controller;
        this.netIO = new NetworkIO(new NetworkCallback(), () -> {
            // signal that setup is complete
            setupLatch.countDown();
        });
        // wait for TCP Server to be started
        try {
            setupLatch.await();
        } catch (InterruptedException e) {
            // re-set the interrupt flag
            Thread.currentThread().interrupt();
            Log.e("Net setup was interrupted");
        }
        broadcast("GetUser");
        onSetup.execute();
    }

    /**
     * Takes the username if available
     * 
     * @param username
     * @return error | "" if available and taken
     */
    public String usernameAvailable(String username) {
        String isSyntaxValid = isUsernameValid(username);
        if (!isSyntaxValid.equals(""))
            return isSyntaxValid;
        for (UserEntry entry : ipToUserEntry.values())
            if (entry.username.equals(username))
                return "'" + username + "' is not available!";
        broadcast("NewUser " + username);
        // printHashMap();
        return "";
    }

    private class NetworkCallback extends NetCallback {
        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast,
                boolean isUDP) {
            final String senderIP = senderAddress.getHostAddress();
            Log.l("[" + senderIP + ":" + senderPort + "] " + value, Log.LOG);
            final String command;
            final String args;
            if (value.equals("GetUser")) {
                command = value;
                args = null;
            } else {
                final String[] splited = value.split(" ", 2);
                if (splited.length != 2) {
                    Log.e("Wrong message format: " + value);
                    return;
                }
                command = splited[0];
                args = splited[1];
            }
            Log.l("received: \"" + command + "\" \"" + args + "\"", Log.LOG);
            switch (command) {
                case "GetUser":
                    String username = controller.getUsername();
                    if (username != null)
                        send(senderIP, "GetUserResponse " + username);
                    break;
                case "GetUserResponse":
                    if (!isUsernameValid(args).equals(""))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else {
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                        MainController.contactsChange.safePut(args + " is now connected");
                    }
                    break;
                case "NewUser":
                    if (!isUsernameValid(args).equals(""))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else {
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                        MainController.contactsChange.safePut(args + " is now connected");
                    }
                    break;
                case "SetOfflineUser":
                    if (ipToUserEntry.containsKey(senderIP)) {
                        ipToUserEntry.get(senderIP).online = false;
                    } else {
                        if (isUsernameValid(args).equals("")) {
                            ipToUserEntry.put(senderIP, new UserEntry(false, args));
                            MainController.contactsChange.safePut(args + " has disconnected");
                        }
                        else
                            Log.l("Forbidden username: " + args);
                    }
                    break;
                case "UpdateUsername":
                    if (!isUsernameValid(args).equals(""))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else if (ipToUserEntry.containsKey(senderIP))
                        ipToUserEntry.get(senderIP).username = args;
                    else {
                        // TODO: check safety
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                        MainController.contactsChange.safePut(args + " has updated his username");
                    }
                    break;
                case "SendMessage":
                    if (ipToUserEntry.containsKey(senderIP)) // Maybe set user to online = true
                        System.out.println("[" + ipToUserEntry.get(senderIP).username + "] " + args);
                    else {
                        send(senderIP, "GetUser");
                        System.out.println("[Unkown user] " + args);
                    }
                    break;
                default:
                    Log.l("Unkown command: " + command + " " + value, Log.LOG);
                    break;
            }
        }

    }

    public HashMap<String, UserEntry> getHashMap() {
        return ipToUserEntry;
    }

    public void printHashMap() {
        if (ipToUserEntry.isEmpty()) {
            System.out.println("{}");
            return;
        }
        System.out.println("{");
        for (Map.Entry<String, UserEntry> e : ipToUserEntry.entrySet()) {
            System.out.println("\t" + e.getKey() + ":" + e.getValue());
        }
        System.out.println("}");
    }

    public ArrayList<UserEntry> getUserEntries() {
        ArrayList<UserEntry> l = new ArrayList<>();
        for (UserEntry ue : ipToUserEntry.values()) {
            l.add(ue);
        }
        return l;
    }

    /**
     * Returns whether a username syntax is valid
     * 
     * @param username
     * @return error | "" if valid
     */
    private String isUsernameValid(String username) {
        if (username.contains(":"))
            return "Username should not contain ':'!";
        else if (username.contains(" "))
            return "Username should not contain spaces ' '!";
        else if (username.length() < 2)
            return "Username should have at least 2 characters!";
        return "";
    }

    private boolean send(String address, String string) {
        Log.l("Sending: " + string, Log.LOG);
        return netIO.send(address, string);
    }

    private void broadcast(String string) {
        Log.l("Broadcasting: " + string, Log.LOG);
        netIO.broadcast(string);
    }
}
