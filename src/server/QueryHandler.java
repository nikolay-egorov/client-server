package server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class QueryHandler implements Runnable {

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


    private void Send(String message) {
        this.writer.println(message);
        System.out.println("[SERVER RESPOND] " + message);
    }

    private void handleQuery() {
        String fileName = "";
        // Retrieve filename from terminal query.
        int separatorPos = this.message.lastIndexOf("::");
        if ((separatorPos > 0) && this.message.contains("::")) {
            if (!this.message.endsWith("::")) {
                fileName = this.message.substring(separatorPos + 2);
            }
        }

        if (this.message.startsWith("requesting::")) {
            SendFileToUser(fileName);
        } else if (this.message.startsWith("requestlist::")) {
            SendImageList();
        } else {
            Send("Invalid query: " + this.message);
        }

    }


    private void ReadImagesFromFile() {

        System.out.println("Indexing own base...");
        File dir = new File(this.dirPath + "/images");
        this.fileList = dir.listFiles();
        this.imageList = new ArrayList<String>(this.fileList.length);
        for (File file : this.fileList) {
            if (file.isFile()) {
                System.out.println(" adding " + file.getName());
                this.imageList.add(file.getName());
            }
        }
        System.out.println();

    }

    public void SendFileToUser(String fileName) {
        try {
            int dotLocation = fileName.lastIndexOf(".");
            String fileType = "";
            if (dotLocation > 0) {
                fileType = fileName.substring(dotLocation + 1);
            }
            System.out.println("Detected file type: " + fileType);
            System.out.println("Analyzing file: /images/" + fileName);
            BufferedImage img = ImageIO.read(new File(this.dirPath + "/images/" + fileName));
            System.out.println("Converting image to byte array output stream.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, fileType, baos);
            baos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();
            System.out.println("Byte array with " + bytes.length + " length created.");

            System.out.println("Opening new socket to connect to user.");
            Socket soc = new Socket(this.clientAddress, 4000);
            System.out.println("Opening stream with user.");
            OutputStream outStream = soc.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outStream);

            System.out.println("Writing to user stream.");
            dos.writeInt(bytes.length);
            dos.write(bytes, 0, bytes.length);
            System.out.println("Closing stream and socket.");
            dos.close();
            outStream.close();
            soc.close();
            writeToLog("image sent", fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void SendImageList() {
        ReadImagesFromFile();
        Send(Integer.toString(imageList.size()));
        for (String anImageList : this.imageList) {
            Send(anImageList);
        }
        Send("stop::");
    }

    private void writeToLog(String functionRequested, String filename) {
        File log = new File("servlog.txt");

        if (!log.exists()) {
            try {
                PrintWriter writer = new PrintWriter("servlog.txt", "UTF-8");
                writer.write("");
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Create a new Date object and read date and time from it as strings.
        Date currentDateTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String request = functionRequested + " " + filename;
        String date = dateFormat.format(currentDateTime);
        String time = timeFormat.format(currentDateTime);
        String toLog = date + " : " + time + " : " + this.clientAddress + " : " + request + "\n";
        // Write to serverlog.txt
        try {
            Files.write((Paths.get("servlog.txt")), toLog.getBytes(), StandardOpenOption.APPEND);
            System.out.println("Event has been logged.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                this.message = reader.nextLine();
                while (this.message != null) {
                    System.out.println("Server read: " + this.message + "\n");
                    handleQuery();
                    System.out.println("Listening for message...");
                    this.message = reader.nextLine();
                }
            } catch (NoSuchElementException nsee) {
                reader.close();
                writer.close();
                System.out.println("Client disconnected.");
                writeToLog("client disconnected", "");
                break;
            }
        }
    }


}
