package ru.kos.ix.server;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kos.ix.dto.AnsTask;
import ru.kos.ix.dto.Status;
import ru.kos.ix.dto.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Thread that belongs to one client. It waits requests and delegates tasks to other threads.
 * Created by kosbr on 08.04.2016.
 */
public class ClientThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(ClientThread.class);

    private final int clientId;

    private final Socket socket;

    private final ExecutorService executorService;

    /**
     * Creates client thread
     * @param clientId ClientId
     * @param socket Client socket
     * @param executorService Executor service for task threads
     */
    public ClientThread(final int clientId, final Socket socket, final ExecutorService executorService) {
        this.clientId = clientId;
        this.socket = socket;
        this.executorService = executorService;
        ClientSocketHolder.getInstance().put(clientId, socket);
    }

    @Override
    public void run() {
        LOGGER.debug("Start thread for client" + clientId);
        try {
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                Task task = (Task) is.readObject();
                LOGGER.info("Get " + task + " from client" + clientId);
                TaskCallable taskCallable = new TaskCallable(task);
                ListenableFutureTask<AnsTask> future = ListenableFutureTask.create(taskCallable);
                Futures.addCallback(future, new FutureCallback<AnsTask>() {
                    @Override
                    public void onSuccess(final AnsTask result) {
                        synchronized (os) {
                            try {
                                LOGGER.info("Answer " + result + " to client " + clientId);
                                os.writeObject(result);
                                os.flush();
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(final Throwable t) {
                        Throwable cause = t;
                        if (t instanceof InvocationTargetException) {
                            cause = t.getCause();
                        }
                        synchronized (os) {
                            LOGGER.error("Service error: " + cause.getMessage(), cause);
                            try {
                                AnsTask ansTask = new AnsTask(task.getId(), null, Status.ERROR, cause.getMessage());
                                LOGGER.info("Answer " + ansTask + " to client " + clientId);
                                os.writeObject(ansTask);
                                os.flush();
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }
                });
                executorService.execute(future);
                LOGGER.debug("Waiting for new tasks from client" + clientId);
            }
        } catch (Exception e) {
            LOGGER.warn("Possible client has closed connection or server is stopping: " + e.getMessage());
        } finally {
            try {
                LOGGER.debug("Close socket for client " + clientId);
                socket.close();
                ClientSocketHolder.getInstance().remove(clientId);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
