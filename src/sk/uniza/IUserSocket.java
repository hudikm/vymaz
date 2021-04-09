package sk.uniza;

public interface IUserSocket {
    void sendData(String data);
    void startListening();
}
