
package serverapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    //ovaj projekat je pravljen da se koristi od strane telnet klijenta
    //da bi se aplikacija koristila potrebno je aktivirati telnet klijent: 
    //> Control Panel.
    //> Programs and Features.
    //> Turn Windows features on or off.
    //U Windows Features dialog boxu, stiklirati Telnet Client.
    //Nakon aktivacije telnet klijenta i pokretanja ove aplikacije potrebno je u CMD ukucati: telnet localhost 4455 
    //Komande su put [string] [string]; get [string]; getall

    ServerSocket serverSocket;
    Socket clientSocket;
    HashMap<String, String> hMap = new HashMap<String, String>();

    public static void main(String[] args) {
        Server s = new Server();
        s.pokretanje();
    }

    void pokretanje() {

        try {
            serverSocket = new ServerSocket(4455);
            System.out.println("Server je pokrenut.");
            while (true) {
                clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                Thread t = new Thread(new ClientHandler(this));
                t.start();
                System.out.println("uspostavljena je konekcija sa korisnikom cija je nit: " + t.getName());
            }

        } catch (BindException bex) {
            System.out.println("Server je vec pokrenut.\nAplikacija je prestala sa radom");
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("Problem sa pokretanjem servera.");
            ex.printStackTrace();
        }
    }//kraj pokretanja

    //metoda koja od unetog stringa pravi niz stringova 
    //koji se mogu uneti u kolekciju
    String[] parsiraj(String unos) {
        //ogranicava pravljenje niza od maksimalno 3 clana
        //sto omogucava razmake u samom sadrzaju teksta
        String[] niz = unos.split(" ", 3);
        return niz;
    }//kraj metode parsiraj

    //metoda za unosenje naziva teksta i teksta u kolekciju
    //i obavestavanje korisnika o uspesnosti unosa
    String put(String[] niz) {
        //uslov onemogucava izostavljane naziva teksta ili samog teksta 
        if (niz.length < 3) {
            return "Nepravilan unos komande put.\r\nprimer pravilnog unosa: 'put naslov tekst'";
        } else {
            synchronized (this) {
                hMap.put(niz[1], niz[2]);
            }
            return "Uspesan unos.";
        }//kraj else bloka
    }//kraj put metode

    //metoda za prikazivanje teksta na usnovu unetog naslova
    String get(String[] niz) {
        if (niz.length != 2) {
            return "Nepravilan unos komande get\r\nprimer pravilnog unosa: 'get naslov'";
        } else {
            synchronized (this) {
                if (hMap.get(niz[1]) == null) {
                    return "Uneli ste nepostojeci naslov.";
                } else {
                    return "tekst: " + hMap.get(niz[1]);
                }
            }//kraj sinhronizacije
        }//kraj else bloka
    }//kraj get metode

    //metoda za ispis svih clanova kolekcije
    //kod je prilagodjena metoda toString() od HashMapa 
    synchronized String getAll(HashMap hMap) {
        Iterator<HashMap.Entry<String, String>> i = hMap.entrySet().iterator();
        if (!i.hasNext()) {
            return "Nema unetih tekstova";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("naslov: ");
        for (;;) {
            Map.Entry<String, String> e = i.next();
            String key = e.getKey();
            String value = e.getValue();
            sb.append(key);
            sb.append("\r\ntekst: ");
            sb.append(value + ";");
            if (!i.hasNext()) {
                return sb.toString();
            }
            sb.append("\r\nnaslov: ");
        }//kraj for petlje
    }//kraj getAlla

}//kraj klase
