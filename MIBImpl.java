import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

// Doit hériter de UnicastRemoteObject + implémentation de l'interface
public class MIBImpl extends UnicastRemoteObject implements MIB {

    // Stocke les communautés avec les droits associés
    HashMap<String, String> community_hashmap = new HashMap<>();
    // Propage l'exception RemoteException et appelle le constructeur de la super classe
    HashMap<Traps, List<String>> trap2 = new HashMap<>();
    String message = "null";

    /**
     * Constructeur de l'objet MIB
     *
     * @throws RemoteException
     */
    public MIBImpl() throws RemoteException {
        // Appel du constructeur de la super classe
        super();
    }

    public void GenereDisque(){
        Random random = new Random();
        Thread thread = new Thread(() -> {
            while (true) {
                int rdm = random.nextInt(90);
                System.out.println(rdm);
                String occup_disque = String.valueOf(Integer.valueOf(rdm));
                try (FileReader fileReader = new FileReader("Mib.json")) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(fileReader);
                    JSONArray MibArray = (JSONArray) obj;
                    for (Object objet : MibArray) {
                        JSONObject element = (JSONObject) objet;
                        String oldoid = (String) element.get("OID");
                        if (oldoid.equals("1.3.6.1.4.1.6")) {
                            element.put("valeur", occup_disque);
                            break;
                        }
                    }
                    try (FileWriter obj2 = new FileWriter("Mib.json")) {
                        obj2.write(JSONValue.toJSONString(MibArray));
                        obj2.flush();
                        obj2.close();
                    }
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    /**
     * @param community Le nom de la communauté
     * @return VRAI, si la communauté existe, FAUX si la communauté n'existe pas
     */
    public Boolean VerifAuth(String community) {
        return community_hashmap.containsKey(community);
    }

    /**
     * @param community "Le nom de la communauté"
     * @param perms     "Le droit associé à cette communauté"
     *                  Valeurs possibles :
     *                  "00" : Aucun droit associé , ni de lecture ni d'écriture
     *                  "01" : Un droit de lecture seulement est associé
     *                  "11" : Un droit de lecture et d'écriture est associé
     */
    public void addCommunity(String community, String perms) {
        if (!perms.equals("00") && !perms.equals("01") & !perms.equals("11")) {
            throw new RuntimeException("La permission " + perms + " pour la communauté " + community + " n'existe pas");
        }
        community_hashmap.put(community, perms);
    }

    /**
     * @param OID       L'OID voulu
     * @param community La communauté
     * @return Le nom et la valeur selon l'OID précisé
     */
    @Override
    public String getMIB(String OID, String community) throws RemoteException {
        JSONParser parser = new JSONParser();
        String nom = null;
        String valeur = null;
        String OIDRead;
        // Si la communauté n'existe pas , on throw une exception
        if (!VerifAuth(community)) {
            throw new RuntimeException("La communauté " + community + " n'existe pas");
        }
        // Si la communauté n'a pas les droits nécessaires , on throw une exception
        if (community_hashmap.get(community).equals("00")) {
            throw new RuntimeException("La communauté " + community + " n'a pas les droits nécessaires");
        }
        try {
            // Lecture des valeurs et des noms des fichiers jusqu'à tomber sur l'OID voulu
            Object obj = parser.parse(new FileReader("Mib.json"));
            JSONArray MibArray = (JSONArray) obj;
            for (Object equipements : MibArray) {
                JSONObject equipement = (JSONObject) equipements;
                OIDRead = (String) equipement.get("OID");
                if (OID.equals(OIDRead)) {
                    nom = (String) equipement.get("nom");
                    valeur = (String) equipement.get("valeur");
                }
            }
            if (nom == null && valeur == null) {
                throw new RuntimeException("L'OID spécifié " + OID + " n'existe pas ! ");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ("GET : " + nom + " - " + valeur);
    }

    /**
     * @param OID       : L'OID voulu
     * @param value     : La valeur qu'on veut mettre dans le fichier selon l'OID défini précédemment
     * @param community : La communauté
     * @throws RemoteException
     */
    @Override
    public void setMIB(String OID, String value, String community) throws RemoteException {
        if (!VerifAuth(community)) {
            throw new RuntimeException("La communauté " + community + " n'existe pas");
        }
        if (community_hashmap.get(community).equals("01") || community_hashmap.get(community).equals("00")) {
            throw new RuntimeException("La communauté " + community + " n'a pas les droits nécessaires (Ecriture nécessaire)");
        }
        try (FileReader fileReader = new FileReader("Mib.json")) {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(fileReader);
            JSONArray MibArray = (JSONArray) obj;
            for (Object objet : MibArray) {
                JSONObject element = (JSONObject) objet;
                String oldoid = (String) element.get("OID");
                if (oldoid.equals(OID)) {
                    element.put("valeur", value);
                    break;
                }
            }

            try (FileWriter obj2 = new FileWriter("Mib.json")) {
                obj2.write(JSONValue.toJSONString(MibArray));
                obj2.flush();
                obj2.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        sendTraps();
    }

    /**
     * @param OID       : L'OID voulu
     * @param community : La communauté
     * @return Retourne l'OID suivant par rapport à l'OID précisé
     * @throws RemoteException
     */
    public String getNextMIB(String OID, String community) throws RemoteException {
        JSONParser parser = new JSONParser();
        String OID1;
        String OID2 = null;

        if (!VerifAuth(community)) {
            throw new RuntimeException("La communauté " + community + " n'existe pas");
        }
        if (community_hashmap.get(community).equals("00")) {
            throw new RuntimeException("La communauté " + community + " n'a pas les droits nécessaires");
        }
        try {
            Object obj = parser.parse(new FileReader("Mib.json"));
            JSONArray MibArray = (JSONArray) obj;
            for (int i = 0; i < MibArray.size() - 1; i++) {
                JSONObject equipement1 = (JSONObject) MibArray.get(i);
                JSONObject equipement2 = (JSONObject) MibArray.get(i + 1);
                OID1 = (String) equipement1.get("OID");
                if (OID.equals(OID1)) {
                    OID2 = (String) equipement2.get("OID");
                }
            }
            if (OID2 == null) {
                throw new RuntimeException("L'OID spécifié ou son suivant n'existe pas ! ");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ("GET-NEXT : OID SUIVANT " + OID2);
    }

    @Override

    /**
     * Abonne un manager à un ap présent dans la MIB
     * @param OID OID du Trap en question
     * @param addedtrap Objet Trap côté client
     * @param community Le nom de la communauté
     * @throws RemoteException
     */
    public void Abotrap(String OID, Traps addedtrap, String community) throws RemoteException {
        if (!VerifAuth(community)) {
            throw new RuntimeException("La communauté " + community + " n'existe pas");
        }
        if (community_hashmap.get(community).equals("00")) {
            throw new RuntimeException("La communauté " + community + " n'a pas les droits nécessaires");
        }
        List<String> listeTrap = trap2.get(addedtrap);
        if (listeTrap == null) {
            listeTrap = new ArrayList<>();
        }
        String temp = this.getMIB(OID, community);
        String nomTrap = temp.substring(6, temp.indexOf(" -"));
        if (!listeTrap.contains(nomTrap)) {
            listeTrap.add(nomTrap);
            this.message = listeTrap.toString();
        }
        if (addedtrap != null) {
            trap2.put(addedtrap, new ArrayList<>(listeTrap));
        }

    }

    /**
     * Permet l'affichage côté client en appelant la méthode afficheTrap(String message)
     *
     * @throws RemoteException
     */
    public void sendTraps() throws RemoteException {
        for (Traps element : trap2.keySet()) {
            if (element != null && element instanceof Traps) {
                try {
                    element.afficheTrap(message);
                } catch (RemoteException e) {
                    element = null;
                }
            }
        }
    }
    public void setPriority(Traps trap,int number) throws RemoteException
    {
        trap.setPriority(number);
        trap.afficheTrap("Je suis un manager qui vient d'être lancé avec la priorité : " + trap.getPriority());
            AtomicInteger value = new AtomicInteger();
            Thread thread = new Thread(() -> {
                while (true) {
                    String chaine;
                    try {
                        chaine = this.getMIB("1.3.6.1.4.1.6","commTest");
                        String value_string = chaine.substring(chaine.indexOf("- ")+2, chaine.length());
                        if(value_string != "") {
                            value.set(Integer.parseInt(value_string));
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    if (value.get() > 50) {
                        try {
                            if(trap.getPriority() == 2) {
                                try {
                                    trap.afficheTrap("Seuil disque supérieur à 50% !");
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(value.get() > 80){
                        try {
                            if(trap.getPriority() == 1) {
                                try {
                                    trap.afficheTrap("Seuil disque supérieur à 80 % !");
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        Thread.sleep(1000); // Attend une seconde avant de vérifier à nouveau
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    public int getPriority(Traps trap,int number) throws RemoteException
    {
        return trap.getPriority();
    }
}