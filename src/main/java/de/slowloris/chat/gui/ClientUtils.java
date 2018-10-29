/*
 * Copyright (c) 2018 Slowloris.de
 *
 * Development: Weichtier
 *
 * You're allowed to edit the Project.
 * Its not allowed to reupload this Project!
 */

package de.slowloris.chat.gui;

import org.jsoup.Jsoup;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class ClientUtils {


    public static void appendToPane(JTextPane tp, String msg){
        HTMLDocument doc = (HTMLDocument)tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String parseHTMLMessage(String html) {
        return Jsoup.parse(html).text();
    }

    public static void sendMessage() {
        try {
            String message = ClientGui.chatFrame.message.getText().trim();

            if(message.startsWith("!")){
                String[] input = message.substring(1).split(" ");
                String command = input[0];
                String[] args = new String[0];
                System.arraycopy(input, 1, args, 0, input.length - 1);

                if(command.equalsIgnoreCase("clearchat")){
                    ClientGui.chatFrame.textArea.setText("");
                }else if(command.equalsIgnoreCase("help")){
                    appendToPane(ClientGui.chatFrame.textArea, "Help:\n!clearchat : Clears the Chat field\n");
                }else {
                    appendToPane(ClientGui.chatFrame.textArea, "try !help for help");
                }
                ClientGui.chatFrame.message.requestFocus();
                ClientGui.chatFrame.message.setText(null);
                return;
            }


            if (message.equals("")) {
                return;
            }
            ClientGui.output.println(message);
            ClientGui.chatFrame.message.requestFocus();
            ClientGui.chatFrame.message.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

}
