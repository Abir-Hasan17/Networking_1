import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

class Client{
    String name;
    DataInputStream dis;
    DataOutputStream dos;

    Client(String name, DataInputStream dis, DataOutputStream dos){
        this.name = name;
        this.dis = dis;
        this.dos = dos;
    }
}