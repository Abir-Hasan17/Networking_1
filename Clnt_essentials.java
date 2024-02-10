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
                if(msg.equals("//p")){
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

class clnt_ping implements Runnable{
    int port;
    String ip;
    clnt_ping(int port, String ip){
        this.port = port;
        this.ip = ip;
    }
    @Override
    public void run() {
        try{
            DatagramSocket ds = new DatagramSocket(6969);
            byte[] receive = new byte[65535];

            DatagramPacket DpReceive = null;
            DpReceive = new DatagramPacket(receive, receive.length);
            System.out.println("Waiting for connection.....");
            ds.receive(DpReceive);
            String s = data(receive).toString();
            String[] arr = s.split("@",2);
            port = Integer.parseInt(arr[1]);
            ip = arr[0];
            receive = new byte[65535];

            Thread init = new Thread(new clnt_init(port,ip));
            init.start();
        }catch(Exception e){System.out.println("error in clnt_ping");}
    }
    static StringBuilder data(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}

class clnt_init implements Runnable{
    int port;
    String ip;
    clnt_init(int port, String ip){
        this.port = port;
        this.ip = ip;
    }
    @Override
    public void run() {
        try{
            Scanner inp = new Scanner(System.in);
            System.out.print("User_Name: ");
            String clnt = inp.nextLine();
            Socket s = new Socket(ip,port);
            System.out.println("\nConnected to server!!!!\n");

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF(clnt);

            Thread in = new Thread(new clnt_in(dos,dis));
            Thread out = new Thread(new clnt_out(dos,dis));

            out.start();
            in.start();
        }catch(Exception e){System.out.println("error in clnt_init");}
    }
}