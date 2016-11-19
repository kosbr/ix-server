package ru.kos.ix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * Main class of the application. It configures server and starts it. <br/>
 * The lifecycle of server: <br/>
 * 1)Read configuration from server.properties <Br/>
 * 2)Create service instances according to service.* lines of properties  <br/>
 * 3)Prepare {@link ClientSocketHolder} <br/>
 * 4)Run server <br/>
 * 5)When active param in status.file (server.properties) becomes not true, server starts prepare shutdown <br/>
 * 6)Close all open client sockets <Br/>
 * 7)Stop server <Br/>
 * Created by Константин on 08.04.2016.
 */
public class ServerManager {

    private static final Logger LOGGER = LogManager.getLogger(ServerManager.class);
    private static final String STATUS_FILE_KEY = "status.file";
    private static final String ACTIVE_PARAM = "active";
    private static final int STATUS_MONITORING_INTERVAL_MS = 500;

    private Properties properties;

    /**
     * Main method of the application. First param is server listening port.
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        InputStream inputStream  = ServiceHolder.class.getClassLoader().getResourceAsStream("server.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        LOGGER.info("Properties files is loaded. Properties.size=" + properties.size());

        ServiceHolder.init(properties);
        ClientSocketHolder.init();
        ServerManager serverManager = new ServerManager(properties);
        serverManager.startServer(port);
    }

    public ServerManager(final Properties properties)  {
        this.properties = properties;
    }

    /**
     * Starts server. Creates and starts {@link AcceptThread}. Starts monitoring of event to shutdown.
     * @param port
     * @throws IOException
     */
    public void startServer(final int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
        LOGGER.info("server is started port " + port);
        AcceptThread acceptThread = new AcceptThread(serverSocket);
        acceptThread.start();

        String statusFile = properties.getProperty(STATUS_FILE_KEY);

        try {
            while (isWorking(statusFile)) {
                Thread.sleep(STATUS_MONITORING_INTERVAL_MS);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Can't read server.properties " + e.getMessage());
        }

        LOGGER.info("server is stopped");
        ClientSocketHolder.getInstance().closeAll();
        serverSocket.close();
    }

    /**
     * Check if server must start prepare to shutdown
     * @param statusFile
     * @return
     * @throws IOException
     */
    private boolean isWorking(final String statusFile) throws IOException {
        InputStream inputStream = new FileInputStream(statusFile);
        Properties properties = new Properties();
        properties.load(new BufferedInputStream(inputStream));
        return Boolean.parseBoolean(((String) properties.get(ACTIVE_PARAM)));
    }


}
