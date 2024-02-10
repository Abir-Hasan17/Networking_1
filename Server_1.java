import java.util.*;
import java.io.*;
import java.net.*;


public class Server_1 {
    static ArrayList<Client> clients = new ArrayList<Client>();
    static ArrayList<Integer> avail_port = new ArrayList<Integer>();
    public static void main(String[] args) throws IOException{

        int start_port = 700;
        int port = start_port;
        int max_port = 4;

        while(port-start_port<max_port){
            Thread conn = new Thread(new serv_conn(port, clients));
            conn.start();
            port++;
        }
        Thread ping = new Thread(new serv_ping());
        ping.start();
        System.out.println("\nWaiting for client....\n");

        Thread out = new Thread(new serv_out(clients));
        out.start();

    }
}

