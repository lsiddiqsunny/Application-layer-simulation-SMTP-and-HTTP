package HTTP_Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import static HTTP_Server.Main.logfile;

public class Main {
    static final int PORT = 6789;
    static final File logfile=new File("log.txt");

    public static void main(String[] args) throws IOException {
        ServerSocket serverConnect = new ServerSocket(PORT);
        FileWriter fr=new FileWriter(logfile);
        fr.write(new Date().toString()+" : "+"Server started.\r\n");
        fr.close();
        System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
        while(true)
        {
            Socket s=serverConnect.accept();
            Worker wt = new Worker(s);
            Thread t = new Thread(wt);
            t.start();

        }

    }
}

class Worker implements Runnable {

    private Socket connectionSocket;
    private PrintWriter outToClient;
    private BufferedReader inFromClient;
    private  BufferedOutputStream fileOut;

    public Worker(Socket s) throws IOException {
        this.connectionSocket = s;
        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        outToClient = new PrintWriter(connectionSocket.getOutputStream());
        fileOut=new BufferedOutputStream(connectionSocket.getOutputStream());
    }

    public void run() {


            try {
                String input = inFromClient.readLine();
                System.out.println("Input : "+input);
                String[] args=input.split(" ");

                FileWriter fr=new FileWriter(logfile,true);
                fr.write(new Date().toString()+" : "+input+"\r\n");
                fr.close();
                /*
                for(String s:args){
                    System.out.println(s);
                }*/
                if(args[0].equals("GET")){

                    if(args[1].equals("/")){
                        //System.out.println("here");
                       /* System.out.println("Working Directory = " +
                                System.getProperty("user.dir"));*/
                       ShowFile("./index.html","text/html");


                    }else{
                        String fileextension=getFileExtension(args[1]);
                        if(fileextension.length()==0){
                            FileNotFound();
                        }
                        else{
                            String filecontent="text/plain";
                            if(fileextension.equals("html") || fileextension.equals("htm")){
                                filecontent="text/html";
                            }
                            else if(fileextension.equals("jpeg")||fileextension.equals("jpg")){
                                filecontent="image/jpeg";
                            }
                            else if(fileextension.equals("pdf")){
                                filecontent="application/pdf";
                            }
                            else if(fileextension.equals("css")){
                                filecontent="text/css";
                            }
                            else if(fileextension.equals("png")){
                                filecontent="image/png";
                            }
                            else if(fileextension.equals("bmp")){
                                filecontent="image/bmp";
                            }
                            else if(fileextension.equals("tiff")){
                                filecontent="image/tiff";
                            }
                            else if(fileextension.equals("svg")){
                                filecontent="image/svg+xml";
                            }
                            //System.out.println(fileextension+" " +filecontent);
                            ShowFile("./"+args[1],filecontent);
                        }




                    }
                }
                else if(args[0].equals("POST")){

                    while((input = inFromClient.readLine()).length() != 0){
                        //System.out.println(input);
                    }

                    String payload = new String("");
                    while(inFromClient.ready()){
                        payload=payload+((char) inFromClient.read());
                    }
                    int index=payload.indexOf('=');
                    payload=payload.substring(index+1);
                    File f;
                    f = new File(".",args[1]);
                    if(!f.exists()){
                        FileNotFound();
                        //return;
                    }
                    else{
                        PrintWriter fi=new PrintWriter(f);

                        fi.write("<html><body><h1>Your name is "+payload+"</h1></body></html>");
                        fi.close();

                    }
                    ShowFile("./"+args[1],"text/html");

                    System.out.println(payload);


                }else{
                    NotImplementError();
                }
                connectionSocket.close();
                inFromClient.close();
                outToClient.close();
                fileOut.close();

            } catch (IOException e) {
                e.printStackTrace();

            }



    }
    void NotImplementError() throws IOException {
        String mssg="Error 501 : Command Not Implemented\r\n";


        FileWriter fr=new FileWriter(logfile,true);
        fr.write(new Date().toString()+" : "+mssg);
        fr.close();

        System.out.print(mssg);
        File f = new File("501.html");
        byte[] fileData = FiletoByte(f);
        outToClient.println("HTTP/1.1 404 Not Found");
        outToClient.println("Server: Local Host");
        outToClient.println("Date: " + new Date());
        outToClient.println("Content-type: text/html" );
        outToClient.println("Content-length: " + f.length());
        outToClient.println();
        outToClient.flush();
        //outToClient.print(f);

        fileOut.write(fileData,0,(int)f.length());
        fileOut.flush();
    }
    void FileNotFound() throws IOException {

        String mssg="Error 404 : File not found\r\n";
        FileWriter fr=new FileWriter(logfile,true);
        fr.write(new Date().toString()+" : "+mssg);
        fr.close();
        System.out.print(mssg);
        File f = new File("404.html");
        byte[] fileData = FiletoByte(f);
        outToClient.println("HTTP/1.1 404 Not Found");
        outToClient.println("Server: Local Host");
        outToClient.println("Date: " + new Date());
        outToClient.println("Content-type: text/html" );
        outToClient.println("Content-length: " + f.length());
        outToClient.println();
        outToClient.flush();
       // outToClient.print(f);

        fileOut.write(fileData,0,(int)f.length());
        fileOut.flush();
    }
    void ShowFile(String filename,String content) throws IOException {

        File f;
        f = new File(filename);
        if(!f.exists()){
            FileNotFound();
            return;
        }
        String mssg="HTTP/1.1 200 OK\r\n";
        FileWriter fr=new FileWriter(logfile,true);
        fr.write(new Date().toString()+" : "+mssg);
        fr.close();

        byte[] fileData = FiletoByte(f);
        outToClient.println("HTTP/1.1 200 OK");
        outToClient.println("Server: Local Host");
        outToClient.println("Date: " + new Date());
        outToClient.println("Content-type: "+content );
        outToClient.println("Content-length: " + f.length());
        outToClient.println();
        outToClient.flush();
        //outToClient.print(f);

        fileOut.write(fileData,0,(int)f.length());
        fileOut.flush();


    }
    private byte[] FiletoByte(File file)  {

        byte[] fileData = new byte[(int)file.length()];
        FileInputStream fileIn;
        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
            fileIn.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return fileData;
    }
    String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf+1);
    }
}
