package rmi;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;

/**
 * A TCP server for skeleton
 * This implements a multithreaded server with thread pool approach.
 */
public class TCPServer<T> implements Runnable {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Skeleton<T> skeleton;

    private boolean stopped = true;

    static final POOL_SIZE = 50;
    static final DEFAULT_BACKLOG = 100;

    public TCPServer(Skeleton<T> skeleton) throws IOException {
        this.serverSocket = new ServerSocket();
        this.threadPool = Executors.newFixedThreadPool(this.poolSize);
        this.skeleton = skeleton;
    }

    public TCPServer(InetSocketAddress ipSocketAddress, Skeleton<T> skeleton) throws IOException {
        this.serverSocket = new ServerSocket(ipSocketAddress.getPort(), DEFAULT_BACKLOG, ipSocketAddress.getAddress());
        this.threadPool = Executors.newFixedThreadPool(this.poolSize);
        this.skeleton = skeleton;
    }

    @Override
    public void run() {
        this.stopped = false;
        while (!this.isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();

                if (!this.isStopped()) {
                    this.threadPool.execute(new Thread(new TCPWorker<T>(clientSocket, this.skeleton)));
                }
            } catch (IOException e) {
                if (this.isStopped()) {
                    System.out.println("Server has stopped.");
                    return;
                } else {
                    this.stop();
                }
                // TODO:
                // Call service_error on skeleton or simply throw an exception.
                this.skeleton.service_error(new RMIException(e));
                throw new RuntimeException("Error accepting client connection", e);
                e.printStackTrace();
            }
        }

        this.threadPool.shutdownNow();
    }

    public void stop() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            System.out.println("Server failed to stop.");
            e.printStackTrace();
        }
        this.stopped = true;
    }

    private boolean isStopped() {
        return this.stopped;
    }
}
