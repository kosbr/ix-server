package ru.kos.ix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kos.ix.ServiceHolder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created by Константин on 08.04.2016.
 */
public class ServerManager {

    private static final Logger logger = LogManager.getLogger(ServerManager.class);

    private ServerSocket serverSocket;

    public static void main(String[] args) throws Exception {
        ServiceHolder.init();
        ServerManager serverManager = new ServerManager();
        serverManager.startServer();
    }

    public ServerManager()  {

    }

    public void startServer() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(3129, 0, InetAddress.getByName("localhost"));
        logger.info("server is started");
        AcceptThread acceptThread = new AcceptThread(serverSocket);
        acceptThread.start();

        boolean workflag = true;
        while (workflag) {
            Thread.sleep(500);
        }
        logger.info("server is stopped");
        serverSocket.close();
    }
}
