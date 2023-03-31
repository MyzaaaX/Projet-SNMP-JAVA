import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TrapsImpl extends UnicastRemoteObject implements Traps {
    /**
     * Constructeur de la classe TrapsImpl
     *
     * @throws RemoteException
     */
    int priority;
    public TrapsImpl() throws RemoteException {
        super();
    }

    /**
     * Affiche les messages de trap côté client
     *
     * @param message
     * @throws RemoteException
     */
    @Override
    public void afficheTrap(String message) throws RemoteException {
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        message = message.replace("[", "");
        message = message.replace("]", "");
        List<String> Liste_Traps = new ArrayList<String>(Arrays.asList(message.split(",")));
        for (String element : Liste_Traps) {
            System.out.println(s.format(date) + " Trap déclenché : " + element);
        }
    }
    public void setPriority(int number) throws RemoteException
    {
        priority = number;
    }
    public int getPriority() throws RemoteException{
        return priority;
    }
}
