package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String hostServ;
    private Scanner socketInbound;
    private PrintWriter socketOut = null;

    public Client(String host, int port) {
        hostServ=host;
        try {
            Socket socket = new Socket(host, port);
            socketInbound = new Scanner(socket.getInputStream());
            socketOut = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void getImageList(){
        int length = Integer.parseInt(socketInbound.nextLine());

    }

    public void downloadFromServer(String fileName){
        socketOut.println("requesting::" + fileName);
        try {
            System.out.println("Opening the new socket for connection...");
            ServerSocket server = new ServerSocket(3001);
            Socket socket = server.accept();
            System.out.println(" Connection with server established.\nRetrieving input stream...");
            InputStream inStream = socket.getInputStream();
            DataInputStream dis = new DataInputStream(inStream);

            int dataLength = dis.readInt();
            byte[] data = new byte[dataLength];
            dis.readFully(data);
            dis.close();
            inStream.close();
            System.out.println(" Finished receiving input stream.\nConverting to file...");



            //TODO conversation
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
