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
 * Created by Константин on 08.04.2016.
 */
public class ServerManager {

    private static final Logger logger = LogManager.getLogger(ServerManager.class);
    private static final String STATUS_FILE_KEY = "status.file";
    private static final String ACTIVE_PARAM = "active";

    private Properties properties;

    public static void main(String[] args) throws Exception {
        InputStream inputStream  = ServiceHolder.class.getClassLoader().getResourceAsStream("server.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        logger.info("Properties files is loaded. Properties.size=" + properties.size());

        ServiceHolder.init(properties);
        ServerManager serverManager = new ServerManager(properties);
        serverManager.startServer();
    }

    public ServerManager(Properties properties)  {
        this.properties = properties;
    }

    public void startServer() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(3129, 0, InetAddress.getByName("localhost"));
        logger.info("server is started");
        AcceptThread acceptThread = new AcceptThread(serverSocket);
        acceptThread.start();

        String statusFile = properties.getProperty(STATUS_FILE_KEY);

        try {
            while (isWorking(statusFile)) {
                Thread.sleep(500);
            }
        } catch (IOException e) {
            logger.error("Can't read server.properties");
        }

        logger.info("server is stopped");
        serverSocket.close();
    }

    private boolean isWorking(String statusFile) throws IOException {
        InputStream inputStream = new FileInputStream(statusFile);
        Properties properties = new Properties();
        properties.load(new BufferedInputStream(inputStream));
        return new Boolean((String)properties.get(ACTIVE_PARAM));
    }


}
