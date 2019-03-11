package client;

import java.io.IOException;

class WallChanger {


    public void setMac(String path) throws IOException {
        String as[] = {
                "osascript",
                "-e", "tell application \"Finder\"",
                "-e", "set desktop picture to POSIX file \"" + path + "\"",
                "-e", "end tell"
        };
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(as);
    }


    /**
     *
     * @param uiAction - ActionID is an user specified parameter for Windows Settings
     * @param uiParam -
     * @param pvParam - Additional info for parametr
     * @param fWinIni
     * @return
     */
    public static native int SystemParametersInfo(int uiAction, int uiParam, String pvParam, int fWinIni);

    static {
        System.loadLibrary("user32");
    }

    /**
     * For Wallpaper changer which is uiAction == 20 and uiParam is obligatory == 0
     * @param path - path to the wallpaper file
     * @return
     */
    public int Change(String path) throws IOException {

        if (OsDefiner.isWindows())
            return SystemParametersInfo(20, 0, path, 0);
        else if (OsDefiner.isUnix())
            setMac(path);
        return 0;
    }









}
