package sk.uniza;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UdpSocketUser implements  IUserSocket{
    private final DatagramSocket socket;
    private final DatagramPacket datagramPacket;
    private SocketAddress socketAddress;

    public UdpSocketUser(SocketAddress socketAddress, DatagramSocket socket) throws SocketException {
        this.socket = socket;
        this.socketAddress = socketAddress;
        datagramPacket = new DatagramPacket(new byte[150],150,socketAddress);
    }

    @Override
    public void sendData(String data) {
        datagramPacket.setData(data.getBytes(StandardCharsets.UTF_8));
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startListening() {

    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UdpSocketUser that = (UdpSocketUser) o;

        if (!datagramPacket.equals(that.datagramPacket)) return false;
        return socketAddress.equals(that.socketAddress);
    }

    @Override
    public int hashCode() {
        return socketAddress.hashCode();
    }

    @Override
    public String toString() {
        return "UdpSocketUser{" +
                "SocketAddress=" + socketAddress +
                '}';
    }
}
