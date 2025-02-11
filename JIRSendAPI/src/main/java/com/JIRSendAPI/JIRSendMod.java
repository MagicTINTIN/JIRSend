package com.JIRSendAPI;

import javax.swing.ImageIcon;

/**
 * GENERAL INFORMATION about JIRSend mods
 * 
 * @implSpec ID and username for users (recipientID/userID, username
 *           in ModUser) must not exceed 20 chars
 * @implSpec mod ID (id in JIRSendModInformation) must not exceed 20
 *           chars
 * 
 * @implNote be sure to have a unique mod id, because it will be used to route
 *           messages to your mod
 * @implNote You might need to send information using the ModController's pipes
 *           (contactChange, storeMessage)
 * 
 */
public interface JIRSendMod {

    /**
     * Will be executed at JIRSend startup (after every JIRSend routines)
     * 
     * @param controller to communicate information to JIRSend app and get some
     *                   information such as username.
     */
    public void initialize(ModController controller);

    /**
     * Executed when the user will be connected for the first time
     */
    public void connected();

    /**
     * Will be normally executed when JIRSend is closing
     */
    public void stop();

    /**
     * Get all the information from mod
     * 
     * @return mod information
     */
    public JIRSendModInformation getModInformation();

    /**
     * Return if a username is available. No need to check if there is are contacts
     * that have the same name: this is checked internally according to the
     * contactChange given. Made for specific cases like username length or specific
     * chars
     * 
     * @param username
     * @return if username is available
     */
    public boolean isUsernameAvailable(String username);

    /**
     * Announce that our user has taken this username
     * 
     * @param username
     */
    public void changeUsername(String username);

    /**
     * Send a message to recipient
     * 
     * @param recipientID (generally an IP address)
     * @param message
     */
    public void sendMessage(String recipientID, String message);

    public static class JIRSendModInformation {
        public String id, name, description, author;
        public int interfaceVersion, modVersion;
        public ImageIcon modIcon;

        public JIRSendModInformation(String id, String name, String description, String author, int interfaceVersion,
                int modVersion, ImageIcon modIcon) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.author = author;
            this.interfaceVersion = interfaceVersion;
            this.modVersion = modVersion;
            this.modIcon = modIcon;
        }

        public final String getVersion() {
            return "v" + interfaceVersion + "." + modVersion;
        }

        @Override
        public final String toString() {
            return "[ [" + name + "](" + getVersion() + ") by: '" + author + "' ]";
        }
    }
}
