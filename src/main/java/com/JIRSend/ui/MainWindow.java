package com.JIRSend.ui;

import com.JIRSend.controler.MainController;
import com.JIRSend.network.Net;
import com.JIRSend.users.BaseUser;

import javax.swing.*;

public class MainWindow {
    private enum State {
        notInit, waitConnection, personal
    }

    protected MainController controler;

    private boolean noPanel;

    protected String lastError;

    private State state;
    private JFrame frame;
    private GUISection currentSection;
    private JPanel currentPanel;

    public MainWindow(MainController controler) {
        this.controler = controler;
        this.state = State.notInit;
        this.noPanel = true;
        this.currentSection = new GUISectionConnection(this, frame);
        this.lastError = "";
    }

    public void open() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Log.l("Starting window !", Log.LOG);
                createWindow();
            }
        });
    }

    private void createWindow() {
        frame = new JFrame("JIRSend");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon img = new ImageIcon("assets/jirsend_logo.png");

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
        Log.l("New window selection: " + currentSection.getSectionName(), Log.LOG);
        currentPanel = currentSection.createPanel();
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }
}
