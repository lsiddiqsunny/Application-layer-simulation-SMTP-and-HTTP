import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private Socket smtpSocket=null;
    private BufferedReader in=null;
    private PrintWriter pr=null;
    private String state,prevstate;
    public Server(){
        prevstate=state="closed";

    }

    public Server(InetAddress mailHost, int port,String name,String pass) throws SocketException {
        prevstate="closed";
        state="begin";
        try {
            smtpSocket=new Socket(mailHost,port);//connecting to host
        } catch (IOException e) {
            System.out.println(e.toString());
            state="closed";

        }

        try {
            if(state.equals("begin"))
            in =  new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.toString());
            state="closed";

        }
        try {
            if(state.equals("begin"))
            pr = new PrintWriter(smtpSocket.getOutputStream(),true);
        } catch (IOException e) {
            System.out.println(e.toString());
            state="closed";
        }
        if(state.equals("begin")){
            smtpSocket.setSoTimeout(100000);

            try {
                System.out.println("0 : "+in.readLine());
                pr.println("AUTH LOGIN");
                System.out.println("1 : "+in.readLine());
                pr.println(name);
                System.out.println("2 : "+in.readLine());
                pr.println(pass);
                System.out.println("3 : "+in.readLine());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    String getState(){
        return  state;
    }

    void Reply(){
       // System.out.println(state);
        try {
            String reply;

            reply=in.readLine();
            if(reply!=null) {

                System.out.println(reply);
                if(state.equals("deliver") && reply.charAt(0)=='2'){
                    System.out.println("Mail sent successfully.");
                    state="wait";
                }
                if(state.equals("deliver") && reply.charAt(0)!='2'){
                    System.out.println("Mail sending failed.");
                    state="closed";
                }
                if(reply.charAt(0)=='4') {
                    state="closed";
                    return;
                }
                if(reply.charAt(0)=='5'){
                if(state.equals("wait") && prevstate.equals("begin")){
                    state="begin";
                    prevstate="closed";
                }
                else if(state.equals("from") && prevstate.equals("wait")){
                    state="wait";
                   // System.out.println("Error in your mail address.");
                }
                else if(state.equals("rcpt") && prevstate.equals("from")){
                   // System.out.println("Error in recipient mail address.");
                    state="from";
                    prevstate="wait";

                }else if(state.equals("from") && prevstate.equals("begin")){
                    state="begin";
                    prevstate="begin";
                }
                    System.out.println(reply.substring(4,reply.length()));
                  //  state="closed";
                }
              }

        } catch (Exception e){
            e.printStackTrace();
            state="closed";
        }
    }
    void Request(String req){

            if(state.equalsIgnoreCase("data")){
                pr.println(req);
                if(req.equals(".")){
                    state="deliver";
                }
                return;
            }
            if(req.length()<4){
                pr.println(req);
                return;
            }

            String check= req.substring(0,4);

            if(check.equalsIgnoreCase("RSET")){
                state="wait";
                prevstate="begin";
            }
            if(check.equalsIgnoreCase("QUIT")){
                state="closed";
            }

            if(state.equals("begin") && check.equalsIgnoreCase("HELO")){
                prevstate="begin";
                state="wait";
            }
           else if(state.equals("begin") && check.equalsIgnoreCase("MAIL")){
                prevstate="begin";
                state="from";
            }
            else if(state.equals("begin")){
                prevstate="begin";
                state="begin";
            }

            if(state.equals("wait") && check.equalsIgnoreCase("MAIL")){
                prevstate="wait";
                state="from";
            }
            if(state.equals("wait") && !check.equalsIgnoreCase("MAIL")){
                prevstate="wait";
                state="wait";
            }
            if(state.equals("from") && check.equalsIgnoreCase("RCPT")){
                prevstate="from";
                state="rcpt";
            }
            if(state.equals("from") && !check.equalsIgnoreCase("RCPT")){
                prevstate="from";
                state="from";
            }
            if(state.equals("rcpt") && check.equalsIgnoreCase("RCPT")){
                prevstate="rcpt";
                state="rcpt";
            }
            else if(state.equals("rcpt") && check.equalsIgnoreCase("DATA")){
                prevstate="rcpt";
                state="data";
            }
            else if(state.equals("rcpt")){
                prevstate="rcpt";
                state="rcpt";
            }



            pr.println(req);

    }


}
