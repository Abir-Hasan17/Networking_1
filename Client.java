import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

class Client{
    String name;
    DataInputStream dis;
    DataOutputStream dos;
    int port;

    Client(String name, DataInputStream dis, DataOutputStream dos, int port){
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.port = port;
    }
}