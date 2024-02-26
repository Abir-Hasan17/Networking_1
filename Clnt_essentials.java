import java.util.*;
import java.io.*;
import java.net.*;

class clnt_in implements Runnable{
    DataOutputStream dos;
    DataInputStream dis;
    ArrayList<Client> clients;
    clnt_in(DataOutputStream dos, DataInputStream dis, ArrayList<Client> clients){
        this.dos = dos;
        this.dis = dis;
        this.clients = clients;
    }
    @Override
    public void run() {
        boolean pg = false;
        while(true){
            try {
                String sender = dis.readUTF();
                String msg = dis.readUTF();
                String key = utility.find_key(sender,clients);
                msg = utility.decrypt(msg, key);
                if(msg.equals("exit")&&sender.equals("Server")) System.exit(0);
                if(pg){
                    System.out.println(msg);
                }else{
                    System.out.println("#"+sender+": "+msg);
                }
                if(msg.equals("//p")){
                    pg = !pg;
                }
            } catch (Exception e) {
                System.out.println("error in clnt_in " +e.getMessage());
            }
        }
    }
}

class clnt_out implements Runnable{
    Scanner inp = new Scanner(System.in);
    DataOutputStream dos;
    DataInputStream dis;
    ArrayList<Client> clients;

    clnt_out(DataOutputStream dos, DataInputStream dis, ArrayList<Client> clients){
        this.dos = dos;
        this.dis = dis;
        this.clients = clients;
    }
    @Override
    public void run() {
        while(true){
            try {
                String s = inp.nextLine();
                String arr[] = s.split(" ",0);
                String msg;
                if(arr[0].equals("cmd")){
                    if(arr[1].equals("set_key")) utility.set_key(arr[2],arr[3],clients);
                }else{
                    String brr[] = s.split(">> ", 2);
                    String key = utility.find_key(brr[0],clients);
                    if(s.contains(">> ")){
                        msg = utility.encrypt(brr[1],key);
                        dos.writeUTF(brr[0]+">> "+msg);
                    }else {
                        msg = utility.encrypt(s,key);
                        dos.writeUTF(msg);
                    }
                    if(s.equals("exit")) System.exit(0);
                }
            } catch (Exception e) {
                System.out.println("error in clnt_out " +e.getMessage());
            }
        }
    }
}

class clnt_ping implements Runnable{
    int port;
    String ip;
    ArrayList<Client> clients;
    clnt_ping(int port, String ip, ArrayList<Client> clients){
        this.port = port;
        this.ip = ip;
        this.clients = clients;
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
            ds.close();
            Thread init = new Thread(new clnt_init(port,ip,clients));
            init.start();
        }catch(Exception e){System.out.println("error in clnt_ping ");}
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
    static ArrayList<Client> clients;
    clnt_init(int port, String ip, ArrayList<Client> clients){
        this.port = port;
        this.ip = ip;
        this.clients = clients;
    }
    @Override
    public void run() {
        try{
            Scanner inp = new Scanner(System.in);
            Socket s = new Socket(ip,port);
            System.out.println("\nConnected to server!!!!\n");

            System.out.print("User_Name: ");
            String clnt = inp.nextLine();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF(clnt);

            Thread in = new Thread(new clnt_in(dos,dis,clients));
            Thread out = new Thread(new clnt_out(dos,dis,clients));

            out.start();
            in.start();
        }catch(Exception e){System.out.println("error in clnt_init "+e.getMessage());}
    }
}