package com.JIRSend.ui;

import com.JIRSend.network.Net;
import com.JIRSend.network.NetCallback;
import com.JIRSend.users.BaseUser;

import java.net.InetAddress;

import javax.swing.*;

public class MainWindow {
    private enum State {
        notInit, waitConnection, personal
    }

    private boolean noPanel;

    protected BaseUser user;
    protected Net net;
    protected String lastError;
    protected String username;
    private State state;
    private JFrame frame;
    private GUISection currentSection;
    private JPanel currentPanel;

    public MainWindow() {
        this.state = State.notInit;
        this.noPanel = true;
        this.currentSection = new GUISectionConnection(this, frame);
        this.lastError = "";
        this.net = new Net();
    }

    public void open() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Log.l("Starting window !");
                createWindow();
            }
        });
    }

    private void createWindow() {
        frame = new JFrame("JIRSend");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon img = new ImageIcon("assets/jirsend_logo.jpg");
        System.out.println("iconWidth" + img.getIconWidth());

        switchToNextSection();

        frame.pack();
        frame.setVisible(true);
        frame.setIconImage(img.getImage());
        // frame.revalidate();
        // frame.repaint();
    }

    protected void switchToNextSection() {
        if (state == State.notInit) {
            state = State.waitConnection;
        } else if (state == State.waitConnection) {
            state = State.personal;
        }
        refreshSection();
    }

    protected void refreshSection() {
        if (state == State.notInit) {
            return;
        } else if (!noPanel) {
            frame.remove(currentPanel);
        }

        if (state == State.waitConnection) {
            currentSection = new GUISectionConnection(this, frame);
        } else if (state == State.personal) {
            currentSection = new GUISectionPersonalInfo(this, frame);
        }

        noPanel = false;
        System.out.println(currentSection.getSectionName());
        currentPanel = currentSection.createPanel();
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }

    public class NetworkCallback extends NetCallback {

        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast) {
            // TODO Auto-generated method stub
            Log.l("[" + senderAddress.getHostAddress() + ":" + senderPort + "] RECEIVED: " + value);
        }

    }
}
