package ru.kos.ix.server;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kos.ix.dto.AnsTask;
import ru.kos.ix.dto.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Created by Константин on 08.04.2016.
 */
public class ClientThread extends Thread {

    private static final Logger logger = LogManager.getLogger(ClientThread.class);

    private Socket socket;

    private ExecutorService executorService;

    public ClientThread(Socket socket, ExecutorService executorService) {
        this.socket = socket;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        logger.info("Start thread for " + socket);
        try {
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                Task task = (Task)is.readObject();
                logger.info("Get " + task + " from " + socket);
                TaskCallable taskCallable = new TaskCallable(task);
                ListenableFutureTask<AnsTask> future = ListenableFutureTask.create(taskCallable);
                Futures.addCallback(future, new FutureCallback<AnsTask>() {
                    @Override
                    public void onSuccess(AnsTask result) {
                        synchronized (os) {
                            try {
                                logger.info("Answer " + result);
                                os.writeObject(result);
                                os.flush();
                            } catch (IOException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        synchronized (os) {
                            logger.error("Service error: " + t.getMessage(), t);
                            try {
                                AnsTask ansTask = new AnsTask(task.getId(), null, "Error: " + t.getMessage());
                                logger.info("Answer " + ansTask);
                                os.writeObject(ansTask);
                                os.flush();
                            } catch (IOException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                });
                executorService.execute(future);
                logger.info("Waiting for new tasks");
            }
        } catch (Exception e) {
            logger.info("Possible client has closed connection: " + e.getMessage());
        } finally {
            try {
                logger.info("Close socket " + socket);
                socket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
