package ru.kos.ix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton. Keeps sockets of every client. <br/>
 * It is necessary for server shut down to close all sockets. <br/>
 * Created by kosbr on 09.04.2016.
 */
public final class ClientSocketHolder {

    private static final Logger LOGGER = LogManager.getLogger(ClientSocketHolder.class);

    private static ClientSocketHolder instance;

    private Map<Integer, Socket> socketMap;

    /**
     * Initialize holder. <br/>
     * It is called from main method once, so it doesn't have to be synchronized
     */
    public static void init() {
        instance = new ClientSocketHolder();
    }

    /**
     * Returns instance. It must be called {@link #init()} method before.
     * @return
     */
    public static ClientSocketHolder getInstance() {
        return instance;
    }

    private ClientSocketHolder() {
        socketMap = new ConcurrentHashMap<>();
    }

    /**
     * Puts socket of client to storage. <br/>
     * Due to using {@link ConcurrentHashMap} it is thread-safe method.
     * @param clientId
     * @param socket
     * @return
     */
    public Socket put(final Integer clientId, final Socket socket) {
        return socketMap.put(clientId, socket);
    }

    /**
     * Removes socket of client from storage. <br/>
     * Corresponding to goal of class it can be called after socket closing. <br/>
     * Due to using {@link ConcurrentHashMap} it is thread-safe method.
     * @param clientId
     * @return
     */
    public Socket remove(final Integer clientId) {
        return socketMap.remove(clientId);
    }

    /**
     * Closes all sockets in storage
     * @throws IOException
     */
    public void closeAll() throws IOException {
        socketMap.forEach(this::closeSocket);
    }

    private void closeSocket(final Integer clientId, final Socket socket) {
        try {
            LOGGER.info("Close socket for client:" + clientId);
            socket.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
