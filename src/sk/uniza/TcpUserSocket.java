package sk.uniza;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpUserSocket implements IUserSocket {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final IServerCallBack iServerCallBack;

    public TcpUserSocket(Socket socket, IServerCallBack iServerCallBack) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.iServerCallBack = iServerCallBack;
    }

    @Override
    public void sendData(String data) {
        out.println(data);
    }

    @Override
    public void startListening() {
        new Thread(() -> {
            try {
                String inputString;
                while ((inputString = in.readLine()) != null) {
                    iServerCallBack.onReceive(this, inputString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                iServerCallBack.onDisconnect(this);
            }
        }).start();
    }

    @Override
    public String toString() {
        return "TcpUserSocket{" +
                "Address=" + socket.getLocalSocketAddress().toString() +
                '}';
    }
}
