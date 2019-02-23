package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String hostServ;
    private Scanner socketInbound;

    public Client(String host, int port) {
        hostServ=host;
        try {
            Socket socket = new Socket(host, port);
            socketInbound = new Scanner(socket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void getImageList(){
        int length = Integer.parseInt(socketInbound.nextLine());
        
    }
}
