package rmi;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * A TCP worker thread for TCP server
 */
class TCPWorker<T> implements Runnable {
    protected Socket clientSocket;
    protected ServerSocket serverSocket;

    protected T remoteObject;

    public TCPWorker(Socket clientSocket, ServerSocket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
        this.remoteObject = serverSocket.remoteObject;
    }

    public void run() {

    }
}