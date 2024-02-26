import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

public class Client_1 {
    static int port;
    static String ip;
    static ArrayList<Client> clients = new ArrayList<Client>();
    public static void main(String[] args) throws IOException {

        Thread ping = new Thread(new clnt_ping(port,ip,clients));
        ping.start();

    }
}
