package com.JIRSend.view.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.JIRSend.controller.MainController;
import com.JIRSend.view.MainAbstractView;
import com.JIRSend.view.cli.Log;

public class MainGUI extends MainAbstractView {
    private enum State {
        notInit, waitConnection, chat, personal
    }

    protected MainController controller;

    private boolean noPanel;

    protected String lastError;

    private State state;
    private JFrame frame;
    private GUISection currentSection;
    private JPanel currentPanel;

    public MainGUI(MainController controller) {
        this.controller = controller;
        this.state = State.notInit;
        this.noPanel = true;
        this.currentSection = new GUISectionConnection(this, frame);
        this.lastError = "";
    }

    @Override
    public void open() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Log.l("Starting window", Log.LOG);
                createWindow();
            }
        });
    }

    private void createWindow() {
        frame = new JFrame("JIRSend");
        frame.setTitle("JIRSend");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Log.l("Closing GUI", Log.WARNING);
                controller.stoppingApp();
            }
        });

        ImageIcon img = new ImageIcon("assets/jirsend_logo.png");

        switchToNextSection();

        frame.pack();
        frame.setVisible(true);
        frame.setIconImage(img.getImage());
        frame.setSize(400, 500);
    }

    public void updateIcon() {
        ImageIcon img;
        int totalUnread = controller.getTotalUnread();
        // Log.l("REFRESH ICON: " + totalUnread, Log.WARNING);
        if (totalUnread > 0) {
            img = new ImageIcon("assets/jirsend_logo_notif.png");
            frame.setName("JIRSend (" + totalUnread + ")");
            frame.setTitle("JIRSend (" + totalUnread + ")");
        } else {
            img = new ImageIcon("assets/jirsend_logo.png");
            frame.setName("JIRSend");
            frame.setTitle("JIRSend");
        }
        frame.setIconImage(img.getImage());
    }

    protected void switchToNextSection() {
        if (state == State.notInit) {
            state = State.waitConnection;
        } else if (state == State.waitConnection) {
            state = State.chat;
        } else {
            // we should not arrive in that case, for test purpose only
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
        } else if (state == State.chat) {
            frame.setSize(800, 400);
            frame.setMinimumSize(new Dimension(800, 400));
            currentSection = new GUISectionMain(this, frame);
        }

        noPanel = false;
        Log.l("New window selection: " + currentSection.getSectionName(), Log.LOG);
        currentPanel = currentSection.createPanel();
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }

    protected void refreshFrame() {
        frame.revalidate();
        frame.repaint();
    }
}
