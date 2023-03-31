import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Cette classe représente un Manager dans SNMP représenté ici dans cette partie comme un manager
public class Client_Manager2 {
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        // Appel au registre RMI à partir de l'URL "rmi://localhost:1099/MIB" + Récupération objet annuaire + Cast car retourne un objet de type Remote
        MIB mib = (MIB) Naming.lookup("rmi://localhost:1099/MIB");
        // Appel de la méthode getMIB avec comme paramètre l'OID 1.3.6.1.4.1.1 sur l'objet distant avec comme communauté : "commTest"
        Traps trap = new TrapsImpl();
        mib.setPriority(trap,2);
        System.out.println(mib.getMIB("1.3.6.1.4.1.1", "commTest"));
        // Appel de la méthode getNextMIB avec comme paramètre l'OID 1.3.6.1.4.1.1 sur l'objet distant avec comme communauté : "commTest"
        System.out.println(mib.getNextMIB("1.3.6.1.4.1.1", "commTest"));
        // Appel de la méthode setMIB avec comme paramètre l'OID 1.3.6.1.4.1.1 et la valeur "192.168.1.90" sur l'objet distant avec comme communauté : "commTest"
        mib.setMIB("1.3.6.1.4.1.2", "192.168.1.84", "commTest");
        // Appel de la méthode getMIB avec comme paramètre l'OID 1.3.6.1.4.1.2 sur l'objet distant avec comme communauté : "commTest"
        System.out.println(mib.getMIB("1.3.6.1.4.1.2", "commTest"));
        // Appel de la méthode getMIB avec comme paramètre l'OID 1.3.6.1.4.1.4 sur l'objet distant avec comme communauté : "commTest"
        System.out.println(mib.getMIB("1.3.6.1.4.1.4", "commTest"));
        // Appel de la méthode getNextMIB avec comme paramètre l'OID 1.3.6.1.4.1.3 sur l'objet distant avec comme communauté : "commTest"
        System.out.println(mib.getNextMIB("1.3.6.1.4.1.3", "commTest"));
        // Appel de la méthode getNextMIB avec comme paramètre l'OID 1.3.6.1.4.1.3 sur l'objet distant avec comme communauté : "commTest"
        try {
            // Abonnement à différents traps présents dans la MIB
            mib.Abotrap("1.3.6.1.4.1.8", trap, "commTest");
            mib.Abotrap("1.3.6.1.4.1.9", trap, "commTest");
            mib.Abotrap("1.3.6.1.4.1.10", trap, "commTest");
            mib.Abotrap("1.3.6.1.4.1.11", trap, "commTest");
            // Déclenchement de ces traps lors d'une mise à jour dans la MIB
            mib.setMIB("1.3.6.1.4.1.2", "192.168.1.84", "commTest");
            // Supprime l'objet distant trap

        } catch (Exception e) {
            // Supprime l'objet distant trap

        }
    }
}
