import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerBase {

    private ServerSocket serverSocket = null;
    private Integer threadLimit = null;

    public ServerBase(int port, int threadLimit) {
        this.threadLimit= threadLimit;
        try {
            serverSocket= new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread sclThread = new Thread(new ServerConnectLoop());
        sclThread.start();
    }

    public class ServerConnectLoop implements Runnable
    {
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadLimit);

        public void run()
        {
            try
            {	while (true)
            {	Socket clientSocket = serverSocket.accept();
                QueryHandler q = new QueryHandler(clientSocket);
                pool.execute(q);
                System.out.println("[SERVER] Client connected. " + pool.getActiveCount() + " client(s) connected.");
            }
            }
            catch (IOException e)
            {	e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        ServerBase s= new ServerBase(6000,5);
        System.out.println("Server is running. Terminate the process for shut down.");
    }
}
