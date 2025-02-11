package com.JIRSendAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;

public class ModController {
    private String controllerName;
    public final ModControllerActions mainController;

    // Mods
    HashMap<String, JIRSendMod> mods;

    // Pipes
    public static DataPipe<ModUser> contactChange = new DataPipe<>("MOD: contact change");
    public static DataPipe<ModMessage> storeMessage = new DataPipe<>("MOD: Store message");

    public ModController(String name, ModControllerActions actions) {
        this.controllerName = name;
        this.mainController = actions;
        this.mods = new HashMap<>();
        ModLoader modLoader = new ModLoader();
        List<JIRSendMod> modList = modLoader.loadMods();
        System.out.println("Loading mods... (" + modList.size() + " found)");
        for (JIRSendMod jirSendMod : modList) {
            String modID = jirSendMod.getModInformation().id;
            if (this.mods.containsKey(modID))
                mainController.signalErrorAndStop("2 mods are using the same ID: " + this.mods.get(modID).getModInformation()
                        + " and " + jirSendMod.getModInformation(), 0);
            mods.put(modID, jirSendMod);
        }
    }

    public void initializeMods() {
        for (JIRSendMod mod : mods.values()) {
            mod.initialize(this);
            System.out.println(mod.getModInformation() + " loaded.");
        }
    }

    public ModController(ModControllerActions actions) {
        this("JIRSend Mod Controller", actions);
    }

    public String getName() {
        return controllerName;
    }

    public ArrayList<JIRSendModInformation> getModsInformation() {
        ArrayList<JIRSendModInformation> infos = new ArrayList<>();
        for (JIRSendMod mod : mods.values()) {
            infos.add(mod.getModInformation());
        }
        return infos;
    }

    /**
     * Will stop the mods
     */
    public void stop() {
        for (JIRSendMod mod : mods.values()) {
            mod.stop();
        }
    }

    public void nowConnected() {
        for (JIRSendMod mod : mods.values()) {
            mod.connected();
        }
    }

    public void changeUsername(String username) {
        for (JIRSendMod mod : mods.values()) {
            mod.changeUsername(username);
        }
    }

    public boolean isUsernameAvailable(String username) {
        for (JIRSendMod mod : mods.values()) {
            if (!mod.isUsernameAvailable(username))
                return false;
        }
        return true;
    }

    public void sendMessageViaMod(String modID, String recipientID, String message) {
        if (!mods.containsKey(modID)) {
            mainController.signalError("MOD NOT FOUND: " + modID);
            return;
        }
        mods.get(modID).sendMessage(recipientID, message);
    }

    public static interface ModControllerActions {

        public String getUsername();

        public boolean isUsernameAvailable(String username, JIRSendModInformation info);

        public ArrayList<String> getConnectedUsernames();

        /**
         * To signal error with popup (if GUI)
         * 
         * @param error
         */
        public void signalError(String error);

        /**
         * Signals error with popup (if GUI) and STOPS the app
         * 
         * @warning use it carefully!
         * @param error
         * @param exitStatus between 0 and 15
         */
        public void signalErrorAndStop(String error, int exitStatus);
    }
}
