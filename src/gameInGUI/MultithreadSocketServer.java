/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameInGUI;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author rajesh
 */
public class MultithreadSocketServer {

    public static void main(String[] args) throws Exception {
        int PortNum = 9090;
        int counter = 0;
        try {
            ServerSocket server = new ServerSocket(PortNum);
            System.out.println("Waiting for client on port " + server.getLocalPort() + "...");
            while (true) {
                counter++;
                Socket serverClient = server.accept();
                System.out.println("Just connected to " + serverClient.getRemoteSocketAddress());
                System.out.println(" >> " + "Client No:" + counter + " started!");
                Thread t = new Thread(new GameRules(serverClient, counter));
                t.start();
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
