import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MIBCreator {
    /**
     * Creer le fichier JSON s'il n'existe pas.
     */
    public static void CreateJson() {
        File Mib = new File("Mib.json");
        if (Mib.exists()) {
            return;
        }
        try {
            Mib.createNewFile();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        String jsonString = "[{\"valeur\":\"Agent1\",\"OID\":\"1.3.6.1.4.1.1\",\"nom\":\"nomEquip\"},{\"valeur\":\"192.168.1.84\",\"OID\":\"1.3.6.1.4.1.2\",\"nom\":\"adresseIP\"},{\"valeur\":\"Linux\",\"OID\":\"1.3.6.1.4.1.3\",\"nom\":\"versionOS\"},{\"valeur\":\"16\",\"OID\":\"1.3.6.1.4.1.4\",\"nom\":\"totalRam\"},{\"valeur\":\"8\",\"OID\":\"1.3.6.1.4.1.5\",\"nom\":\"NbCoeurProc\"},{\"valeur\":\"55\",\"OID\":\"1.3.6.1.4.1.6\",\"nom\":\"Seuil Espace Disque\"},{\"valeur\":\"active\",\"OID\":\"1.3.6.1.4.1.7\",\"nom\":\"IntG0\\/0\\/0\"},{\"description\":\"TrapTest1\",\"OID\":\"1.3.6.1.4.1.8\",\"priority\":\"1\",\"nom\":\"TrapEtatInterface1\"},{\"description\":\"Un autre trap ... \",\"OID\":\"1.3.6.1.4.1.9\",\"priority\":\"2\",\"nom\":\"TrapTest2\"},{\"description\":\"Un autre trap ... \",\"OID\":\"1.3.6.1.4.1.10\",\"priority\":\"3\",\"nom\":\"TrapTest3\"},{\"description\":\"Un autre trap ... \",\"OID\":\"1.3.6.1.4.1.11\",\"priority\":\"4\",\"nom\":\"TrapTest4\"}]";
        try (
                FileWriter obj = new FileWriter("Mib.json")) {
            obj.write(jsonString);
            obj.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MIBCreator.CreateJson();
    }
}
