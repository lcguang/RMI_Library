package rmi;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * A TCP worker thread for TCP server
 */
class TCPWorker implements Runnable {
    protected Socket clientSocket;
    protected ServerSocket serverSocket;

    public TCPWorker(Socket clientSocket, ServerSocket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    public void run() {

    }
}