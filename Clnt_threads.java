import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

class clnt_in implements Runnable{
    DataOutputStream dos;
    DataInputStream dis;
    clnt_in(DataOutputStream dos, DataInputStream dis){
        this.dos = dos;
        this.dis = dis;
    }
    @Override
    public void run() {
        while(true){
            try {
                String sender = dis.readUTF();
                String msg = dis.readUTF();
                if(msg.equals("exit")) System.exit(0);
                System.out.println("#"+sender+": "+msg);
            } catch (IOException e) {
                System.out.println("error in clnt_in");
            }
        }
    }

}

class clnt_out implements Runnable{
    Scanner inp = new Scanner(System.in);
    DataOutputStream dos;
    DataInputStream dis;
    clnt_out(DataOutputStream dos, DataInputStream dis){
        this.dos = dos;
        this.dis = dis;
    }
    @Override
    public void run() {
        while(true){
            try {
                String msg = inp.nextLine();
                dos.writeUTF(msg);
                if(msg.equals("exit")) System.exit(0);
            } catch (IOException e) {
                System.out.println("error in clnt_out");
            }
        }
    }

}
