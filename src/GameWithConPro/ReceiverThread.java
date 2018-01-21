/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameWithConPro;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static GameWithConPro.Client_StartGame.*;

/**
 *
 * @author rajesh
 */
public class ReceiverThread implements Runnable {

    Socket socket;
    public static byte[] response = new byte[4];
    String name;
    static byte[] NameByte;
    int interrupCounter=0;
    public ReceiverThread(Socket sckt) {
        this.socket = sckt;
        System.out.println("Receiver Thread Started!\n");
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            while (loop) {
                System.out.println("waiting for Message");
                inStream.read(response, 0, 3);
                System.out.println("Response-1 : {0} " + response[0] + " {1} " + response[1] + " {2} " + response[2]);
                switch (response[0]) {
                    case 5:
                         interrupCounter++;
                         if(interrupCounter >= 2)
                         {
                          JFrame frame = new JFrame();
                          JOptionPane.showMessageDialog(frame, "Server is Going to Shutdown!", "Interruption", JOptionPane.WARNING_MESSAGE);  
                         }
                         else{
                             System.out.println("Server is Going to Shutdown!");
                         }
                        
                        break;
                    case 1:
                        System.out.println("Game Started and Level : " + ClientInput + " Selected!");
                        msg_area.setText("Level " + ClientInput + " Selected!\n");
                        msg_area.append("Please guess the Number ->>\n");
                        if (response[2] == (byte) 1) {
                            System.out.println("\nYou are already in Game.!!\nPlease finish the runing one first.");
                            // System.out.println("Response : {0} " + response[0] + " {1} " + response[1] + " {2} " + response[2]);
                        }
                        break;
                    case 2:
                            switch (response[2]) {
                                case 0:
                                    System.out.println("\nyour guess is too big! try again.\n");
                                    msg_area.append("\nyour guess is too big! try again.\n");
                                    msg_area.append("Please guess the Number ->>\n");

                                    break;
                                case 1:
                                    System.out.println("\nyour guess is too low! try again.\n");

                                    msg_area.append("\nyour guess is too low! try again.\n");
                                    msg_area.append("Please guess the Number ->>\n");
                                    break;
                                case 2:
                                    System.out.println("\nPerfect! \nWhat is your name? \nName : ");
                                    msg_area.append("\nPerfect! \nWhat is your name? \n");
                                    sendName = true;
                                    System.out.println("Response : {0} " + response[0] + " {1} " + response[1] + " {2} " + response[2]);
    //                            Byte bNameRes = new Byte(response[1]);
    //                            int afterGetName = bNameRes.intValue();
    //                            if (afterGetName == 1) {
    //                                System.out.println(" Server Say's: Thanks!\n ");
    //                            }

                                    break;
                                /*The Java byte type is an 8 bit signed integral type with values in the range -128 to +127.
                                    The literal 0xff represents +255 which is outside of that range.*/
                                case 125:
                                    Byte bNameRes = new Byte(response[1]);
                                    int afterGetName = bNameRes.intValue();
                                    if (afterGetName == 1) {
                                        System.out.println("  ");
                                        msg_area.setText("Server Say's: Thanks! " + ClientInput);
                                        msg_send.setEnabled(false);
                                    }
                                    break;
                            }
                     break;
                    case 3:
                        System.out.println("{0} " + response[0] + " {1} " + response[1] + " {2} " + response[2]);
                        Byte bHighSc = new Byte(response[2]);
                        int HighScore = bHighSc.intValue();
                        if (HighScore == 0) {
                            System.out.println("\nNo highScore generated!\n");

                            score_show.setText("No Score Generated!!\n");
                        } else {
                            System.out.println("\nPrinting from RcvrTh --> your score is : " + HighScore + " Bravo!!\n");
                            score_show.setText("your score is : " + HighScore + " Bravo!!");

                        }
                        break;
//                    case 4:
//                        if (response[2] == 1) {
//                            System.out.println(" Server Say's: GoodBye!\n ");
//                            socket.close();
//                            loop = false;
//                        }
//                        break;

                }
                System.out.println("Response : " + "{0} " + response[0] + " {1} " + response[1] + " {2} " + response[2]);
            }

        } catch (IOException ex) {
            Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
