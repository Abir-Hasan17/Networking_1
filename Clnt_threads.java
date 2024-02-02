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
        boolean pg = false;
        while(true){
            try {
                String sender = dis.readUTF();
                String msg = dis.readUTF();
                if(msg.equals("exit")) System.exit(0);
                if(pg){
                    System.out.println(msg);
                }else{
                    System.out.println("#"+sender+": "+msg);
                }
                if(msg.equals("<paragraph>")){
                    pg = !pg;
                }
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
