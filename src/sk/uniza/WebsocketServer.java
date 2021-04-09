package sk.uniza;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.logging.Logger;

public class WebsocketServer extends AbstractServer {
    private InnerWebSocketServer proxyWebSocketServer;
    private final Logger logger = Logger.getLogger("WebSockerServer");
    private final IServerCallBack iServerCallBack;

    protected WebsocketServer(UserSocketCreator concreteUserSocketCreator, Set<IUserSocket> userSockets, IServerCallBack serverCallBack) {
        super(concreteUserSocketCreator, userSockets);
        this.iServerCallBack = serverCallBack;
    }

    private class InnerWebSocketServer extends WebSocketServer {

        public InnerWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            logger.info(String.format("New user: %s", conn.getLocalSocketAddress()));

            concreteUserSocketCreator.createUser(conn, iServerCallBack)
                    .ifPresent(userSocket -> {
                        concreteUserSocketCreator.registerUser(userSocket, userSockets);
                        iServerCallBack.onConnect(userSocket);

                    });
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {

            concreteUserSocketCreator.createUser(conn, iServerCallBack)
                    .ifPresent(iServerCallBack::onDisconnect);


        }

        @Override
        public void onMessage(WebSocket conn, String message) {

            userSockets.stream()
                    .filter(WebSocketUser.class::isInstance)
                    .map(WebSocketUser.class::cast)
                    .filter(webSocketUser -> webSocketUser.getSocket().equals(conn))
                    .findFirst()
                    .ifPresent(userSocket -> {
                        iServerCallBack.onReceive(userSocket, message);
                    });

//            concreteUserSocketCreator.createUser(conn, iServerCallBack)
//                    .ifPresent(userSocket -> {
//                        iServerCallBack.onReceive(userSocket, message);
//                    });
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            logger.severe(ex.toString());
        }

        @Override
        public void onStart() {

        }
    }


    @Override
    Thread startServer(int port) {
        proxyWebSocketServer = new InnerWebSocketServer(new InetSocketAddress(port));

        Thread thread = new Thread(proxyWebSocketServer::run);
        thread.start();
        return thread;
    }
}
