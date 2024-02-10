import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DSender {
    public static void main(String args[]) throws IOException
    {
        Scanner sc = new Scanner(System.in);
        DatagramSocket ds = new DatagramSocket();

        for (int i = 0; i<256; i++){
            InetAddress ip = InetAddress.getByName("192.168.0."+i);
            byte buf[] = null;
            String inp = "192.168.0.104"+"@"+"123";
            buf = inp.getBytes();
            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 1234);
            ds.send(DpSend);
        }
    }
}
