import java.util.*;
import java.io.*;
import java.net.*;

class serv_conn implements Runnable{
    int port;
    ArrayList<Client> clients;
    serv_conn(int port, ArrayList<Client> clients){
        this.port = port;
        this.clients = clients;
    }
    @Override
    public void run(){
        try{
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Port: "+port+" ready to connect...");
            Client.av_port.add(port);
            Socket s = ss.accept();
            Client.av_port.remove(Integer.valueOf(port));

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            String clnt = dis.readUTF();
            Client client = new Client (clnt,dis,dos,port,ss,s);
            clients.add(client);
            System.out.println("\n"+clnt+" Connected to port: "+port+"!!!!");

            Thread in = new Thread(new serv_in(client,clients));
            in.start();
        }catch(IOException e){System.out.println("error in serve_conn");}
    }

}

class serv_in implements Runnable{
    Client client;
    ArrayList<Client> clients;
    serv_in(Client client, ArrayList<Client> clients){
        this.client = client;
        this.clients = clients;
    }
    @Override
    public void run() {
        boolean pg = false;
        while(true){
            try {
                String s = client.dis.readUTF();
                if(s.equals("exit")){
                    System.out.println(client.name+" disconnected!!!");
                    client.disconnect();
                    new Thread(new serv_conn(client.port, clients)).start();
                    clients.remove(client);
                    break;
                }
                if(pg) System.out.println(s);
                else System.out.println("#"+client.name+": "+s);
                String[] arr = s.split(" ",2);
                String clnt = arr[0];
                if(clnt.contains(">>")){
                    for(int i = 0; i<clients.size(); i++){
                        if(arr[1].equals("//p")) break; //fix later...
                        if(clnt.equals(clients.get(i).name+">>")){
                            clients.get(i).dos.writeUTF(client.name);
                            if(arr[1].equals("exit")) clients.get(i).dos.writeUTF(" Wants you to disconnect!!!");
                            else clients.get(i).dos.writeUTF(arr[1]);
                        }
                    }
                }
                if(s.equals("//p")) pg = !pg;
            }
            catch (SocketException e) {break;}
            catch (IOException e) {
                System.out.println("error in serve_in");
                break;
            }
        }
    }
}

class serv_out implements Runnable{
    Scanner inp = new Scanner(System.in);
    ArrayList<Client> clients;
    serv_out(ArrayList<Client> clients){
        this.clients = clients;
    }
    @Override
    public void run() {
        while(true){
            try {
                String s = inp.nextLine();
                String[] arr = s.split(" ",2);
                String clnt = arr[0];
                if(clnt.contains(">>")){
                    for(int i = 0; i<clients.size(); i++){
                        if(clnt.equals(clients.get(i).name+">>")){
                            clients.get(i).dos.writeUTF("Server");
                            clients.get(i).dos.writeUTF(arr[1]);
                            if(arr[1].equals("exit")){
                                clients.get(i).disconnect();
                                new Thread(new serv_conn(clients.get(i).port, clients)).start();
                                clients.remove(i);
                            }
                            if(arr[1].equals("block")){
                                clients.remove(i);
                            }
                        }
                    }
                }else{
                    //System.out.println(dos.size());
                    for(int i = 0; i<clients.size(); i++){
                        clients.get(i).dos.writeUTF("Server");
                        clients.get(i).dos.writeUTF(s);
                    }
                    if(s.equals("exit")) System.exit(0);
                }
            } catch (IOException e) {System.out.println("error in serve_out");}
        }
    }

}

class serv_ping implements Runnable {
    @Override
    public void run() {
    try {
        int p1, p2, p3, p4;
        p1 = 192;
        p2 = 168;
        p3 = 0;
        p4 = 0;
        DatagramSocket ds = new DatagramSocket();
        while(true){
        for (p4 = 0; p4 < 256; p4++) {
            //for (p3 = 0; p3 < 256; p3++) {
                if (Client.av_port.size() == 0) {
                    Thread.sleep(10);
                    continue;
                }
                InetAddress ip = InetAddress.getByName(p1 + "." + p2 + "." + p3 + "." + p4);
                byte buf[] = null;
                String ping = "192.168.0.103"+"@"+Client.av_port.get(0);
                buf = ping.getBytes();
                DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 6969);
                ds.send(DpSend);
            //System.err.println(0);
            //}
        }
        Thread.sleep(2);
        }
    } catch (Exception e) {System.out.println("error in serv_ping");}
    }
}