package client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    private WallChanger changer;


    // experimental
    public ArrayList<String> imageList = null;
    public File[] fileList = null;

    public Client(String host, int port) {
        hostServ=host;
        dirPath = System.getProperty("user.dir");
        changer=new WallChanger();
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
            cacheImages();
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
            cacheImages();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void sendToServer(String fileName)
    {
        socketOut.println("sending::" + fileName);
        try
        {
            int dot = fileName.lastIndexOf(".");
            String fileType = "";
            if (dot > 0)
            {	fileType = fileName.substring(dot+1);
            }
            System.out.println("Detected file type: " + fileType);
            System.out.println("Reading file: /images/" + fileName);
            BufferedImage img = ImageIO.read(new File(this.dirPath + "/images/" + fileName));
            System.out.println("Converting image to byte array output stream.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, fileType, baos);
            baos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();
            System.out.println("Byte array of length " + bytes.length + " created.");

            System.out.println("Opening new socket to connect to server.");
            Socket soc = new Socket(this.hostServ, 4000);
            System.out.println("Opening streams with server.");
            OutputStream outStream = soc.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outStream);

            System.out.println("Writing to server stream.");
            dos.writeInt(bytes.length);
            dos.write(bytes, 0, bytes.length);
            System.out.println("Closing streams/socket.");
            dos.close();
            outStream.close();
            soc.close();
        }

        catch (Exception e)
        {	System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void cacheImages()
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

    protected void queryTaker()
    {
        String messageOut;
        while ((messageOut = keyIn.nextLine()) != null)
        {
            String fileName = "";
            boolean error = true;
            if (messageOut.contains("::"))
            {
                int separatorLocation = messageOut.lastIndexOf("::");
                if (!messageOut.endsWith("::"))
                {
                    fileName = messageOut.substring(separatorLocation+2);
                }

                if (messageOut.equals("requestlist::"))
                {
                    error = false;
                    System.out.println("[CLIENT OUTPUT] " + messageOut + "\n");
                    System.out.println("  Available images to download:");
                    getImageList();
                }

                if (messageOut.startsWith("requesting::") && serverImageList.contains(fileName))
                {
                    error = false;
                    System.out.println("[CLIENT OUTPUT] " + messageOut);
                    downloadFromServer(fileName);
                }

                if (messageOut.startsWith("sending::") && imageList.contains(fileName))
                {
                    error = false;
                    System.out.println("Not implemented yet");
                    sendToServer(fileName);
                }
            }

            if (error)
            {
                System.out.println("\n  Invalid requests. Please enter one of the following:");
                System.out.println("   * requesting::[filename] ");
                System.out.println("   * sending::[filename]");
                System.out.println("   * requestlist::");
            }
        }
    }


}
