import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

public class Client_1 {
    public static void main(String[] args) throws IOException {
        Scanner inp = new Scanner(System.in);
        String clnt = "client_1";

        Socket s = new Socket("192.168.0.105",700);
        System.out.println("\nConnected to server!!!!\n");

        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        dos.writeUTF(clnt);
        
        Thread in = new Thread(new clnt_in(dos,dis));
        Thread out = new Thread(new clnt_out(dos,dis));
        
        out.start();
        in.start();

    }
}
