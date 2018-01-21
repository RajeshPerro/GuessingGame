/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameInGUI;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;

public class GameRules implements Runnable {

    Socket serverClient;
    int clientNo;
    byte[] clientRspPart1 = new byte[2];
    byte[] clientRspPart2 = new byte[2];
    byte[] GuessNumberRead;
    byte[] serverResponse = new byte[4];


    public GameRules(Socket inSocket, int counter) {
        this.serverClient = inSocket;
        this.clientNo = counter;
        System.out.println("Gettind data : " + serverClient.toString() + "Client number : " + clientNo);
    }

    @Override
    public void run() {
        try {
            Random random = new Random();

            DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
            DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            boolean loop = true, started = false;
            int randomGenNum = 0, tryCount = 0, level = 0;
            while (loop) {

                inStream.read(clientRspPart1, 0, 2);
                //byte temp = clientResponse[0];
                Byte b = new Byte(clientRspPart1[0]);
                int FirstPartOfResponse = b.intValue();

                //to identify same things with clinet //sob somoy 0 number ghor manage hocche eikhane..
                serverResponse[0] = clientRspPart1[0];

                switch (FirstPartOfResponse) {

//***************Start the game***************
                    case 1:
                        if (started) //jodi started true hoi client trying to start game again!!
                        {
                            System.out.println("Signal from Client :" + FirstPartOfResponse);
                            serverResponse[0] = 1;
                            serverResponse[1] = 0;
                            serverResponse[2] = 1;
                            outStream.write(serverResponse, 0, 3);
                        } else {
                            System.out.println("Signal from Client :" + FirstPartOfResponse);
                            serverResponse[0] = 1;
                            serverResponse[1] = 0;
                            serverResponse[2] = 0;
                            outStream.write(serverResponse, 0, 3);
                            started = true;

                            //***************Based on client level generating randomNumber......*******
                            inStream.read(clientRspPart2, 0, 2);
                            for (byte c : clientRspPart2) {
                                //System.out.println("ClientResponse part 2 : " + c);
                                Byte bl = new Byte(c);
                                level = bl.intValue();
                                System.out.println("Int : " + level);
                            }
                            //Byte bl = new Byte(clientRspPart2[1]);

                            System.out.println("Level Choosen by Client : " + level);
                            //Beginner lever 
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
                    case 2:
                        if (started) {
                            inStream.read(clientRspPart2, 0, 1);
                            Byte bg = new Byte(clientRspPart2[0]);
                            int tempClienGuessLen = bg.intValue();
                            System.out.println("Length = " + tempClienGuessLen);
                            GuessNumberRead = new byte[tempClienGuessLen];
                            inStream.read(GuessNumberRead);
//                            for (byite c : GuessNumberRead) {
//                                System.out.println("Guess num array : "+c);
//                            }
                            byte[] byteArray1 = new byte[tempClienGuessLen];
                            for (int i = 0; i < tempClienGuessLen; i++) {
                                byteArray1[i] = GuessNumberRead[i];

                            }

                            String str1 = new String(byteArray1);

                            if (isInteger(str1)) {
                                int tempClienGuess = Integer.parseInt(str1);
                                System.out.println("Guess number = " + tempClienGuess);
                                tryCount++;
                                if (randomGenNum > tempClienGuess) {
                                    serverResponse[1] = 0;
                                    serverResponse[2] = 1;

                                } else if (randomGenNum < (tempClienGuess / 2)) {
                                    serverResponse[1] = 0;
                                    serverResponse[2] = 0;
                                } else if (randomGenNum == tempClienGuess) {
                                    serverResponse[1] = 0;
                                    serverResponse[2] = 2;
                                }

                                outStream.write(serverResponse, 0, 3);
                                outStream.flush();

                            } else {
                                System.out.println("User Name : " + str1);
                                serverResponse[1] = 1;
                                serverResponse[2] = 127;
                                outStream.write(serverResponse, 0, 3);
                                outStream.flush();
                                started = false;
                            }

                        } else {

                            System.out.println("Game didn't started  but client press (2)");
                            serverResponse[1] = 0;
                            serverResponse[2] = 127;
                            outStream.write(serverResponse, 0, 3);
                        }
                        break;
//***************Client Want to know the highScore***************  
                    case 3:
                        System.out.println("Client want to know the rangking..");
                        System.out.println("Try : " + tryCount + "random Number : " + randomGenNum + "Level : " + level);

                        int highScore = (level * (randomGenNum / 10) / tryCount);
                        serverResponse[1] = 0;
                        serverResponse[2] = (byte) highScore;

                        if (tryCount == 0) {
                            serverResponse[1] = 0;
                            serverResponse[2] = 0;
                        }
                        outStream.write(serverResponse, 0, 3);
                        System.out.println("Server data : "+"{0} "+serverResponse[0]+" {1} "+serverResponse[1]+" {2} "+serverResponse[2]);
                        outStream.flush();
                        break;
//***************Client want to end the game***************
                    case 4:
                        Byte by = new Byte(clientRspPart1[1]);
                        int SecondValueOfRes = by.intValue();
                        if (SecondValueOfRes == 1) {
                            System.out.println("Client Say's : Bye");
                        }
                        serverResponse[1] = 0;
                        serverResponse[2] = 1;
                        outStream.write(serverResponse, 0, 3);
                        outStream.flush();
                        loop = false;
                        break;
//                    default:
//                        serverResponse[1]=0;
//                        serverResponse[2]=4;
//                        outStream.write(serverResponse, 0, 3);
//                        outStream.flush();
//                        loop = false;

                }

            }
            
                inStream.close();
                outStream.close();
                serverClient.close();
            
            //here I will add close connection things..

        } catch (Exception e) {
            System.err.println("Erro in server : " + e);
        } finally {
            System.out.println("Client =" + clientNo + " exit!! ");
            clientNo--;
        }

    }

//    public static void main(String[] args) {
//        int portnum = 6060;
//        Thread t1 = new Thread(new GameRules(portnum));
//        t1.start();
//    }
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
