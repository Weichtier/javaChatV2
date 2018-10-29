/*
 * Copyright (c) 2018 Slowloris.de
 *
 * Development: Weichtier
 *
 * You're allowed to edit the Project.
 * Its not allowed to reupload this Project!
 */

package de.slowloris.chat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.slowloris.chat.client.Client;
import de.slowloris.chat.gui.ClientGui;
import de.slowloris.chat.server.Server;



import java.io.*;

public class Main {
    private static double version;
    public static void main(String[] args) throws IOException {
        if(args.length == 1){

            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(new InputStreamReader(Main.class.getResourceAsStream("/resources/app.json")), JsonObject.class);
            version = obj.get("version").getAsDouble();

            if(args[0].equalsIgnoreCase("server")){

                Server.main();

            }else if(args[0].equalsIgnoreCase("consoleclient")){

                Client.main();

            }else if(args[0].equalsIgnoreCase("gui")){

                ClientGui.main();

            }else {
                System.out.println("Please type which type of Application you want (gui|consoleclient|server)");
                System.exit(0);
            }
        }else {
            System.out.println("Please type which type of Application you want (gui|consoleclient|server)");
            System.exit(0);
        }
    }

    public static double getVersion() {
        return version;
    }
}
