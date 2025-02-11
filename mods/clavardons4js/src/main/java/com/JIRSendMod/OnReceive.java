package com.JIRSendMod;

import com.JIRSendAPI.ModController;
import com.JIRSendAPI.ModMessage;
import com.JIRSendAPI.ModUser;

/*
 *
 * NOTE:
 * MOST OF THIS CODE IS A COPY-PASTE 
 * FROM chatsystem-anglade-loubejac !
 * (as it is a mod to communicate with their chatsystem)
 * 
 */

public class OnReceive implements UserList.Observer {
    //it actually is used
    @SuppressWarnings("unused")
    private final Clavardons4JS controller;

    public OnReceive(Clavardons4JS controller) {
        this.controller = controller;
    }

    public void updateUserList(String type, String[] args) {
        System.out.println("GOT MESSAGE FROM CLAVARDONS:\n[" + type + "] " + String.join(" ", args));
        User user;
        switch (type) {
            case "setUsername":
                user = UserList.getInstance().getActiveUserByUsername(args[0]);
                user.setUsername(args[1]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        args[1],
                        ModUser.Status.Online));
                break;
            case "setUserOffline":
                user = UserList.getInstance().getActiveUserByUsername(args[0]);
                if(user==null) break;
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Offline));
                break;
            case "addUser":
                user = UserList.getInstance().getUserByUsername(args[1]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Online));
                break;
            case "connectTCP":
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Online));
                user.getSocket().sendConnectResponse(true);
                break;
            case "AcceptSession":
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Online));
                break;
            case "RefuseSession":
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Busy));
                break;
            case "messageTCP":
                User me = UserList.getInstance().getMe();
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.storeMessage.put(new ModMessage(Clavardons4JS.MOD_INFO, user.modUser.userID,
                        user.modUser.username, me.modUser.userID, args[1], Clavardons4JS.getTime(), true));
                break;
            case "quitTCP":
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Away));
                break;
            default:
                System.err.println("Received unknown message from Clavardon: ["+type+"] "+String.join(" ",args));
        }
    }
}
