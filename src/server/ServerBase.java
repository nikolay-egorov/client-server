package server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerBase {

    private ServerSocket serverSocket = null;


    public ServerBase(int port) {
        try {
            serverSocket= new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ServerBase s= new ServerBase(3221);
    }
}
