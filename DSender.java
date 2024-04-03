//p
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DSender {
    public static void main(String args[]) throws IOException
    {
        ServerSocket ss = new ServerSocket(6666);
        Socket  s = ss.accept();

        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        String f_name = new Scanner(System.in).nextLine();
        String arr[] = f_name.split("\\.",2);
        String name = arr[0];
        String extension = arr[1];

        File f = new File(f_name);
        if (f.createNewFile()) {
            System.out.println("File created: " + f.getName()+"\n an empty file will be sent!!!");
        } else {
            System.out.println("File sent: "+name+'.'+extension);
        }
        String str = "";
        Scanner fsc = new Scanner(f);
        while(fsc.hasNextLine()){
            str = str+fsc.nextLine()+"\n";
        }

        dos.writeUTF(name);
        dos.writeUTF(extension);
        dos.writeUTF(str);



    }
}
//p
