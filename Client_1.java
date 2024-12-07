import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

public class Client_1 {
    public static void main(String[] args) throws IOException {
        Scanner inp = new Scanner(System.in);
        // int i = 99;
        // Socket s;
        // while(true){
        //     try{
        //         i++;
        //         s = new Socket("192.168.0."+i,777);
        //         System.out.println("\nConnected to server!!!!\n");
        //         break;
        //     }catch(Exception e){System.out.println("192.168.0."+i+" unsuccesful");}
        // }

        Socket s = new Socket("localhost",8080);
        System.out.println("\nConnected to server!!!!\n");

        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        Thread in = new Thread(new clnt_in(dos,dis));
        Thread out = new Thread(new clnt_out(dos,dis));
        
        out.start();
        in.start();

    }
}

class clnt_in implements Runnable{
    DataOutputStream dos;
    DataInputStream dis;
    clnt_in(DataOutputStream o, DataInputStream i){
        dos = o;
        dis = i;
    }
    @Override
    public void run() {
        while(true){
            try {
                String s = dis.readUTF();
                if(s.equals("exit")) System.exit(0);
                System.out.println("#Server: "+s);
            } catch (IOException e) {e.printStackTrace();}
        }
    }

}

class clnt_out implements Runnable{
    Scanner inp = new Scanner(System.in);
    DataOutputStream dos;
    DataInputStream dis;
    clnt_out(DataOutputStream o, DataInputStream i){
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
