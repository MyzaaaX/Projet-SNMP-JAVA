import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Traps extends Remote {
    /**
     * Affiche les messages de trap côté client
     *
     * @param message
     * @throws RemoteException
     */
    void afficheTrap(String message) throws RemoteException;
    void setPriority(int number) throws RemoteException;
    int getPriority() throws RemoteException;
}
