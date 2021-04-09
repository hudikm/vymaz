package sk.uniza;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.logging.Logger;

public class TcpServer extends AbstractServer implements Runnable {

    private ServerSocket serverSocket;
    private final IServerCallBack iServerCallBack;
    private final Logger logger = Logger.getLogger("TCPServer");

    protected TcpServer(UserSocketCreator concreteUserSocketCreator, Set<IUserSocket> users, IServerCallBack iServerCallBack) {
        super(concreteUserSocketCreator, users);
        this.iServerCallBack = iServerCallBack;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();

                concreteUserSocketCreator.createUser(socket, iServerCallBack)
                        .ifPresent(userSocket -> {
                            iServerCallBack.onConnect(userSocket);
                            concreteUserSocketCreator.registerUser(userSocket, userSockets);
                            userSocket.startListening();

                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Thread startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            logger.info(String.format("Server started: %s", serverSocket.getLocalSocketAddress().toString()));
            Thread thread = new Thread(this);
            thread.start();
            return thread;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
