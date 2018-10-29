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
import de.slowloris.chat.gui.filter.IntFilter;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatLogin {
    public JPanel panel;
    public JButton connectButton;
    public JTextField serverField;
    public JTextField portField;
    public JTextField nickField;


    public ChatLogin() {

        PlainDocument doc = (PlainDocument) portField.getDocument();
        doc.setDocumentFilter(new IntFilter());

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientGui.connect(serverField.getText(), Integer.parseInt(portField.getText()), nickField.getText());
            }
        });

        serverField.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ClientGui.connect(serverField.getText(), Integer.parseInt(portField.getText()), nickField.getText());
                }
            }
        });

        portField.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ClientGui.connect(serverField.getText(), Integer.parseInt(portField.getText()), nickField.getText());
                }
            }
        });

        nickField.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ClientGui.connect(serverField.getText(), Integer.parseInt(portField.getText()), nickField.getText());
                }
            }
        });

    }

    public JPanel getPanel(){
        return panel;
    }
}
