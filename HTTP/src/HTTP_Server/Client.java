package HTTP_Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        Socket socket = new Socket (addr, 6789);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("GET / HTTP/1.1");
        out.println("Connection : Keep Alive");
        out.println();
        Scanner sc=new Scanner(System.in);
        while(true)
        {
            String s = in.readLine();

            while( true) {
                s= in.readLine();
                if(s==null) break;
                if(s.equals("$")) break;
                System.out.println(s);

            }
            if(s==null) break;
           // System.out.println("Hello");
            //GET /http_post.html HTTP/1.1
            //Connection : Keep Alive
            out.println(sc.nextLine());
            out.println(sc.nextLine());
            out.println();
        }
       socket.close();
    }
}