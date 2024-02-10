import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DReceiver {
    public static void main(String[] args) throws IOException
    {
        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket ds = new DatagramSocket(1234);
        byte[] receive = new byte[65535];

        DatagramPacket DpReceive = null;
        DpReceive = new DatagramPacket(receive, receive.length);
        ds.receive(DpReceive);
        InetAddress ip = ds.getInetAddress();
        String s = data(receive).toString();
        String[] arr = s.split("@",2);
        int port = Integer.parseInt(arr[1]);
        System.out.println("Client:-" + arr[0]);
        System.out.println(port);
        receive = new byte[65535];

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
