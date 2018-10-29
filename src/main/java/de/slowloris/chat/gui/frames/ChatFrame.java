/*
 * Copyright (c) 2018 Slowloris.de
 *
 * Development: Weichtier
 *
 * You're allowed to edit the Project.
 * Its not allowed to reupload this Project!
 */

package de.slowloris.chat.gui.frames;

import de.slowloris.chat.gui.ClientGui;
import de.slowloris.chat.gui.ClientUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatFrame {
    public JTextPane textArea;
    public JTextField message;
    public JTextPane userlist;
    public JButton sendbutton;
    public JPanel panel;
    public JLabel servername;
    public JButton disconnectbutton;
    private JScrollBar scrollbar;

    public ChatFrame() {

        textArea.setEditable(false);
        textArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        userlist.setEditable(false);

        scrollbar.setEnabled(true);
        textArea.add(scrollbar, BorderLayout.CENTER);

        sendbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientUtils.sendMessage();
            }
        });

        message.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ClientUtils.sendMessage();
                }
            }
        });

        disconnectbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientGui.disconnect();
            }
        });

    }

    public JPanel getPanel(){
        return panel;
    }

}
