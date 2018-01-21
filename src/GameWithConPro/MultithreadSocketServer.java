/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameWithConPro;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author rajesh
 */
public class MultithreadSocketServer {
    public static int counter = 0;
    public static void main(String[] args) throws Exception {
        int PortNum = 9090;
        BlockingQueue<Message> queue = new LinkedBlockingDeque<>();;
        try {
            ServerSocket server = new ServerSocket(PortNum);
            System.out.println("Waiting for client on port " + server.getLocalPort() + "...");
            while (true) {
                counter++;
                Socket serverClient = server.accept();
                System.out.println("Just connected to " + serverClient.getRemoteSocketAddress());
                System.out.println(" -->>>> " + "Client No:" + counter + " started!--->>>>>>");
//                Thread t = new Thread(new GameRules(serverClient));
//                t.start();
               
                //All thread begin here......-->>>>>>>>>>>
                GameRules gamerul = new GameRules(serverClient, queue);
                InteruptionTh intupth = new InteruptionTh(queue);
                
                Thread tgame = new Thread(gamerul);
                Thread tintp = new Thread(intupth);
                
                tgame.start();
               
                //tsndr.start();
                tintp.start();
            
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
