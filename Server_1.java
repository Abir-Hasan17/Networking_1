import java.util.*;
import java.io.*;
import java.net.*;


public class Server_1 {
    static ArrayList<Client> clients = new ArrayList<Client>();
    public static void main(String[] args) throws IOException{

        int start_port = 700;
        int port = start_port;
        int max_port = 4;

        while(port-start_port<max_port){
            Thread conn = new Thread(new serv_conn(port, clients));
            conn.start();
            port++;
        }
        System.out.println("\nWaiting for client....\n");

        Thread out = new Thread(new serv_out(clients));
        out.start();

    }
}

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
            Socket s = ss.accept();

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
                System.out.println("#"+client.name+": "+s);

                String[] arr = s.split(" ",2);
                String clnt = arr[0];
                if(clnt.contains(">>")){
                    for(int i = 0; i<clients.size(); i++){
                        if(clnt.equals(clients.get(i).name+">>")){
                            clients.get(i).dos.writeUTF(clients.get(i).name);
                            clients.get(i).dos.writeUTF(arr[1]);
                        }
                    }
                }

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
