package ru.kos.ix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Константин on 08.04.2016.
 */
public class AcceptThread extends Thread {

    private static final Logger logger = LogManager.getLogger(AcceptThread.class);

    private ServerSocket serverSocket;

    public AcceptThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        int currentClientId = 0;
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            logger.debug("Start accepting");
            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                logger.info("Socket was accepted " + clientSocket);
                ClientThread clientThread = new ClientThread(currentClientId, clientSocket, executorService);
                clientThread.start();
                currentClientId++;
            }
        } catch (IOException e) {
            logger.warn("Possible server is stopped:" + e.getMessage());
        }
    }
}
