import client.WallChanger;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Hello World!");
        System.out.println(System.getProperty("os.name"));
        WallChanger changer=new WallChanger();
        changer.changeWallpaper("C:\\Users\\User\\Desktop\\subscription_girl-min.png");
    }
}
