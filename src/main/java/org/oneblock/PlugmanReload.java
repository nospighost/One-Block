package org.oneblock;

import com.github.t9t.minecraftrconclient.RconClient;

public class PlugmanReload {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 25575;
        String password = "WC32sdfsdfhJLK";

        try (RconClient client = RconClient.open(host, port, password)) {
            String response = client.sendCommand("plugman reload OneBlock");
            System.out.println("Serverantwort: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
