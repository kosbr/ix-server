package ru.kos.ix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Константин on 09.04.2016.
 */
public final class ClientSocketHolder {

    private static final Logger logger = LogManager.getLogger(ClientSocketHolder.class);

    private static ClientSocketHolder instance;

    private Map<Integer, Socket> socketMap;

    public static void init() {
        instance = new ClientSocketHolder();
    }

    public static ClientSocketHolder getInstance() {
        return instance;
    }

    public ClientSocketHolder() {
        socketMap = new ConcurrentHashMap<>();
    }

    public Socket put(Integer clientId, Socket socket) {
        return socketMap.put(clientId, socket);
    }

    public Socket remove(Integer clientId) {
        return socketMap.remove(clientId);
    }

    public void closeAll() throws IOException {
        socketMap.forEach(this::closeSocket);
    }

    private void closeSocket(Integer clientId, Socket socket) {
        try {
            logger.info("Close socket for client:" + clientId );
            socket.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
