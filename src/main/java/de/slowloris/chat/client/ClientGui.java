package de.slowloris.chat.client;

import de.slowloris.chat.Main;
import org.jsoup.Jsoup;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Utilities;
import javax.swing.text.html.*;

import java.util.ArrayList;
import java.util.Arrays;


public class ClientGui extends Thread{

    private final JFrame frame;
    private final JTextPane jtextFilDiscu = new JTextPane();
    private final JTextPane jtextListUsers = new JTextPane();
    private final JTextField jtextInputChat = new JTextField();
    private String oldMsg = "";
    private Thread read;
    private String serverName;
    private int PORT;
    private String name;
    private BufferedReader input;
    private PrintWriter output;
    private Socket server;
    private boolean focused;

    private ClientGui() {

        this.serverName = "slowloris.de";
        this.PORT = 4587;
        this.name = "nickname";

        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);

        frame = new JFrame("SlowChat | v" + Main.getVersion());
        frame.getContentPane().setLayout(null);
        frame.setSize(700, 500);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setIcon("/resources/img/icon.png");

        jtextFilDiscu.setBounds(25, 25, 490, 320);
        jtextFilDiscu.setFont(font);
        jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
        jtextFilDiscu.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
        jtextFilDiscuSP.setBounds(25, 25, 490, 320);

        jtextFilDiscu.setContentType("text/html");
        jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        jtextFilDiscu.addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                System.out.println(e.getURL());
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        jtextListUsers.setBounds(520, 25, 156, 320);
        jtextListUsers.setEditable(true);
        jtextListUsers.setFont(font);
        jtextListUsers.setMargin(new Insets(6, 6, 6, 6));
        jtextListUsers.setEditable(false);
        JScrollPane jsplistuser = new JScrollPane(jtextListUsers);
        jsplistuser.setBounds(520, 25, 156, 320);

        jtextListUsers.setContentType("text/html");
        jtextListUsers.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // Field message user input
        jtextInputChat.setBounds(0, 350, 400, 50);
        jtextInputChat.setFont(font);
        jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
        final JScrollPane jtextInputChatSP = new JScrollPane(jtextInputChat);
        jtextInputChatSP.setBounds(25, 350, 650, 50);

        // button send
        final JButton sendbtn = new JButton("Send");
        sendbtn.setFont(font);
        sendbtn.setBounds(575, 410, 100, 35);

        // button Disconnect
        final JButton disconnectbtn = new JButton("Disconnect");
        disconnectbtn.setFont(font);
        disconnectbtn.setBounds(25, 410, 130, 35);

        jtextInputChat.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }

                // Get last message typed
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    String currentMessage = jtextInputChat.getText().trim();
                    jtextInputChat.setText(oldMsg);
                    oldMsg = currentMessage;
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String currentMessage = jtextInputChat.getText().trim();
                    jtextInputChat.setText(oldMsg);
                    oldMsg = currentMessage;
                }
            }
        });

        // Click on send button
        sendbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });

        // Connection view
        final JTextField txtfieldName = new JTextField(this.name);
        final JTextField txtfieldPort = new JTextField(Integer.toString(this.PORT));
        final JTextField txtfieldHost = new JTextField(this.serverName);
        final JButton jcbtn = new JButton("Connect");

        // check if those field are not empty
        txtfieldName.getDocument().addDocumentListener(new TextListener(txtfieldName, txtfieldPort, txtfieldHost, jcbtn));
        txtfieldPort.getDocument().addDocumentListener(new TextListener(txtfieldName, txtfieldPort, txtfieldHost, jcbtn));
        txtfieldHost.getDocument().addDocumentListener(new TextListener(txtfieldName, txtfieldPort, txtfieldHost, jcbtn));

        jcbtn.setFont(font);
        txtfieldHost.setBounds(25, 380, 135, 40);
        txtfieldName.setBounds(375, 380, 135, 40);
        txtfieldPort.setBounds(200, 380, 135, 40);
        jcbtn.setBounds(575, 380, 100, 40);

        jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
        jtextListUsers.setBackground(Color.LIGHT_GRAY);

        frame.add(jcbtn);
        frame.add(jtextFilDiscuSP);
        frame.add(jsplistuser);
        frame.add(txtfieldName);
        frame.add(txtfieldPort);
        frame.add(txtfieldHost);
        frame.setVisible(true);

        frame.addWindowFocusListener(new WindowAdapter() {


            public void windowGainedFocus(WindowEvent e) {
                setIcon("/resources/img/icon.png");
                focused = true;
            }

            public void windowLostFocus(WindowEvent e) {
                focused = false;
            }
        });

        jtextListUsers.addMouseListener(new MouseAdapter() {
                                            @Override
                                            public void mouseClicked(MouseEvent e) {
                                                try
                                                {

                                                    String clicked = null;
                                                    int point = jtextListUsers.viewToModel(e.getPoint());
                                                    int startPoint = Utilities.getWordStart(jtextListUsers, point);
                                                    int endPoint = Utilities.getWordEnd(jtextListUsers, point);

                                                    clicked = jtextListUsers.getText(startPoint, endPoint-startPoint);
                                                    if(jtextInputChat.getText().equalsIgnoreCase("")){
                                                        jtextInputChat.setText("@" + clicked);
                                                    }else {
                                                        if(jtextInputChat.getText().endsWith(" ")){
                                                            jtextInputChat.setText(jtextInputChat.getText() + "@" + clicked);
                                                        }else {
                                                            jtextInputChat.setText(jtextInputChat.getText() + " @" + clicked);
                                                        }
                                                    }
                                                }
                                                catch(Exception exception)
                                                {
                                                    exception.printStackTrace();
                                                }
                                            }
                                        });


                appendToPane(jtextFilDiscu, "<h4>Befehle die du benutzen kannst:</h4>"
                        + "<ul>"
                        + "<li><b>@nickname</b> Schreibe eine Private Nachricht an markierte Person</li>"
                        + "<li><b>#d3961b</b> änder deine Farbe (Hexcodes wie bei HTML)</li>"
                        + "<li><b>;)</b> Smileys :)</li>"
                        + "<li><b>Pfeil nach oben</b> letzte von dir geschriebene Nachricht zeigen</li>"
                        + "</ul><br/>");

        // On connect
        jcbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    name = txtfieldName.getText();
                    String port = txtfieldPort.getText();
                    serverName = txtfieldHost.getText();
                    PORT = Integer.parseInt(port);

                    appendToPane(jtextFilDiscu, "<span>Connecting to " + serverName + " on port " + PORT + "...</span>");
                    server = new Socket(serverName, PORT);

                    appendToPane(jtextFilDiscu, "<span>Connected to " +
                            server.getRemoteSocketAddress()+"</span>");

                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(), true);

                    // send nickname to server
                    output.println(name);

                    // create new Read Thread
                    read = new Read();
                    read.start();
                    frame.remove(txtfieldName);
                    frame.remove(txtfieldPort);
                    frame.remove(txtfieldHost);
                    frame.remove(jcbtn);
                    frame.add(sendbtn);
                    frame.add(jtextInputChatSP);
                    frame.add(disconnectbtn);
                    frame.revalidate();
                    frame.repaint();
                    jtextFilDiscu.setBackground(Color.WHITE);
                    jtextListUsers.setBackground(Color.WHITE);
                } catch (Exception ex) {
                    appendToPane(jtextFilDiscu, "<span>Could not connect to Server</span>");
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }

        });

        disconnectbtn.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent ae) {
                frame.add(txtfieldName);
                frame.add(txtfieldPort);
                frame.add(txtfieldHost);
                frame.add(jcbtn);
                frame.remove(sendbtn);
                frame.remove(jtextInputChatSP);
                frame.remove(disconnectbtn);
                frame.revalidate();
                frame.repaint();
                read.interrupt();
                jtextListUsers.setText(null);
                jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
                jtextListUsers.setBackground(Color.LIGHT_GRAY);
                appendToPane(jtextFilDiscu, "<span>Connection closed.</span>");
                output.close();
            }
        });

    }

    // check if if all field are not empty
    class TextListener implements DocumentListener{
        final JTextField jtf1;
        final JTextField jtf2;
        final JTextField jtf3;
        final JButton jcbtn;

        public TextListener(JTextField jtf1, JTextField jtf2, JTextField jtf3, JButton jcbtn){
            this.jtf1 = jtf1;
            this.jtf2 = jtf2;
            this.jtf3 = jtf3;
            this.jcbtn = jcbtn;
        }

        public void changedUpdate(DocumentEvent e) {}

        public void removeUpdate(DocumentEvent e) {
            if(jtf1.getText().trim().equals("") ||
                    jtf2.getText().trim().equals("") ||
                    jtf3.getText().trim().equals("")
                    ){
                jcbtn.setEnabled(false);
            }else{
                jcbtn.setEnabled(true);
            }
        }
        public void insertUpdate(DocumentEvent e) {
            if(jtf1.getText().trim().equals("") ||
                    jtf2.getText().trim().equals("") ||
                    jtf3.getText().trim().equals("")
                    ){
                jcbtn.setEnabled(false);
            }else{
                jcbtn.setEnabled(true);
            }
        }

    }

    private void sendMessage() {
        try {
            String message = jtextInputChat.getText().trim();

            if(message.startsWith("!")){
                String[] input = message.substring(1).split(" ");
                String command = input[0];
                String[] args = new String[0];
                System.arraycopy(input, 1, args, 0, input.length - 1);

                if(command.equalsIgnoreCase("clearchat")){

                    jtextFilDiscu.setText("");

                }else if(command.equalsIgnoreCase("help")){

                    appendToPane(jtextFilDiscu, "Help:\n!clearchat : Clears the Chat field\n");

                }else {

                    appendToPane(jtextFilDiscu, "try !help for help");

                }

                jtextInputChat.requestFocus();
                jtextInputChat.setText(null);

                return;
            }
            if (message.equals("")) {
                return;
            }
            this.oldMsg = message;
            output.println(message);
            jtextInputChat.requestFocus();
            jtextInputChat.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    public static void main() {
        ClientGui client = new ClientGui();
    }

    // read new incoming messages
    class Read extends Thread {
        public void run() {
            String message;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    message = input.readLine();
                    if(message != null){
                        if (message.charAt(0) == '[') {
                            message = message.substring(1, message.length()-1);
                            ArrayList<String> ListUser = new ArrayList<String>(
                                    Arrays.asList(message.split(", "))
                            );
                            jtextListUsers.setText(null);
                            for (String user : ListUser) {
                                if(parseHTMLMessage(user).equalsIgnoreCase(getNickname())){
                                    appendToPane(jtextListUsers, user.replace(getNickname(), "<b>@" + getNickname() + "</b>"));
                                }else {
                                    appendToPane(jtextListUsers, "@" + user);
                                }
                            }
                        }else{
                            String parsed = parseHTMLMessage(message);
                            System.out.println(parsed);

                            String[] splited = parsed.split(" ");

                            for (String s : splited){
                                if(s.startsWith("http://") ||s.startsWith("https://")){

                                    message = message.replace(s, "<a href='" + s + "'>" + s + "</a>");

                                }else if(s.startsWith(":") && s.endsWith(":")){

                                    String link = s.substring(1, s.length() - 1);
                                    message = message.replace(s, "<a href='" + link + "'><img style='max-height:87px;max-width:87px;' src='" + link + "'></a>");

                                }else if(s.equalsIgnoreCase("@" + getNickname())){

                                    message = "<div style='background-color:yellow'>" + message.replace("@" + getNickname(), "<b>@" + getNickname() + "</b>") + "</div>";

                                    if(!focused){
                                        setIcon("/resources/img/icon_newmessage.png");
                                        playSound("received.wav");
                                    }
                                }
                            }

                            System.out.println(message);
                            if(parsed.startsWith(getNickname())){
                                appendToPane(jtextFilDiscu,  "<b>" + message + "</b>");
                            }else{
                                appendToPane(jtextFilDiscu, message);
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

    // send html to pane
    private void appendToPane(JTextPane tp, String msg){
        HTMLDocument doc = (HTMLDocument)tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            getClass().getResourceAsStream("/resources/sounds/" + url));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
    private String parseHTMLMessage(String html) {
        return Jsoup.parse(html).text();
    }

    private String getNickname() {
        return name;
    }

    private void setIcon(String url){
        URL iconURL = getClass().getResource(url);
        ImageIcon icon = new ImageIcon(iconURL);
        frame.setIconImage(icon.getImage());
    }
}
