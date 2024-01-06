import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;


public class Server_1 {
    public static void main(String[] args) throws IOException{

        int start_port = 700;
        int port = start_port;
        int max_port = 3;
        ArrayList<DataOutputStream> dos = new ArrayList<DataOutputStream>();

        System.out.println("\nWaiting for client....\n");
        while(port-start_port<max_port){
            Thread conn = new Thread(new serv_conn(port, dos));
            conn.start();
            port++;
        }

        Thread out = new Thread(new serv_out(dos));
        out.start();

    }
}

class serv_conn implements Runnable{
    int port;
    ArrayList<DataOutputStream> dos;
    serv_conn(int p, ArrayList<DataOutputStream> o){
        port = p;
        dos = o;
    }
    @Override
    public void run(){
        try{
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Port: "+port+" ready to connect...");
            Socket s = ss.accept();
            String clnt;

            DataInputStream dis = new DataInputStream(s.getInputStream());
            dos.add(new DataOutputStream(s.getOutputStream()));
            clnt = dis.readUTF();
            System.out.println("\n"+clnt+" Connected to port: "+port+"!!!!");

            Thread in = new Thread(new serv_in(dis,clnt));
            in.start();
        }catch(IOException e){System.out.println(e);}
    }
    
}

class serv_in implements Runnable{
    DataInputStream dis;
    String clnt;
    serv_in(DataInputStream i, String c){
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
    ArrayList<DataOutputStream> dos;
    serv_out(ArrayList<DataOutputStream> o){
        dos = o;
    }
    @Override
    public void run() {
        while(true){
            try {
                String s = inp.nextLine();
                for(int i = 0; i<dos.size(); i++){
                    dos.get(i).writeUTF(s);
                }
                if(s.equals("exit")) System.exit(0);
            } catch (IOException e) {e.printStackTrace();}
        }
    }

}
