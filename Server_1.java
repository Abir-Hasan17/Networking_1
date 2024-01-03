import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;


public class Server_1 {
    public static void main(String[] args) throws IOException{

        int start_port = 700;
        int port = start_port;
        int max_port = 3;

        while(port-start_port<max_port){
            Thread conn = new Thread(new serv_conn(port));
            conn.start();
            port++;
        }
        System.out.println("\nWaiting for client....");

    }
}

class serv_conn implements Runnable{
    int port;
    serv_conn(int p){
        port = p;
    }
    @Override
    public void run(){
        try{
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Port: "+port+" ready to connect...");
            Socket s = ss.accept();
            String clnt;

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            clnt = dis.readUTF();
            System.out.println(clnt+" Connected to port: "+port+"!!!!\n");

            Thread in = new Thread(new serv_in(dos,dis,clnt));
            Thread out = new Thread(new serv_out(dos,dis));

            in.start();
            out.start();
        }catch(IOException e){System.out.println(e);}
    }
    
}

class serv_in implements Runnable{
    DataOutputStream dos;
    DataInputStream dis;
    String clnt;
    serv_in(DataOutputStream o, DataInputStream i, String c){
        dos = o;
        dis = i;
        clnt = c;
    }
    @Override
    public void run() {
        while(true){
            try {
                String s = dis.readUTF();
                if(s.equals("exit")) System.exit(0);
                System.out.println("#"+clnt+": "+s);
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
