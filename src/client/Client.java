package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private String hostServ;
    private Scanner socketInbound;
    private PrintWriter socketOut = null;
    private Scanner keyIn = null;
    private String messageIn = null;
    public ArrayList<String> serverImageList = null;
    private String dirPath ;



    // experimental
    public ArrayList<String> imageList = null;
    public File[] fileList = null;

    public Client(String host, int port) {
        hostServ=host;
        dirPath = System.getProperty("user.dir");
        try {
            Socket socket = new Socket(host, port);
            socketInbound = new Scanner(socket.getInputStream());
            socketOut = new PrintWriter(socket.getOutputStream(), true);


            keyIn = new Scanner(System.in);
            String intro = socketInbound.nextLine();
            while (true)
            {	System.out.println(intro);
                intro = socketInbound.nextLine();
                if (intro.equals("stop::"))
                {
                    break;
                }
            }
            getImageList();
            //TODO read files
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void getImageList(){
        socketOut.println("requestlist::");
        int length = Integer.parseInt(socketInbound.nextLine());
        this.serverImageList = new ArrayList<String>(length);
        this.messageIn = socketInbound.nextLine();
        System.out.println("Available images to download:");
        while (!this.messageIn.equals("stop::"))
        {
            System.out.println("    - " + this.messageIn);
            this.serverImageList.add(this.messageIn);
            this.messageIn = socketInbound.nextLine();
        }
        System.out.println("");

    }

    public void downloadFromServer(String fileName){
        socketOut.println("requesting::" + fileName);
        try {
            System.out.println("Opening the new socket for connection...");
            ServerSocket server = new ServerSocket(3001);
            Socket socket = server.accept();
            System.out.println(" Connection with server is established.\nRetrieving input stream...");
            InputStream inStream = socket.getInputStream();
            DataInputStream dis = new DataInputStream(inStream);

            int dataLength = dis.readInt();
            byte[] data = new byte[dataLength];
            dis.readFully(data);
            dis.close();
            inStream.close();
            System.out.println(" Finished receiving input stream.\nConverting to file...");

            InputStream bais = new ByteArrayInputStream(data);
            String filePath = this.dirPath + "/images/" + fileName;
            OutputStream toFile = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = bais.read(buffer)) != -1)
            {	System.out.println(" Bytes read of length: " + bytesRead);
                toFile.write(buffer, 0, bytesRead);
            }
            bais.close();
            toFile.flush();
            toFile.close();
            server.close();
            System.out.println(" ...Finished!\n");

            //TODO conversation
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void updateImagesFromFile()
    {
        File dir = new File(this.dirPath + "/images");
        this.fileList = dir.listFiles();
        this.imageList = new ArrayList<String>(this.fileList.length);
        System.out.println("Finding files in /images directory...");
        boolean found = false;
        for (File file : this.fileList)
        {	if (file.isFile())
        {	System.out.println(" Found: " + file.getName());
            found = true;
            this.imageList.add(file.getName());
        }
        }
        if (!found)
        {
            System.out.println("No files found!");
        }
        System.out.println();
    }

    private void readImagesFromFile()
    {
        File dir = new File(this.dirPath + "/images");
        this.fileList = dir.listFiles();
        this.imageList = new ArrayList<String>(this.fileList.length);
        System.out.println("Finding files in /images directory...");
        boolean found = false;
        for (File file : this.fileList)
        {	if (file.isFile())
        {	System.out.println(" Found: " + file.getName());
            found = true;
            this.imageList.add(file.getName());
        }
        }
        if (!found)
        {
            System.out.println("No files found!");
        }
        System.out.println("");
    }
}
