package rmi;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.IOException;

/**
 * The TCPWorker handling clients requests.
 */
public class TCPWorker<T> implements Runnable {
    private Socket clientSocket;
    private Skeleton<T> skeleton;
    private T remoteObject;

    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;

    public TCPWorker(Socket clientSocket, Skeleton<T> skeleton) {
        this.clientSocket = clientSocket;
        this.skeleton = skeleton;
        // TODO:
        // Implements getRemoteObject() on Skeleton<T>.
        this.remoteObject = skeleton.getRemoteObject();
    }

    @Override
    public void run() {
        // TODO:
        // Implements RemoteCall class.
        RemoteCall remoteCall = null;

        try {
            this.outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.outputStream.flush();
            this.inputStream = new ObjectInputStream(this.clientSocket.getInputStream());
            remoteCall = (RemoteCall) this.inputStream.readObject();
        } catch (IOException e) {
            this.skeleton.service_error(new RMIException(e));
        } catch (ClassNotFoundException e) {
            this.skeleton.service_error(new RMIException(e));
        }

        if (remoteCall != null) {
            // TODO:
            // Implements RemoteReturn class.
            RemoteReturn remoteReturn = handleRemoteCall(remoteCall);
            try {
                this.outputStream.writeObject(remoteReturn);
            } catch (IOException e) {
                this.skeleton.service_error(new RMIException(e));
            }
        }

        try {
            this.clientSocket.close();
        } catch (IOException e) {
            this.skeleton.service_error(new RMIException(e));
        }
    }

    private RemoteReturn handleRemoteCall(RemoteCall remoteCall) {
        // TODO:
        // Find the correct method name.
        String methodName = remoteCall.getMethodName();
        String className = remoteCall.getClassName();
        String interfaceName = this.skeleton.getRemoteInterface().getName();
        Object[] args = remoteCall.getArgs();
        Class<?>[] argsClass = remoteCall.getArgsClass();
        Class<?> remoteObjectClass = this.remoteObject.getClass();

        Method targetMethod = null;

        Object ReturnValue = null;

        try {
            targetMethod = remoteObjectClass.getMethod(methodName, argsClass);
        } catch (NoSuchMethodException | SecurityException e) {
            this.skeleton.service_error(new RMIException(e));
            return new RemoteReturn(null, e);
        }

        if (targetMethod != null) {
            if (!className.equals(interfaceName)) {
                String exceptionMessage = "calling method not declared in the interface for which the skeleton was created";
                this.skeleton.service_error(new RMIException(new NoSuchMethodException(exceptionMessage)));
                return new RemoteReturn(null, new RMIException(exceptionMessage));
            }
            try {
                targetMethod.setAccessible(true);
                returnValue = targetMethod.invoke(this.remoteObject, args);
            } catch (InvocationTargetException e) {
                return new RemoteReturn(null, (Exception) e.getTargetException());
            } catch (IllegalAccessException | IllegalArgumentException e) {
                this.skeleton.service_error(new RMIException(e));
                return new RemoteReturn(null, new RMIException(e));
            }
        }

        return new RemoteReturn(returnValue, null);
    }
}
