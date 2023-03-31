import java.rmi.RemoteException;

public interface MIB extends java.rmi.Remote {
    /**
     * @param OID       L'OID voulu
     * @param community La communauté
     * @return Le nom et la valeur selon l'OID précisé
     * @throws RemoteException
     */
    String getMIB(String OID, String community) throws RemoteException;

    /**
     * @param OID       : L'OID voulu
     * @param value     : La valeur qu'on veut mettre dans le fichier selon l'OID défini précédemment
     * @param community : La communauté
     * @throws RemoteException
     */
    void setMIB(String OID, String value, String community) throws RemoteException;

    /**
     * @param OID       : L'OID voulu
     * @param community : La communauté
     * @return Retourne l'OID suivant par rapport à l'OID précisé
     * @throws RemoteException
     */
    String getNextMIB(String OID, String community) throws RemoteException;

    /**
     * Abonne un manager à un trap présent dans la MIB
     *
     * @param OID OID du Trap
     * @param addedtrap Objet Trap côté client
     * @param community Le nom de la communauté
     * @throws RemoteException
     */
    void Abotrap(String OID, Traps addedtrap, String community) throws RemoteException;
    public void setPriority(Traps trap,int number) throws RemoteException;
    public int getPriority(Traps trap,int number) throws RemoteException;
}