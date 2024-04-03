import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Scanner;

public class DReceiver {
    public static void main(String[] args) throws IOException
    {
        Socket s = new Socket("localhost", 6666);
        DataInputStream dis = new DataInputStream(s.getInputStream());

        String name = dis.readUTF();
        String extension = dis.readUTF();
        String f_name = name  + "." + extension;
        String str = dis.readUTF();
        File f;

        char c = '1';
        while(true){
            if(new File(f_name).exists()){
                f_name = name  + c + "." + extension;
                c++;
            }else{
                f = new File(f_name);
                break;
            }
        }
        if (f.createNewFile()) {
            System.out.println("New File created: " + f.getName());
            Files.writeString(f.toPath(),str);
        }

    }


}
