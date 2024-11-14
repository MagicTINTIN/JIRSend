package com.JIRSend.view.gui;

import com.JIRSend.controller.MainController;
import com.JIRSend.model.Message;
import com.JIRSend.model.user.Conversation;
import com.JIRSend.model.user.UserEntry;
import com.JIRSend.view.cli.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.Locale;

public class GuiPanelMainChatSystem {
    private JPanel contentPane;
    private JPanel headPane;
    private JPanel bodyPane;
    private JPanel Footer;
    private JPanel contactsSection;
    private JPanel chatSection;
    private JPanel chatContent;
    private JPanel contactsContent;
    private JLabel contactsLabel;
    private JPanel chatContactName;
    private JPanel SendMessageSection;
    private JScrollPane messagesScroll;
    private JButton sendMessageButton;
    private JScrollPane messageToSendScroll;
    private JTextPane inputMessage;
    private JLabel chatContactLabel;
    private JTextField usernameTextField;
    private JScrollPane contactsListScroll;
    private JPanel contactsList;
    private JButton reconnectButton;
    private JPanel messagesList;
    private JPanel contactElement;
    private JLabel contactName;
    private JPanel messageElement;
    private JLabel messageAuthor;
    private JTextPane messageContent;
    private JLabel chatSystemName;
    private JLabel JIRSendLogo;

    private MainController controller;

    public static final Color headerFooterBGColor = new Color(-11842224);
    public static final Color bodyBGColor = new Color(-14342358);
    public static final Color contactSectionBGColor = new Color(-13223877);
    public static final Color contactElementBGColor = new Color(-13288643);
    public static final Color whitestColor = new Color(-394241);
    public static final Color disconnectedColor = new Color(255, 180, 180);
    public static final Color almostWhiteColor = new Color(-854792);
    public static final Color messageBGColor = new Color(-13816014);
    public static final Color chatBGColor = new Color(-14671323);
    public static final Color headerContactColor = chatBGColor; // new Color(-12371388);
    public static final Color carretColor = new Color(-3684409);

    private MainGUI maingui;

    // private Action submitSUAction;
    private Action submitSUAction = new SubmitSwitchUsernameAction();
    private Action submitMsgAction = new SubmitMessageAction();

    public GuiPanelMainChatSystem(MainController controller, MainGUI maingui) {
        this.controller = controller;
        usernameTextField.setText(this.controller.getUsername());
        this.maingui = maingui;
        updateContactList();

        MainController.contactsChange.subscribe((event) -> {
            updateContactList();
        });
        MainController.messageReceived.subscribe((msg) -> {
            if (msg.sender.equals(controller.getConversationName())) {
                updateConversation();
            }
        });
        updateConversation();
    }

    private void updateContactList() {
        String currentConvName = controller.getConversationName();
        contactsList.removeAll();
        // Log.l("UPDATING GUI CONTACTS LIST", Log.WARNING);
        for (UserEntry ue : controller.getContacts()) {
            createContactElement(ue.username, ue.online, currentConvName == ue.username, false);
        }
        maingui.refreshFrame();
    }

    private void updateConversation() {
        Log.l("UPDATING GUI CONVERSATION", Log.WARNING);
        messagesList.removeAll();
        createMessageElement("ADMIN", "WELCOME IN JIRSEND");
        Conversation conv = controller.getConversation();
        if (conv == null) {
            chatContactLabel.setText("<- Choose a conversation");
            SendMessageSection.setVisible(false);
            return;
        }
        String recipient = controller.getConversationName();
        String you = controller.getUsername();
        chatContactLabel.setText(recipient);
        if (controller.isConnected(recipient))
            SendMessageSection.setVisible(true);
        else
            SendMessageSection.setVisible(false);
        for (Message msg : conv.getMessages()) {
            System.out.println("MSG> " + msg.sender + ": " + msg.message);
            createMessageElement(msg.sender.equals("you") ? you : recipient, msg.message);
        }
    }

    private void createContactElement(String username, boolean online, boolean currentConv, boolean hasNewMessage) {
        contactElement = new JPanel();
        contactElement.setMaximumSize(new Dimension(1920, 100));
        contactElement.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contactElement.setBackground(currentConv ? contactElementBGColor.brighter() : contactElementBGColor);
        // contactElement.setBorder(new LineBorder(whitestColor, 2));
        contactElement.setBorder(new GuiRoundedBorder(10));
        contactElement.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.getConversation(username);
                updateConversation();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        contactsList.add(contactElement);
        contactName = new JLabel();
        Font contactNameFont = this.$$$getFont$$$("Monospaced", Font.BOLD, -1, contactName.getFont());
        if (contactNameFont != null)
            contactName.setFont(contactNameFont);
        contactName.setForeground(online ? almostWhiteColor : disconnectedColor);
        contactName.setText(online ? username
                : ("<html><body style=\"text-align:center;\">" + username
                        + "<br><span color=\"red\">(offline)</span></body></html>"));
        contactElement.add(contactName,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
    }

    private void createMessageElement(String author, String content) {
        messageElement = new JPanel();
        messageElement
                .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2,
                        new Insets(0, 0, 0, 0), -1, -1));
        messageElement.setBackground(messageBGColor);
        messagesList.add(messageElement,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null, 0, false));
        messageAuthor = new JLabel();
        messageAuthor.setBackground(messageBGColor);
        Font messageAuthorFont = this.$$$getFont$$$("Monospaced", Font.BOLD, -1, messageAuthor.getFont());
        if (messageAuthorFont != null)
            messageAuthor.setFont(messageAuthorFont);
        messageAuthor.setForeground(whitestColor);
        messageAuthor.setText(author + ": ");
        messageElement.add(messageAuthor,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        messageContent = new JTextPane();
        messageContent.setBackground(messageBGColor);
        messageContent.setDisabledTextColor(almostWhiteColor);
        messageContent.setEditable(false);
        messageContent.setEnabled(false);
        Font messageContentFont = this.$$$getFont$$$("Monospaced", -1, -1, messageContent.getFont());
        if (messageContentFont != null)
            messageContent.setFont(messageContentFont);
        messageContent.setForeground(almostWhiteColor);
        messageContent.setSelectedTextColor(whitestColor);
        messageContent.setText(content);
        messageElement.add(messageContent,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null,
                        new Dimension(150, 50),
                        null, 0, false));
    }

    public JPanel getPanel() {
        return contentPane;
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE! (lol)
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(
                new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), 0, 0));
        headPane = new JPanel();
        headPane.setLayout(
                new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 0, 0));
        headPane.setBackground(headerFooterBGColor);
        contentPane.add(headPane,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(24, 25),
                        null, 0, false));
        chatSystemName = new JLabel();
        chatSystemName.setOpaque(false);
        Font chatSystemNameFont = this.$$$getFont$$$("Monospaced", Font.BOLD, -1, chatSystemName.getFont());
        if (chatSystemNameFont != null)
            chatSystemName.setFont(chatSystemNameFont);
        chatSystemName.setForeground(whitestColor);
        chatSystemName.setText("JIRSend");
        chatSystemName.setBorder(new EmptyBorder(0, 10, 0, 0));
        headPane.add(chatSystemName,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        headPane.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1,
                com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null,
                0, false));
        JIRSendLogo = new JLabel(new ImageIcon(
                new javax.swing.ImageIcon(getClass().getResource("/assets/jirsend_logo.png")).getImage()
                        .getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        JIRSendLogo.setHorizontalAlignment(0);
        JIRSendLogo.setHorizontalTextPosition(0);
        JIRSendLogo.setBorder(new EmptyBorder(0, 1, 0, 0));
        // JIRSendLogo.setText("JS");
        headPane.add(JIRSendLogo,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(20, 20),
                        null, 0, false));
        bodyPane = new JPanel();
        bodyPane.setLayout(
                new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        bodyPane.setBackground(bodyBGColor);
        contentPane.add(bodyPane,
                new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, new Dimension(24, 353), null, 0, false));
        contactsSection = new JPanel();
        contactsSection.setLayout(new CardLayout(0, 0));
        contactsSection.setBackground(contactSectionBGColor);
        bodyPane.add(contactsSection,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(121, 0),
                        null, 0, false));
        contactsContent = new JPanel();
        contactsContent
                .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1,
                        new Insets(0, 0, 0, 0), -1, -1));
        contactsContent.setBackground(contactSectionBGColor);
        contactsSection.add(contactsContent, "Card1");
        contactsLabel = new JLabel();
        contactsLabel.setBackground(contactElementBGColor);
        Font contactsLabelFont = this.$$$getFont$$$("Monospaced", Font.BOLD, 22, contactsLabel.getFont());
        if (contactsLabelFont != null)
            contactsLabel.setFont(contactsLabelFont);
        contactsLabel.setForeground(whitestColor);
        contactsLabel.setText("Contacts");
        contactsContent.add(contactsLabel,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        contactsListScroll = new JScrollPane();
        contactsListScroll.setBackground(contactSectionBGColor);
        contactsContent.add(contactsListScroll,
                new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null, 0, false));
        contactsList = new JPanel();
        contactsList.setLayout(new BoxLayout(contactsList, BoxLayout.Y_AXIS));
        contactsList.setAlignmentY(Component.TOP_ALIGNMENT);

        // Add some padding around each contact panel
        contactsList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // contactsList
        // .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1,
        // new Insets(0, 0, 0, 0), -1, -1));
        contactsList.setBackground(contactSectionBGColor);
        contactsListScroll.setViewportView(contactsList);
        contactsListScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        contactsListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // final com.intellij.uiDesigner.core.Spacer spacer2 = new
        // com.intellij.uiDesigner.core.Spacer();
        // contactsList.add(spacer2,
        // new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1,
        // com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
        // com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1,
        // com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null,
        // null, null, 0, false));
        chatSection = new JPanel();
        chatSection.setLayout(new CardLayout(0, 0));
        chatSection.setBackground(chatBGColor);
        bodyPane.add(chatSection,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null, 0, false));
        chatContent = new JPanel();
        chatContent.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0),
                -1, -1));
        chatContent.setBackground(chatBGColor);
        chatSection.add(chatContent, "Card1");
        chatContactName = new JPanel();
        chatContactName
                .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1,
                        new Insets(0, 0, 0, 0), -1, -1));
        chatContactName.setBackground(headerContactColor);
        // chatContactName.setBorder(new LineBorder(headerContactColor.brighter(), 10,
        // true));
        chatContactName.setBorder(new MatteBorder(0, 0, 2, 0, headerContactColor.brighter()));
        chatContactName.setEnabled(true);
        chatContent.add(chatContactName,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null, 0, false));
        chatContactLabel = new JLabel();
        Font chatContactLabelFont = this.$$$getFont$$$("Monospaced", Font.BOLD, 36, chatContactLabel.getFont());
        if (chatContactLabelFont != null)
            chatContactLabel.setFont(chatContactLabelFont);
        chatContactLabel.setForeground(whitestColor);
        chatContactLabel.setIconTextGap(10);
        chatContactLabel.setText("<- Choose a conversation");
        chatContactName.add(chatContactLabel,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 1, false));
        SendMessageSection = new JPanel();
        SendMessageSection
                .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2,
                        new Insets(0, 0, 0, 0), -1, -1));
        SendMessageSection.setBackground(messageBGColor);
        chatContent.add(SendMessageSection,
                new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, new Dimension(-1, 80), new Dimension(-1, 300), 0, false));
        // sendMessageButton = new JButton(
        // new ImageIcon(new
        // javax.swing.ImageIcon(getClass().getResource("/assets/send.png"))
        // .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));

        sendMessageButton = new JButton(submitMsgAction);
        sendMessageButton.setText("");
        sendMessageButton.setIcon(
                new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/send.png"))
                        .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        sendMessageButton.setBackground(headerFooterBGColor);
        sendMessageButton.setPreferredSize(new Dimension(60, 60));
        sendMessageButton.setBorderPainted(true);
        sendMessageButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        final JSButtonUI uiSend = new JSButtonUI();
        uiSend.setPressedColor(sendMessageButton.getBackground().darker());
        sendMessageButton.setUI(uiSend);
        sendMessageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Font sendMessageButtonFont = this.$$$getFont$$$("Monospaced", Font.BOLD, 8,
                sendMessageButton.getFont());
        if (sendMessageButtonFont != null)
            sendMessageButton.setFont(sendMessageButtonFont);
        sendMessageButton.setForeground(almostWhiteColor);
        // sendMessageButton.setText("SEND >");
        SendMessageSection.add(sendMessageButton,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        messageToSendScroll = new JScrollPane();
        messageToSendScroll.setBackground(messageBGColor);
        SendMessageSection.add(messageToSendScroll,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null, 0, false));
        inputMessage = new JTextPane();
        inputMessage.setBackground(messageBGColor);
        inputMessage.setCaretColor(carretColor);
        inputMessage.setDragEnabled(true);
        Font inputMessageFont = this.$$$getFont$$$("Monospaced", -1, -1, inputMessage.getFont());
        if (inputMessageFont != null)
            inputMessage.setFont(inputMessageFont);
        inputMessage.setForeground(almostWhiteColor);
        inputMessage.setName("Enter your message here");
        inputMessage.setSelectedTextColor(whitestColor);
        inputMessage.setText("");
        inputMessage.setToolTipText("Enter your message here");
        messageToSendScroll.setViewportView(inputMessage);

        // TODO: edit this part like contacts scrollpane & list

        // TODO: ###############################################################

        messagesScroll = new JScrollPane();
        messagesScroll.setAutoscrolls(true);
        messagesScroll.setBackground(chatBGColor);
        messagesScroll.setHorizontalScrollBarPolicy(31);
        messagesScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        chatContent.add(messagesScroll,
                new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null, 0, false));
        messagesList = new JPanel();
        // messagesList.setLayout(
        // new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(1, 1, 1,
        // 1), 2, 2));

        messagesList.setLayout(new BoxLayout(messagesList, BoxLayout.Y_AXIS));
        messagesList.setAlignmentY(Component.TOP_ALIGNMENT);
        messagesList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagesList.setBackground(chatBGColor);
        messagesList.setForeground(almostWhiteColor);
        messagesScroll.setViewportView(messagesList);

        // final com.intellij.uiDesigner.core.Spacer spacer3 = new
        // com.intellij.uiDesigner.core.Spacer();
        // messagesList.add(spacer3,
        // new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1,
        // com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
        // com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1,
        // com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null,
        // null, null, 0, false));

        // TODO: ###############################################################

        Footer = new JPanel();
        Footer.setLayout(
                new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        Footer.setBackground(headerFooterBGColor);
        contentPane.add(Footer,
                new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(24, 25),
                        null, 0, false));
        usernameTextField = new RoundJTextField(17);
        usernameTextField.setBackground(headerFooterBGColor.darker());
        usernameTextField.setCaretColor(carretColor);
        Font usernameTextFieldFont = this.$$$getFont$$$("Monospaced", Font.BOLD, -1,
                usernameTextField.getFont());
        if (usernameTextFieldFont != null)
            usernameTextField.setFont(usernameTextFieldFont);
        usernameTextField.setForeground(almostWhiteColor);
        usernameTextField.setHorizontalAlignment(4);
        usernameTextField.setSelectedTextColor(whitestColor);
        usernameTextField.setText("---");
        usernameTextField.setToolTipText("Change your username");
        Footer.add(usernameTextField,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(150, -1),
                        new Dimension(250, -1), 0, false));
        // reconnectButton = new JButton(
        // new ImageIcon(new
        // javax.swing.ImageIcon(getClass().getResource("/assets/reconnect.png"))
        // .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        // reconnectButton.setAction(submitSUAction);
        reconnectButton = new JButton(submitSUAction);
        reconnectButton.setText("");
        reconnectButton.setIcon(
                new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/reconnect.png"))
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));

        // reconnectButton.setText("change");
        reconnectButton.setPreferredSize(new Dimension(20, 20));
        reconnectButton.setBackground(headerFooterBGColor);
        reconnectButton.setForeground(whitestColor);
        reconnectButton.setBorder(new EmptyBorder(0, 1, 0, 3));
        final JSButtonUI uiReconnect = new JSButtonUI();
        uiReconnect.setPressedColor(reconnectButton.getBackground().darker());
        reconnectButton.setUI(uiReconnect);
        reconnectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // reconnectButton.setText("S");
        Footer.add(reconnectButton,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null,
                        new Dimension(20, 20), 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null)
            return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(),
                size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize())
                : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback
                : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private class SubmitSwitchUsernameAction extends AbstractAction {
        public SubmitSwitchUsernameAction() {
            super("SwitchUsername");
        }

        public void actionPerformed(ActionEvent action) {
            String usernameAsked = usernameTextField.getText();
            if (usernameAsked.equals(controller.getUsername()))
                return;
            String res = controller.changeUsername(usernameAsked);
            if (res.equals("")) {
                usernameTextField.setText(controller.getUsername());
            } else {
                ErrorPopup.show("Impossible to change username", res);
                return;
            }

            updateConversation();
            Log.l("Switching username to '" + usernameAsked + "'", Log.LOG);
        }
    }

    private class SubmitMessageAction extends AbstractAction {
        public SubmitMessageAction() {
            super("SubmitMessage");
        }

        public void actionPerformed(ActionEvent action) {
            String messageToSend = inputMessage.getText();
            if (messageToSend == null || messageToSend.isEmpty()
                    || controller.getConversationName() == null)
                return;
            MainController.sendMessage.safePut(new Message(controller.getUsername(),
                    controller.getConversationName(), messageToSend));

            inputMessage.setText("");
            updateConversation();
        }
    }
}
