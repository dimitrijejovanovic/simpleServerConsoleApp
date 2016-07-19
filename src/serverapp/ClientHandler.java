package serverapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    BufferedReader reader;
    Socket clientSocket;
    PrintWriter writer;
    Server server;


    public ClientHandler(Server s) {
        try {
            server = s;
            clientSocket = s.clientSocket;
            InputStreamReader isReader = new InputStreamReader(clientSocket.getInputStream());
            reader = new BufferedReader(isReader);
            writer = new PrintWriter(clientSocket.getOutputStream());
        } catch (Exception ex) {
            System.out.println("Problem pri povezivanju sa klijentom");
        }
    } // kraj konstruktora

    public void run() {
        //boolean za zavrsetak rada niti ukoliko dodje do klijentovog izbora za prekid konekcije
        boolean nit = true;
        try {
            //uvodno obavestenje za klijenta
            writer.println("Konekcija sa serverom je uspostavljena."
                    + "\r\nDostupne komande su:"
                    + "\r\n'put naziv tekst' za unos novog sadrzaja"
                    + "\r\n'get naziv' za pregled teksta na osnovu naslova"
                    + "\r\n'getall' za pregled celokupnog sadrzaja"
                    + "\t\n'exit' za prekid aplikacije");
            writer.flush();

            while (nit) {
                writer.println("Izaberite komandu.");
                writer.flush();
                String unos = reader.readLine();
                String[] niz = server.parsiraj(unos);
                
                switch (niz[0]) {
                    case "put":
                        writer.println(server.put(niz));
                        writer.flush();
                        break;
                    case "get":
                        writer.println(server.get(niz));
                        writer.flush();
                        break;
                    case "getall":
                        writer.println(server.getAll(server.hMap));
                        writer.flush();
                        break;
                    case "exit":
                        writer.close();
                        reader.close();
                        clientSocket.close();
                        nit = false;
                        break;
                    default:
                        writer.println("Uneli ste nepostojecu komandu.");
                        writer.flush();
                }
            } // kraj while petlje
            //poruka serveru da je klijentska nit zavrsila sa radom
            System.out.println("Korisnik " + Thread.currentThread().getName() + " se odjavio");
        } catch (Exception ex) {
            //poruka u slucaju da je klijentska aplikacija ugasena pre odjave 
            System.out.println("Veza sa klijentom na niti: " + Thread.currentThread().getName() + " je prekinuta");
        }
    } // kraj run metode
} // kraj klase
