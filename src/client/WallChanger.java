package client;


import com.sun.jna.Library;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.Native;
import java.io.IOException;

public class WallChanger {

    public void setMac(String path) throws IOException {
        String as[] = {
                "osascript",
                "-e", "tell application \"Finder\"",
                "-e", "set desktop picture to POSIX file \"" + path + "\"",
                "-e", "end tell"
        };
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(as);
    }

    public static interface User32 extends Library {
        User32 INSTANCE = (User32) Native.loadLibrary("user32",User32.class, W32APIOptions.DEFAULT_OPTIONS);
        boolean SystemParametersInfo (int one, int two, String s ,int three);
    }


    /**
     * For Wallpaper changer which is uiAction == 20 and uiParam is obligatory == 0
     * @param path - path to the wallpaper file
     * @return
     */
    public void changeWallpaper(String path) throws IOException {

        if (OsDefiner.isWindows())
             User32.INSTANCE.SystemParametersInfo(0x0014, 0, path, 1);
        else if (OsDefiner.isMac() || OsDefiner.isUnix())
            setMac(path);

    }
}
