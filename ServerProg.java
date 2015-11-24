import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ServerProg implements Runnable{
    
 private static Socket socket;
  ExecutorService executor = Executors.newFixedThreadPool(5);  //Thread Pool of 5
   static int i=1; //client no initialized
 
    public static void main(String[] args)
    {
        try
        {
 ServerProg sr=new ServerProg();
  Thread tr1=new Thread(sr);
  tr1.start();
            
          
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
     
    } 
    private void socketWork(ExecutorService executor, final Socket socket,final int ivalue) {
    executor.execute(new Runnable() {
        public void run() {
             ReadMesssageServer rm=new ReadMesssageServer(socket,ivalue);
            Thread tr1=new Thread(rm);
            tr1.start();  
            WriteMesssageServer wms=new WriteMesssageServer(socket,ivalue);
            Thread tr2=new Thread(wms);
            tr2.start(); 
        }
    });
}
     public void run(){

            try{
              
             int port = 5000;
            int flag=0;
            ServerSocket serverSocket = new ServerSocket(port);
              while(true){
                 // System.out.println("i value"+i);
                  if(i<=5){
            System.out.println("Server Started and listening to the port 5000");  
            socket = serverSocket.accept();
            System.out.println("client"+i+" connected.");
            socketWork(executor, socket,i);
 
            i++;
            flag=0;
                  }
                  else{
                      if(flag==0){
                      System.out.println("Sorry!...No more client to connect with the pool");  
                      flag=1;
                      }
                      
                  }
                }
            }
            catch(Exception e){
                System.out.println(""+e);  
            }
         } 
}
class WriteMesssageServer implements Runnable{
       private static Socket socket;
       int ivalue=0;
                       final static String INET_ADDR = "224.0.0.3";
  final static int PORT = 8888;
       WriteMesssageServer(Socket wSocket,int i){
           socket=wSocket;
           ivalue=i;
       }
   public void run(){

            try{
             OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String msg = in.readLine();
                    
//                bw.write(in.readLine()+"\n");
//                InetAddress ipAddr = InetAddress.getLocalHost();
//            System.out.println(ipAddr.getHostAddress());
//
//                bw.write("ip: "+ipAddr.getHostAddress()+"\n");
//                bw.write("port: 5000"+"\n");
//                bw.write("Student ID: 15310419"+"\n");
//                bw.flush();
                
   
         //new changes 
 
                 InetAddress addr = InetAddress.getByName(INET_ADDR);
                DatagramSocket serverSocket = new DatagramSocket();
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length,addr, PORT);
                serverSocket.send(msgPacket);

            }
            catch(Exception e){
                
            }
         } 
}
class ReadMesssageServer implements Runnable{
       private static Socket socket;
       int ivalue=0;
       ReadMesssageServer(Socket rSocket,int i){
           socket=rSocket;
           ivalue=i;
           System.out.println("constructor "+ivalue);
       }
  public void run(){
          try{
              ServerProg sr=new ServerProg();
            System.out.println("Waiting for client response");
           InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                while(true){
                String message = br.readLine();
                if(!message.equals("")){
                System.out.println("Message received from client"+ivalue+" is :: "+message);
                
                }
                    if (message.equalsIgnoreCase("Bye")) {

                        int j = --sr.i;
                        System.out.println("j value"+j);
                        try {

                            if (j == 1) {
                                socket.close();
                            }
                        } catch (Exception e) {
                        }
                        System.out.println("Bye client"+ivalue+" ....");
                        if (j == 1) {
                             System.out.println("All Clients close their connections from pool");
                             System.out.println("Bye...");
                            System.exit(0);
                        }
                    } else {
                        // System.out.println("Server message");
                }
            }
            }
            catch(Exception e){
                
            }
        }
}
