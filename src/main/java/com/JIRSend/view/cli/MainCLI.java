package com.JIRSend.view.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.JIRSend.controller.MainController;
import com.JIRSend.view.MainAbstractView;

public class MainCLI extends MainAbstractView {

    protected MainController controller;
    private MainCliThread thread;

    public MainCLI(MainController controller) {
        this.controller = controller;
        this.thread = new MainCliThread();
    }

    @Override
    public void open() {
        Log.l("Starting CLI thread");
        this.thread.start();
    }

    private class MainCliThread extends Thread {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        private String readIn() {
            try {
                return reader.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public void run() {
            this.setName("CLI Thread");

            chooseUsername();

            System.out.println("Welcome " + CliTools.colorize(CliTools.BOLD, controller.getUsername()) + "!");
            CliTools.coloredPrintln(CliTools.BLACK_DESAT_COLOR, controller.getContacts().size()
                    + " people connected.\n");

            while (true) {
                CliTools.coloredPrint(CliTools.PURPLE_NORMAL_COLOR, "> ");
                String cmd = readIn();
                String[] args = cmd.split(" ");
            }
        }

        private void chooseUsername() {
            while (true) {
                System.out.print("Enter your username: ");
                String usernameChosen = readIn();
                if (usernameChosen.length() < 2) {
                    CliTools.printBigError("Username should have at least 2 characters.");
                    continue;
                }

                if (controller.changeUsername(usernameChosen)) {
                    break;
                } else {
                    CliTools.printBigError("'" + usernameChosen + "' is not available");
                }
            }
        }
    }
}
