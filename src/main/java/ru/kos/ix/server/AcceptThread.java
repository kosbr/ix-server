package ru.kos.ix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
        try {
            logger.info("Start accepting");
            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                logger.info("Socket was accepted " + clientSocket);
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }
}
