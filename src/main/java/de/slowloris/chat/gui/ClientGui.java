/*
 * Copyright (c) 2018 Slowloris.de
 *
 * Development: Weichtier
 *
 * You're allowed to edit the Project.
 * Its not allowed to reupload this Project!
 */

package de.slowloris.chat.gui;

import de.slowloris.chat.gui.frames.ChatFrame;
import de.slowloris.chat.gui.frames.ChatLogin;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static de.slowloris.chat.gui.ClientUtils.appendToPane;

public class ClientGui {

    private static JFrame frame;
    public static ChatLogin loginFrame = new ChatLogin();
    public static ChatFrame chatFrame = new ChatFrame();
    public static String server;
    public static int port;
    public static String username;
    public static BufferedReader input;
    public static Thread read;
    public static PrintWriter output;
    public static Socket socket;

    public static void main() {

        frame = new JFrame("ChatLogin");
        frame.setSize(400, 800);

        chatFrame.textArea.setContentType("text/html");
        chatFrame.userlist.setContentType("text/html");

        frame.setContentPane(loginFrame.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public static void connect(String server, int port, String username) {

        try {
            ClientGui.server = server;
            ClientGui.port = port;
            ClientGui.username = username;
            chatFrame.servername.setText(server);

            appendToPane(chatFrame.textArea, "<span>Connecting to " + server + " on port " + port + "...</span>");
            socket = new Socket(server, port);

            appendToPane(chatFrame.textArea, "<span>Connected to " +
                    socket.getRemoteSocketAddress() + "</span>");

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // send nickname to server
            output.println(username);

            // create new Read Thread
            read = new de.slowloris.chat.gui.Read();
            read.start();

            // recreate frame for "ChatFrame" panel
            chatFrame.getPanel().setSize(frame.getSize());
            frame.setContentPane(chatFrame.getPanel());
            frame.setSize(frame.getWidth(), frame.getHeight());
            frame.pack();

        } catch (Exception ex) {
            appendToPane(chatFrame.textArea, "<span>Could not connect to Server</span>");
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }
    }

    public static void disconnect(){

        chatFrame = new ChatFrame();

        loginFrame.getPanel().setSize(frame.getSize());
        frame.setContentPane(loginFrame.getPanel());
        frame.pack();
        output.close();
        read.stop();
    }

}
