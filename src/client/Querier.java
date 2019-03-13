package client;

import java.io.IOException;
import java.util.Scanner;

public class Querier {
    private String hostServ;
    private Client client = null;

    public Querier(String hostServ,int port) {
        this.hostServ = hostServ;
        client = new Client(hostServ,port);
        takeCare();
    }

    public void preprocess(){
        Scanner in= new Scanner(System.in);
        System.out.println("Welcome! Enter server address: ");
        String host ;
        int port;
        host = in.nextLine();
        System.out.println("And do specify the port: ");
        port = in.nextInt();
        client = new Client(hostServ,port);
    }

    public void takeCare(){
        client.queryTaker();
    }

    public static void main(String[] args) {
        Scanner in= new Scanner(System.in);
        System.out.println("Welcome! Enter server address: ");
        String host ;
        int port;
        host = in.nextLine();
        System.out.println("And do specify the port: ");
        port = in.nextInt();
        Querier querier=new Querier(host,port);

    }
}
