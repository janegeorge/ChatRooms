import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
public class ClientProg{
    private static Socket socket;
 
   
    public static void main(String args[])
    {
        try
        {
            String host = "localhost";
            int port = 5000;
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);
            //socket = new Socket();
            if(socket.isClosed()==false){
            System.out.println("Connected with server");
            WriteMesssage wm=new WriteMesssage(socket);
            Thread tr=new Thread(wm);
            tr.start();
            ReadMesssage rm=new ReadMesssage(socket);
            Thread tr1=new Thread(rm);
            tr1.start();
            }
            else{
               System.out.println("Client is not connected"); 
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

    }
}
class WriteMesssage implements Runnable{
       private static Socket socket;
       WriteMesssage(Socket wSocket){
           socket=wSocket;
       }
   public void run(){
            try{
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                while(true){
                   System.out.println("Enter your message");
                   String message=in.readLine();
            bw.write(message+"\n");
                 if(message.equalsIgnoreCase("Bye")){
                     try
            {
                break;
                //Thread.sleep(1000);
                //socket.close();
                //System.exit(0);
            }
            catch(Exception e){}
                     
                }
                
            bw.flush();
               }
            bw.flush();
            System.exit(0);
            }
            catch(Exception e){
                System.out.println("error1"+e);
            }
     
    } 
}
class ReadMesssage implements Runnable{
       private static Socket socket;
       final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;
       ReadMesssage(Socket rSocket){
           socket=rSocket;
       }
  public void run(){
 
            try{
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
           InetAddress address = InetAddress.getByName(INET_ADDR);
            byte[] buf = new byte[256];
            MulticastSocket clientSocket = new MulticastSocket(PORT);
            clientSocket.joinGroup(address);
           while(true){
//            String message = br.readLine();
//            if(message.equals("null"))
//            {
//                System.exit(0);
//            }
//            //System.out.println("Message received from the server :: " +message);
//            //System.out.println("Enter your message");
//            System.out.println(message);
               
               
               DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                clientSocket.receive(msgPacket);

                String msg = new String(buf, 0, buf.length);
                System.out.println("message from server: " + msg);
             }
            }
            catch(Exception e){
                  System.out.println("error2"+e);
                System.exit(0);
            }  
    }  
}
