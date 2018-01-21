/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameWithConPro;

import java.io.DataInputStream;
import java.io.IOException;

import java.net.Socket;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import static GameWithConPro.MultithreadSocketServer.*;

public class GameRules implements Runnable {

    public BlockingQueue<Message> queue;
    Semaphore mutex = new Semaphore(1);
    Socket serverClient;
    int clientNo;

    byte[] clientRspPart1 = new byte[2];
    byte[] clientRspPart2 = new byte[2];
    byte[] GuessNumberRead;
    byte[] serverResponse;

    public GameRules(Socket inSocket, BlockingQueue<Message> q) {
        this.serverClient = inSocket;
        this.queue = q;
        System.out.println("Gettind data : " + serverClient.toString() + "Client number : " + counter);
    }

    @Override
    public void run() {
        try {

            DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
            boolean loop = true, started = false;
            Random random = new Random();
            int randomGenNum = 0, tryCount = 0, level = 0;
            
            SenderTh();
           
            while (loop) {
                inStream.read(clientRspPart1, 0, 2);
                Byte b = new Byte(clientRspPart1[0]);
                int FirstPartOfResponse = b.intValue();
                switch (FirstPartOfResponse) {
                    //***************Start the game***************
                    case 1:
                        if (started) {
                            //if client again want to start a game before finishing the current one. server sendind: 1 0 1 
                            setMsg((byte) 1, (byte) 0, (byte) 1);
                        } else {
                            //client want to start the game server is sending: 1 0 0 -> yes signal!  
                            setMsg((byte) 1, (byte) 0, (byte) 0);
                            started = true;
                            //Now reading the 2nd part of client message to check the level selected by client
                            inStream.read(clientRspPart2, 0, 2);
                            for (byte c : clientRspPart2) {
                                Byte bl = new Byte(c);
                                level = bl.intValue();
                                
                            }
                            System.out.println("Level Choosen by Client : " + level);
                            if (level == 1) {
                                randomGenNum = random.nextInt(10 * 2) + 1;
                                System.out.println("Random Numebr : " + randomGenNum);

                            } else if (level == 2) {
                                randomGenNum = random.nextInt(100 * 2) + 1;
                                System.out.println("Random Numebr : " + randomGenNum);
                            } else {
                                randomGenNum = random.nextInt(1000 * 2) + 1;
                                System.out.println("Random Numebr : " + randomGenNum);
                            } 
                            
                        }
                        break;
                    //***************Playing the game client gueesing number you have to send response ***************  
                    case 2 : 
                        if(started)
                        {
                           inStream.read(clientRspPart2, 0, 1);
                           Byte bg = new Byte(clientRspPart2[0]);
                           int tempClienGuessLen = bg.intValue();
                           //System.out.println("Length of the guess number = " + tempClienGuessLen);
                           GuessNumberRead = new byte[tempClienGuessLen];
                           inStream.read(GuessNumberRead);
                           byte[] byteArray1 = new byte[tempClienGuessLen];
                            for (int i = 0; i < tempClienGuessLen; i++) 
                            {
                                byteArray1[i] = GuessNumberRead[i];
                            }

                            String str1 = new String(byteArray1);
                            
                            if(isInteger(str1))
                            {
                                int tempClienGuess = Integer.parseInt(str1);
                                System.out.println("Guess number = " + tempClienGuess);
                                tryCount++;
                                if (randomGenNum > tempClienGuess) {
                                    setMsg((byte) 2, (byte) 0, (byte) 1);
                                }
                                else if (randomGenNum < (tempClienGuess / 2)) {
                                    setMsg((byte) 2, (byte) 0, (byte) 0);
                                }
                                else if (randomGenNum == tempClienGuess) {
                                    setMsg((byte) 2, (byte) 0, (byte) 2);
                                }
                            }
                            else {
                                System.out.println("User Name : " + str1);
                                setMsg((byte) 2, (byte) 1, (byte) 125);
                                started = false;
                            }
                        }
                        else{
                            System.out.println("Game didn't started  but client press (2)\n");
                            setMsg((byte) 1, (byte) 0, (byte) 127);
                        }
                        break;
   //***************Client Want to know the highScore***************  
                    case 3 : 
                        System.out.println("Client want to know the Score..");
                        System.out.println("Try : " + tryCount + "random Number : " + randomGenNum + "Level : " + level);
                        int highScore = (level * (randomGenNum / 10) / tryCount);
                        setMsg((byte) 3, (byte) 0, (byte) highScore);
                        if (tryCount == 0) {
                        setMsg((byte) 3, (byte) 0, (byte) 0);
                        }
                        break;
       
            //***************Client want to end the game***************                
                    case 4 :
                        Byte by = new Byte(clientRspPart1[1]);
                        int SecondValueOfRes = by.intValue();
                        if (SecondValueOfRes == 1) {
                            System.out.println("Client Say's : Bye");
                        }
                        setMsg((byte) 4, (byte) 0, (byte) 1);
                        loop = false;
                        break;
                }
            }
            //inStream.close();
            //serverClient.close();
        } catch (IOException ex) {
            Logger.getLogger(GameRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(GameRules.class.getName()).log(Level.SEVERE, null, ex);
        }finally {

            System.out.println("-->>>One Client exit!!-->>>> ");
            try {
                mutex.acquire();
                counter=counter-1;
                mutex.release();
            }catch (InterruptedException ex) {
                Logger.getLogger(GameRules.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Total Connected Client now :" + counter);
        }

    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void SenderTh() {
        SenderThread sender = new SenderThread(serverClient, queue);
        Thread tsndr = new Thread(sender);
        tsndr.start();
    }

    public void setMsg(byte a, byte b, byte c) throws InterruptedException {
        
        serverResponse = new byte[3];

        serverResponse[0] = a;
        serverResponse[1] = b;
        serverResponse[2] = c;
        System.out.println("Response set to send : {0}" + serverResponse[0] + " {1}" + serverResponse[1] + " {2}" + serverResponse[2] + "\n");
        Message msg = new Message();
        msg.setServerResponse(serverResponse);
        queue.put(msg);
        //System.out.println("From the Queue : " + Arrays.toString(msg.getServerResponse()));
        

    }

}
