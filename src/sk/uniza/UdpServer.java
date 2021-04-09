package sk.uniza;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.logging.Logger;

public class UdpServer extends AbstractServer implements Runnable {
    private DatagramSocket socket;
    private final IServerCallBack iServerCallBack;
    private final Logger logger = Logger.getLogger("UdpServer");

    protected UdpServer(UserSocketCreator concreteUserSocketCreator, Set<IUserSocket> userSockets, IServerCallBack iServerCallBack) {
        super(concreteUserSocketCreator, userSockets);
        this.iServerCallBack = iServerCallBack;
    }


    @Override
    public Thread startServer(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(this);
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[150], 150);
        try {
            while (true) {

                socket.receive(datagramPacket);
                userSockets.stream()
                        .filter(userSocket -> userSocket instanceof UdpSocketUser)
                        .map(UdpSocketUser.class::cast)
                        /*.filter(userSocket -> userSocket.hashCode() == datagramPacket.getSocketAddress().hashCode())*/
                        .filter(userSocket -> userSocket.getSocketAddress().equals(datagramPacket.getSocketAddress()))
                        .findAny()
                        .ifPresentOrElse(userSocket -> {
                                    String inputString = new String(datagramPacket.getData(),0,datagramPacket.getLength(), StandardCharsets.UTF_8);

                                    if (inputString.equalsIgnoreCase("exit")) {
                                        iServerCallBack.onDisconnect(userSocket);
                                    } else {
                                        iServerCallBack.onReceive(userSocket, inputString);
                                    }

                                }
                                , () -> {
                                    concreteUserSocketCreator.createUser(socket, datagramPacket.getSocketAddress())
                                            .ifPresent(userSocket -> {
                                                iServerCallBack.onConnect(userSocket);
                                                concreteUserSocketCreator.registerUser(userSocket, userSockets);
                                            });
                                }
                        );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
