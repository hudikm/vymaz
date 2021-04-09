package sk.uniza;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Server implements IServerCallBack {
    private final Logger logger = Logger.getLogger("Server");

    private Set<IUserSocket> userSockets = new HashSet<>();
    ConcreteUserSocketCreator concreteUserSocketCreator = new ConcreteUserSocketCreator();

    void startServer() {
        Thread udpServer = new UdpServer(concreteUserSocketCreator, userSockets, this).startServer(9000);
        Thread tcpServer = new TcpServer(concreteUserSocketCreator, userSockets, this).startServer(9001);
        Thread webSocketServer = new WebsocketServer(concreteUserSocketCreator, userSockets, this).startServer(8887);
        try {
            udpServer.join();
            tcpServer.join();
            webSocketServer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onReceive(IUserSocket userSocket, String data) {
        logger.info("New data received: \"%s\" from %s".formatted(data, userSocket.toString()));
        userSockets.stream().filter(userSocket1 -> !userSocket1.equals(userSocket)).forEach(userSocket1 -> userSocket1.sendData(data));
    }

    @Override
    public void onDisconnect(IUserSocket userSocket) {
        logger.info(String.format("User: %s", userSocket.toString()));
        concreteUserSocketCreator.unregisterUser(userSocket, userSockets);
    }

    @Override
    public void onConnect(IUserSocket userSocket) {
        logger.info(String.format("New User connected: %s", userSocket.toString()));
    }

}
