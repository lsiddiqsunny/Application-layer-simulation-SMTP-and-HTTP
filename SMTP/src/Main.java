import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;


public class Main {
    static final String mailServer = "smtp.sendgrid.net";
    static final int port=587;
    static final String username="YXBpa2V5";
    static final String apikey="U0cuNEtXcl8tWkZSZXVJNDVGUmp5MEZsdy5QQVFVckdkcm5mWHVFLVUySXdseUhjV3ZaMkFjcGNqRFFROE9MdzhCLU5N";

    public static void main(String[] args) throws IOException {


        Server smtpServer =new Server(InetAddress.getByName(mailServer),port,username,apikey);

        while(smtpServer.getState().equals("closed")){
            long startTime = System.currentTimeMillis();
            System.out.println("Trying again within 5 seconds.");
            while(startTime+5000>System.currentTimeMillis()){

            }
            System.out.println();
            smtpServer =new Server(InetAddress.getByName(mailServer),port,username,apikey);
        }
        System.out.println("Connection established successfully.");
        //smtpServer.Reply();
        Scanner sc=new Scanner(System.in);
        String req;

        while(true){
            req=sc.nextLine();
            smtpServer.Request(req);
          //  System.out.println(smtpServer.getState());
            smtpServer.Reply();
            if(smtpServer.getState().equals("data")){
                while(!smtpServer.getState().equals("deliver")){
                    req=sc.nextLine();
                    smtpServer.Request(req);

                }
                smtpServer.Reply();
            }

            if(smtpServer.getState().equals("closed")){
                break;
            }
        }

        System.out.println("Connection closed.");

    }
}



