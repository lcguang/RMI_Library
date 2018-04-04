package rmi;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * A TCP server for skeleton
 * source:
 * http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html
 * https://blog.eduonix.com/java-programming-2/learn-create-multi-threaded-server-java/
 */
class TCPServer implements Runnable {
    protected ServerSocket serverSocket;
    protected stopped = true;

    public TCPServer() {
        this.serverSocket = new ServerSocket();
    }

    public void run() {
        this.stopped = false;
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server has stopped.");
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
                e.printStackTrace();
            }
            Thread workerThread = new Thread(new TCPWorker(clientSocket, this));
            workerThread.start();
        }
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