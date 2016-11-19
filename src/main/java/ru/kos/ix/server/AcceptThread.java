package ru.kos.ix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread for accepting clients. After client is accepted it creates ans starts new instance
 * of {@link ClientThread} <br/>
 * Created by Константин on 08.04.2016.
 */
public class AcceptThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(AcceptThread.class);

    private final ServerSocket serverSocket;

    public AcceptThread(final ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        int currentClientId = 0;
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            LOGGER.debug("Start accepting");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Socket was accepted " + clientSocket);
                ClientThread clientThread = new ClientThread(currentClientId, clientSocket, executorService);
                clientThread.start();
                currentClientId++;
            }
        } catch (IOException e) {
            LOGGER.warn("Possible server is stopped:" + e.getMessage());
        }
    }
}
