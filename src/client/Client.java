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
    private String dirPath;
    public ArrayList<String> imageList = null;
    public File[] fileList = null;

    private WallChanger changer;


    public Client(String host, int port) {
        hostServ = host;
        dirPath = System.getProperty("user.dir");
        changer = new WallChanger();
        try {
            Socket socket = new Socket(host, port);
            socketInbound = new Scanner(socket.getInputStream());
            socketOut = new PrintWriter(socket.getOutputStream(), true);
            keyIn = new Scanner(System.in);
            cacheImages();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void getImageList() {
        socketOut.println("requestlist::");
        int length = Integer.parseInt(socketInbound.nextLine());
        this.serverImageList = new ArrayList<String>(length);
        this.messageIn = socketInbound.nextLine();
        System.out.println("Available images to download:");
        while (!this.messageIn.equals("stop::")) {
            System.out.println("    - " + this.messageIn);
            this.serverImageList.add(this.messageIn);
            this.messageIn = socketInbound.nextLine();
        }
        System.out.println("");

    }

    public void downloadFromServer(String fileName) {
        socketOut.println("requesting::" + fileName);
        String filePath = this.dirPath + "/images/" + fileName;
        try (ServerSocket server = new ServerSocket(51712);
             OutputStream toFile = new FileOutputStream(filePath);
        ) {

            System.out.println("Opening the new socket for connection...");

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
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = bais.read(buffer)) != -1) {
                System.out.println(" Bytes read of length: " + bytesRead);
                toFile.write(buffer, 0, bytesRead);
            }
            bais.close();
            toFile.flush();
            toFile.close();
            System.out.println(" ...Finished!\n");
            cacheImages();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void sendToServer(String fileName) {

        for (String item : serverImageList)
            if (item.equals(fileName)) {
                 return;
            }
        socketOut.println("sending::" + fileName);


        try {
            int dot = fileName.lastIndexOf(".");
            String fileType = "";
            if (dot > 0) {
                fileType = fileName.substring(dot + 1);
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
            Socket soc = new Socket(this.hostServ, 51712);
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
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void cacheImages() {
        File dir = new File(this.dirPath + "/images");
        this.fileList = dir.listFiles();
        this.imageList = new ArrayList<String>(this.fileList.length);
        System.out.println("Finding files in /images directory...");
        boolean found = false;
        for (File file : this.fileList) {
            if (file.isFile()) {
                String extension = "";
                int i = file.toString().lastIndexOf('.');
                if (i > 0) {
                    extension = file.toString().substring(i + 1);
                }
                System.out.println(" Found: " + file.getName());
                found = true;
                if (!extension.equals("db"))
                    this.imageList.add(file.getName());
            }
        }
        if (!found) {
            System.out.println("No files found!");
        }
        System.out.println();
    }

    /**
     * Reads input from terminal, which should be one of the following:
     * requesting::[filename], sending::[filename], requestlist::
     * Calls appropriate method if input is valid. Does not send
     * query to server if e.g. file does not exist.
     **/
    protected void queryTaker() {
        String messageOut;
        while ((messageOut = keyIn.nextLine()) != null) {
            String fileName = "";
            boolean error = true;
            if (messageOut.contains("::")) {
                int separatorLocation = messageOut.lastIndexOf("::");
                if (!messageOut.endsWith("::")) {
                    fileName = messageOut.substring(separatorLocation + 2);
                }

                if (messageOut.equals("requestlist::")) {
                    error = false;
                    System.out.println("[CLIENT OUTPUT] " + messageOut + "\n");
                    System.out.println("  Available images to download:");
                    getImageList();
                }

                if (messageOut.startsWith("requesting::") && serverImageList.contains(fileName)) {
                    error = false;
                    System.out.println("[CLIENT OUTPUT] " + messageOut);
                    downloadFromServer(fileName);
                }

                if (messageOut.startsWith("sending::") && imageList.contains(fileName)) {
                    error = false;
                    System.out.println("Ohoh");
                    sendToServer(fileName);
                }
            }

            if (error) {
                System.out.println("\n  Invalid requests. Expected one of the following:");
                System.out.println("   * requesting::[filename] ");
                System.out.println("   * sending::[filename]");
                System.out.println("   * requestlist::");
            }
        }
    }

    protected void setWallPaper(String fileName) throws IOException {
        System.out.println("Setting " + fileName + " as the main wallpaper... ");
        changer.changeWallpaper(dirPath + "/images/" + fileName);
        System.out.println("Successfully set");
    }


    /**
     * For debug and test purposes
     */
    public static void main(String[] args) {
        String host = "127.0.0.1";                 //args[0]
        Client ic = new Client(host, 6000);
        ic.queryTaker();
    }


}
