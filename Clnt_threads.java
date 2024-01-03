import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

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
