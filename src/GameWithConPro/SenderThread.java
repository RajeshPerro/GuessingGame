/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameWithConPro;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author rajesh
 */
public class SenderThread implements Runnable {

    public BlockingQueue<Message> queue;
    Socket serverClient;
    int poisonPill = Integer.MAX_VALUE;
    public static Message msg = new Message();

    public SenderThread(Socket inSocket, BlockingQueue<Message> que) {
        this.queue = que;
        this.serverClient = inSocket;

    }

    @Override
    public void run() {
        try {
            DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());

            System.out.println("\nSender Thread Started!!\n");
            Message m = null;
            while (true) {
                
                if(!queue.isEmpty())
                {
                System.out.println("\nQueue Size :" + queue.size());
                m = queue.poll();
                
                outStream.write(m.getServerResponse(), 0, 3);
                
                outStream.flush();
                
                System.out.println("Data from queue :"+ Arrays.toString(m.getServerResponse())+"\n Data Sent!");
                
                }
                //Thread.sleep(1000);
                
                else
                {
                    
                   // outStream.write(m.getServerResponse(), 0, 3);
                    System.out.println("I am here! Nothing remain in Queue\n");
                    System.out.println("\nQueue Size :" + queue.size());
                    Thread.sleep(5000);
                    //break;
                    
                }
                //break;
            }
           

        } catch (Exception e) {
        }

    }
}
