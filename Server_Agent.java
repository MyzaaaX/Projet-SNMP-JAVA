import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

// Cette classe représente un agent dans SNMP représenté ici dans cette partie comme un serveur
public class Server_Agent {
    // Url malformé ou RemoteException (Obligatoire en RMI)
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        // Création d'un RMI registry sur le port 1099
        LocateRegistry.createRegistry(1099);
        // Création d'un objet MIB avec comme équipement "TestMIB" et comme adresse "192.168.0.1"
        MIBImpl obj = new MIBImpl();

        try {
            // Création d'une communauté "commTest" avec les permissions d'écriture et lecture
            obj.addCommunity("commTest", "11");
            obj.addCommunity("commTest2", "01");
            obj.addCommunity("commTest3", "00");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // Enregistre l'objet et écrase une éventuelle association nom-objet déjà présente dans l'annuaire
        Naming.rebind("rmi://localhost:1099/MIB", obj);
        obj.GenereDisque();
    }
}
