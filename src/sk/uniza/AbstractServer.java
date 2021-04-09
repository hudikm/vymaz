package sk.uniza;

import java.util.Set;

public abstract class AbstractServer {

    final protected UserSocketCreator concreteUserSocketCreator;
    final protected Set<IUserSocket> userSockets;

    protected AbstractServer(UserSocketCreator concreteUserSocketCreator, Set<IUserSocket> userSockets) {
        this.concreteUserSocketCreator = concreteUserSocketCreator;
        this.userSockets = userSockets;
    }

    abstract Thread startServer(int port);
}
