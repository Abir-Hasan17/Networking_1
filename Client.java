import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Client{
    String name;
    String key;
    DataInputStream dis;
    DataOutputStream dos;
    int port;
    ServerSocket ss;
    Socket s;
    static ArrayList<Integer> av_port = new ArrayList<>();

    Client(String name, DataInputStream dis, DataOutputStream dos, int port, ServerSocket ss, Socket s){
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.port = port;
        this.ss = ss;
        this.s = s;
        this.key = "encription_is_key";
    }

    Client(String name, String key){
        this.name = name;
        this.key = key;
    }

    public void disconnect(){
        try{
            dis.close();
            dos.close();
            ss.close();
            s.close();
        }catch (IOException e){
            System.out.println("error in disconnect method");
        }
    }

}