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
            send_current_client_list(dos);
            String clnt = dis.readUTF();
            Client client = new Client (clnt,dis,dos,port,ss,s);
            clients.add(client);
            System.out.println("\n"+clnt+" Connected to port: "+port+"!!!!");

            Thread in = new Thread(new serv_in(client,clients));
            in.start();
            send_instructions(dos,clnt);
            notify_other_clients(clnt);
        }catch(Exception e){System.out.println("error in serve_conn "+e.getMessage());}
    }
    public void notify_other_clients(String clnt) throws IOException {
        for(int i = 0; i<clients.size(); i++){
            if(clients.get(i).name.equals(clnt)) continue;
            clients.get(i).dos.writeUTF("Server");
            clients.get(i).dos.writeUTF(clnt+" connected to server!!!");
        }
    }
    public void send_instructions(DataOutputStream dos, String clnt) throws IOException {
        String Instructions =   "Hello "+clnt+"\n" +
                "These are some information you may need;\n" +
                "1. All your messages will be sent to the server.\n" +
                "2. To send massage to another client use following format:\n" +
                "\t\t client_name>> massage\n" +
                "3. To broadcast a massage to all the clients use the following format:\n" +
                "\t\t all>> massage\n" +
                "4. To send massage as paragraph to the server use the following format:\n" +
                "\t\t //p\n" +
                "\t\t massage\n" +
                "\t\t //p\n" +
                "\n" +
                "NOTE: It is recommended to write the paragraph according to the format elsewhere and then coppying and pasting in the console.\n" +
                "NOTE: Do not forget to put a new line after the closing \"//p\".\n" +
                "5. You can not send paragraphs to other clients\n" +
                "6. To terminate your connection from server send \"exit\" to the server.\n" +
                "\n\t\t\t ##ENJOY##";
        dos.writeUTF("Server");
        dos.writeUTF(Instructions);
    }
    public void send_current_client_list(DataOutputStream dos) throws IOException {
        String s = "";
        for(int i = 0; i<clients.size(); i++){
            s = s+" "+clients.get(i).name;
        }
        dos.writeUTF("Server");
        dos.writeUTF(s);
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
                        if(clnt.equals("all>>")) {
                            if(clnt.equals(clients.get(i).name+">>")) continue;
                            clients.get(i).dos.writeUTF(client.name);
                            if(arr[1].equals("exit")) clients.get(i).dos.writeUTF(" ##Wants you to disconnect!!!");
                            else clients.get(i).dos.writeUTF(arr[1]);
                        }
                        else if(clnt.equals(clients.get(i).name+">>")){
                            clients.get(i).dos.writeUTF(client.name);
                            if(arr[1].equals("exit")) clients.get(i).dos.writeUTF(" ##Wants you to disconnect!!!");
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
            } catch (Exception e) {System.out.println("error in serve_out "+e.getMessage());}
        }
    }

}

class serv_ping implements Runnable{
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
                String ping = "192.168.0.106"+"@"+Client.av_port.get(0);
                buf = ping.getBytes();
                DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 6969);
                ds.send(DpSend);
            //System.err.println(0);
            //}
        }
        Thread.sleep(2);
        }
    } catch (Exception e) {System.out.println("error in serv_ping "+e.getMessage());}
    }
}