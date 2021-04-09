package sk.uniza;

public interface IServerCallBack {
    void onReceive(IUserSocket userSocket, String data);
    void onDisconnect(IUserSocket userSocket);
    void onConnect(IUserSocket userSocket);

}
