/*
 * Copyright (c) 2018 Slowloris.de
 *
 * Development: Weichtier
 *
 * You're allowed to edit the Project.
 * Its not allowed to reupload this Project!
 */

package de.slowloris.chat.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Read extends Thread{

    public void run() {
        String message;
        while(!Thread.currentThread().isInterrupted()){
            try {
                message = ClientGui.input.readLine();
                if(message != null){
                    if (message.charAt(0) == '[') {
                        message = message.substring(1, message.length()-1);
                        ArrayList<String> ListUser = new ArrayList<String>(
                                Arrays.asList(message.split(", "))
                        );
                        ClientGui.chatFrame.userlist.setText(null);
                        for (String user : ListUser) {
                            if(ClientUtils.parseHTMLMessage(user).equalsIgnoreCase(ClientGui.username)){
                                ClientUtils.appendToPane(ClientGui.chatFrame.userlist, user.replace(ClientGui.username, "<b>@" + ClientGui.username + "</b>"));
                            }else {
                                ClientUtils.appendToPane(ClientGui.chatFrame.userlist, "@" + user);
                            }
                        }
                    }else{
                        String parsed = ClientUtils.parseHTMLMessage(message);
                        System.out.println(parsed);

                        String[] splited = parsed.split(" ");

                        for (String s : splited){
                            if(s.startsWith("http://") ||s.startsWith("https://")){

                                message = message.replace(s, "<a href='" + s + "'>" + s + "</a>");

                            }else if(s.startsWith(":") && s.endsWith(":")){

                                String link = s.substring(1, s.length() - 1);
                                message = message.replace(s, "<a href='" + link + "'><img style='max-height:87px;max-width:87px;' src='" + link + "'></a>");

                            }else if(s.equalsIgnoreCase("@" + ClientGui.username)){

                                message = "<div style='background-color:yellow'>" + message.replace("@" + ClientGui.username, "<b>@" + ClientGui.username + "</b>") + "</div>";



                                /*

                                if(!focused){
                                    setIcon("/resources/img/icon_newmessage.png");
                                    playSound("received.wav");
                                }

                                */

                            }
                        }

                        System.out.println(message);
                        if(parsed.startsWith(ClientGui.username)){
                            ClientUtils.appendToPane(ClientGui.chatFrame.textArea,  "<b>" + message + "</b>");
                        }else{
                            ClientUtils.appendToPane(ClientGui.chatFrame.textArea, message);
                        }
                    }
                }
            }
            catch (IOException ex) {
                System.err.println("Failed to parse incoming message");
            }
        }
    }
}
