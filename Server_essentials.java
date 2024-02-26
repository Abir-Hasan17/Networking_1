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
            utility.send_current_client_list(dos, clients);
            String clnt = dis.readUTF();
            Client client = new Client (clnt,dis,dos,port,ss,s);
            clients.add(client);
            System.out.println("\n"+clnt+" Connected to port: "+port+"!!!!");

            Thread in = new Thread(new serv_in(client,clients));
            in.start();
            utility.send_instructions(dos,clnt);
            utility.notify_other_clients(clnt, clients);
        }catch(Exception e){System.out.println("error in serve_conn "+e.getMessage());}
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
                String key = "encription_is_key";
                String msg = utility.decrypt(s,key);
                if(msg.equals("exit")){
                    System.out.println(client.name+" disconnected!!!");
                    client.disconnect();
                    new Thread(new serv_conn(client.port, clients)).start();
                    clients.remove(client);
                    break;
                }
                String[] arr = s.split(">> ",2);
                if(pg) System.out.println(msg);
                else if(!s.contains(">> ")) System.out.println("#"+client.name+": "+msg);
                else System.out.println("#"+client.name+": "+arr[0]+">> "+utility.decrypt(arr[1],key));
                String clnt = arr[0];
                if(s.contains(">> ")){
                    for(int i = 0; i<clients.size(); i++){
                        if(arr[1].equals("//p")) break; //fix later...
                        if(clnt.equals("all")) {
                            if(client.name.equals(clients.get(i).name)) continue;
                            clients.get(i).dos.writeUTF(client.name);
                            if(arr[1].equals("exit")) continue;
                            else clients.get(i).dos.writeUTF(arr[1]);
                        }
                        else if(clnt.equals(clients.get(i).name)){
                            clients.get(i).dos.writeUTF(client.name);
                            if(arr[1].equals("exit")) continue;
                            else clients.get(i).dos.writeUTF(arr[1]);
                        }
                    }
                }
                if(msg.equals("//p")) pg = !pg;
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
                String key = "encription_is_key";
                String msg = utility.encrypt(s, key);
                if(s.contains(">> ")){
                    String[] arr = s.split(">> ",2);
                    String clnt = arr[0];
                    msg = utility.encrypt(arr[1], key);
                    for(int i = 0; i<clients.size(); i++){
                        if(clnt.equals(clients.get(i).name)){
                            clients.get(i).dos.writeUTF("Server");
                            clients.get(i).dos.writeUTF(msg);
                            if(arr[1].equals("exit")){
                                clients.get(i).disconnect();
                                clients.remove(i);
                                new Thread(new serv_conn(clients.get(i).port, clients)).start();
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
                        clients.get(i).dos.writeUTF(utility.encrypt(s, key));
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
            //for (p3 = 0; p3 < 256; p3++) {
            //for (p2 = 0; p3 < 256; p3++) {
            //for (p1 = 0; p3 < 256; p3++) {
            for (p4 = 0; p4 < 256; p4++) {
                if (Client.av_port.size() == 0) {
                    Thread.sleep(10);
                    continue;
                }
                InetAddress ip = InetAddress.getByName(p1 + "." + p2 + "." + p3 + "." + p4);
                byte buf[] = null;
                String ping = InetAddress.getLocalHost().getHostAddress()+"@"+Client.av_port.get(0);
                buf = ping.getBytes();
                DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 6969);
                ds.send(DpSend);
            //}
            //}
            //}
            }
        Thread.sleep(2);
        }
    } catch (Exception e) {System.out.println("error in serv_ping "+e.getMessage());}
    }
}

class utility{
    public static void notify_other_clients(String clnt, ArrayList<Client> clients) throws IOException {
        for(int i = 0; i<clients.size(); i++){
            if(clients.get(i).name.equals(clnt)) continue;
            clients.get(i).dos.writeUTF("Server");
            clients.get(i).dos.writeUTF(utility.encrypt(clnt+" connected to server!!!", "encription_is_key"));
        }
    }
    public static void send_instructions(DataOutputStream dos, String clnt) throws IOException {
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
        dos.writeUTF(utility.encrypt(Instructions, "encription_is_key"));
    }
    public static void send_current_client_list(DataOutputStream dos, ArrayList<Client> clients) throws IOException {
        String s = "";
        for(int i = 0; i<clients.size(); i++){
            s = s+" "+clients.get(i).name;
        }
        dos.writeUTF("Server");
        dos.writeUTF(utility.encrypt(s,"encription_is_key"));
    }
    public static String encrypt(String s, String key){
        StringBuilder se = new StringBuilder();
        for (int i = 0, j = 0; i<s.length(); i++){
            se.append((char) (s.charAt(i)+key.charAt(j)));
            j++;
            if(j==key.length()) j = 0;
        }
        return se.toString();
    }
    public static String decrypt(String s, String key){
        StringBuilder sd = new StringBuilder();
        for (int i = 0, j = 0; i<s.length(); i++){
            sd.append((char) (s.charAt(i)-key.charAt(j)));
            j++;
            if(j==key.length()) j = 0;
        }
        return sd.toString();
    }
    public static void set_key(String name, String key, ArrayList<Client> clients){
        for (int i = 0; i<clients.size(); i++){
            if(name.equals(clients.get(i).name)){
                clients.get(i).key = key;
                return;
            }
        }
        Client cl = new Client(name, key);
        clients.add(cl);
    }
    public static String find_key(String name, ArrayList<Client> clients){
        for (int i = 0; i<clients.size(); i++){
            if(name.equals(clients.get(i).name)){
                return clients.get(i).key;
            }
        }
        return "encription_is_key";
    }

}