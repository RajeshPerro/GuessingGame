/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameWithConPro;

/**
 *
 * @author rajesh
 */
public class Message 
{
    private byte[] serverResponse;
    

    public Message() 
    {
       
    }

    public void setServerResponse(byte[] serverResponse) {
        this.serverResponse = serverResponse;
    }

  
    public byte[] getServerResponse() {
//        System.out.println("getter called!\n");
        return serverResponse;
    }
    
}
