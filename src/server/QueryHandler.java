package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class QueryHandler implements Runnable{

    private Scanner reader = null;
    private PrintWriter writer = null;
    private String message = null;
    private InetAddress clientAddress = null;
    public ArrayList<String> imageList = null;
    public File[] fileList = null;
    private String dirPath = null;
    private Calendar calendar = null;


    public QueryHandler(Socket client) {
        clientAddress = client.getInetAddress();
        dirPath = System.getProperty("user.dir");
        try {
            reader = new Scanner(client.getInputStream());
            writer = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void Send(String message)
    {
        this.writer.println(message);
        System.out.println("[SERVER RESPOND] " + message);
    }

    private void handleQuery() {

    }



    private void ReadImagesFromFile()
    {


    }

    public void SendFileToUser(String fileName)
    {

    }



    public void SendImageList()
    {
        
    }







        @Override
    public void run() {

    }
}
