import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;


public class Server_1 {
    public static void main(String[] args) throws IOException{

        System.out.println("\nWaiting for client....");
        ServerSocket ss = new ServerSocket(8080);
        Socket s = ss.accept();
        System.out.println("Connected to client!!!!\n");

        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        Thread in = new Thread(new serv_in(dos,dis));
        Thread out = new Thread(new serv_out(dos,dis));

        in.start();
        out.start();

    }
}

class serv_in implements Runnable{
    DataOutputStream dos;
    DataInputStream dis;
    serv_in(DataOutputStream o, DataInputStream i){
        dos = o;
        dis = i;
    }
    @Override
    public void run() {
        while(true){
            try {
                String s = dis.readUTF();
                if(s.equals("exit")) System.exit(0);
                System.out.println("#Client: "+s);
            } catch (IOException e) {e.printStackTrace();}
        }
    }
}

class serv_out implements Runnable{
    Scanner inp = new Scanner(System.in);
    DataOutputStream dos;
    DataInputStream dis;
    serv_out(DataOutputStream o, DataInputStream i){
        dos = o;
        dis = i;
    }
    @Override
    public void run() {
        while(true){
            try {
                String s = inp.nextLine();
                dos.writeUTF(s);
                if(s.equals("exit")) System.exit(0);
            } catch (IOException e) {e.printStackTrace();}
        }
    }

}
