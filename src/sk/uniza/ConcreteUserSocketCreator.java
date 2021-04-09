package sk.uniza;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Optional;

public class ConcreteUserSocketCreator extends UserSocketCreator {


    @Override
    Optional<IUserSocket> createUser(Socket socket, IServerCallBack iServerCallBack) {
        try {
            return Optional.of(new TcpUserSocket(socket, iServerCallBack));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    Optional<IUserSocket> createUser(DatagramSocket socket, SocketAddress socketAddress) {
        try {
            UdpSocketUser udpSocketUser = new UdpSocketUser(socketAddress, socket);
            return Optional.of(udpSocketUser);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    Optional<IUserSocket> createUser(WebSocket socket, IServerCallBack iServerCallBack) {
        return Optional.of(new WebSocketUser(socket, iServerCallBack));
    }
}
