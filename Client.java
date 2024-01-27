import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Client{
    String name;
    DataInputStream dis;
    DataOutputStream dos;
    int port;
    ServerSocket ss;
    Socket s;

    Client(String name, DataInputStream dis, DataOutputStream dos, int port, ServerSocket ss, Socket s){
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.port = port;
        this.ss = ss;
        this.s = s;
    }

    void disconnect(){
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