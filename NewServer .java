/*jane george*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.*;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.net.InetAddress;
		public class NewServer
		{
			private ServerSocket serverSocket;
			Socket socket;
			private static final ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
			private int clientId=0;
			private int chatRoomNo=100;
		
			List<ChatRoom> chatRoomList = new ArrayList<ChatRoom>();
			List<CThread> clientLists = new ArrayList<CThread>();
			InetAddress ipAddr;
			
		
			public NewServer(ServerSocket serverSocket)
			{
				this.serverSocket = serverSocket;
				try{
				ipAddr = InetAddress.getLocalHost();
				}
				catch(Exception e)
				{
					System.out.println(e);
				}
			}
			//main function
			public static void main(String[] args) throws Exception
			{				   
				NewServer newSoc = new NewServer(new ServerSocket(5000));
				newSoc.initializeChatRooms();
				newSoc.start();
			}
			
			//accept client connection
			public void start()
			{
				try
				{
					while(true)
					{	
						if(poolExecutor.getActiveCount()<10)
						{										
							System.out.println("Waiting for a client to get connected to the port 5000\n");													
							socket = serverSocket.accept();
							System.out.println("Connected to a client "+clientId+1);
							clientId++;
							CThread client = new CThread(socket,this,clientId);
							clientLists.add(client);
							NewServer.poolExecutor.execute(client);
						}
						
					}						                       
				}
				catch(Exception e)
				{
					System.out.println(e);
				}	
			}
			
			// close server socket
			public void killService()
			{
				try
				{
					
					socket.close();
					System.exit(0);
				}
				catch(Exception e)
				{
					System.out.println(e);
					System.exit(0);
	
				}
			}
			
			//To initialize the chat rooms of server
			public void initializeChatRooms()
			{
				for(int i=0;i<8;i++)
				{
					String chatRoomName = "room"+(i+1);
					int chatRoomId = chatRoomNo+1;
					chatRoomNo++;
					ChatRoom newChatRoom = new ChatRoom(chatRoomName ,chatRoomId);
					chatRoomList.add(newChatRoom);
				}
			}
			
			
			//To join 
			public void joinChatRoom(String chatRoomName,CThread CThread)
			{
				ListIterator listlookup = chatRoomList.listIterator();
				while(listlookup.hasNext())
				{
					ChatRoom cr = (ChatRoom)listlookup.next();
					if((cr.chatRoomName).equals(chatRoomName))
					{
						cr.addClient(CThread);
						break;
					}					
				}
			}
			
			//leave a chat room
			public void leaveChatRoom(int chatRoomId,CThread CThread)
			{
				ListIterator listleave = chatRoomList.listIterator();
				while(listleave.hasNext())
				{
					ChatRoom cr = (ChatRoom)listleave.next();
					if(cr.chatRoomId==chatRoomId)
					{
						cr.removeClient(CThread);
						break;
					}					
				}
			}
			
			
			public void leaveAllChatRooms(CThread cThread)
			{
				ListIterator listleave=chatRoomList.listIterator();
				while(listleave.hasNext())
				{
					ChatRoom cr=(ChatRoom)listleave.next();
					ListIterator listclientsConnected=cr.clientsConnected.listIterator();
					while(listclientsConnected.hasNext())
					{
						CThread ctCompare=(CThread)listclientsConnected.next();
						if(ctCompare.clientName.equals(cThread.clientName))
						{
							cr.disconnect(cThread);
							break;
						}
					}					
				}
			}
			
			public void chat(int chatRoomId,CThread CThread,String message)
			{
				ListIterator listchat=chatRoomList.listIterator();
				while(listchat.hasNext())
				{
					ChatRoom cr=(ChatRoom)listchat.next();
					if(cr.chatRoomId==chatRoomId)
					{
						cr.chat(CThread,message);
						break;
					}					
				}
			}
			
			public void displayTheClients(String chatRoomName)
			{
				ListIterator lisdisplay=chatRoomList.listIterator();
				while(lisdisplay.hasNext())
				{
					ChatRoom cr=(ChatRoom)lisdisplay.next();
					if((cr.chatRoomName).equals(chatRoomName))
					{
						ListIterator listClientDisplay=cr.clientsConnected.listIterator();
						while(listClientDisplay.hasNext())
						{
							CThread ct=(CThread)listClientDisplay.next();
							System.out.println("Client :" + ct.clientId);
						}
						break;
					}					
				}	
				System.out.println("Displayed the clients currently in the chat room");
			}
	
			public class CThread implements Runnable
			{
				private Socket socket;
				private NewServer newserverSoc;
				private boolean kill;
				private int clientId;
				private String clientName;
			
				CThread(Socket socket,NewServer newserverSoc,int clientId)
				{
					this.socket=socket;
					this.newserverSoc=newserverSoc;
					this.kill =false;
					this.clientId=clientId;
				}

				public void joinChatRoom(String chatRoomName, CThread CThread)
				{
					newserverSoc.joinChatRoom(chatRoomName,CThread);	
				}
				
				public void leaveChatRoom(int chatRoomId, CThread CThread)
				{
					newserverSoc.leaveChatRoom(chatRoomId,CThread);	
				}
				
				public void leaveAllChatRooms(CThread CThread)
				{
					newserverSoc.leaveAllChatRooms(CThread);
				}
				
				public void chat(int chatRoomId, CThread CThread, String message)
				{
					newserverSoc.chat(chatRoomId, CThread,message);	
				}
				public void killService()
				{
					newserverSoc.killService();	
				}
			
						
				public void run()
				{
					try
					{
						BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
						while(!kill)
						{
							List<String> inputStrings=new ArrayList<String>();
							String data = "";

							while(br.ready())
							{
								inputStrings.add(br.readLine());
							}
							ListIterator liststring=inputStrings.listIterator();
							while(liststring.hasNext())
							{
								data=data+(String)(liststring.next());
							}

							if(data.equals("HELO BASE_TEST"))
							{
								String messagetoclient="HELO BASE_TEST";
								messagetoclient=messagetoclient + "\nIP:"+ipAddr.getHostAddress()+"\nPort:5000\nStudentID:15310419";
								output.println(messagetoclient);
							}
							else if(data.startsWith("JOIN_CHATROOM")==true)
							{	
								String chatRoomName =((String)(inputStrings.get(0))).split(":")[1];
								this.clientName =((String)(inputStrings.get(3))).split(":")[1];
								joinChatRoom(chatRoomName,this);
												                   
							}
							else if(data.startsWith("CHAT")==true)
							{
								int chatRoomId =Integer.parseInt((((String)(inputStrings.get(0))).split(":")[1]).trim());
								String message =(((String)(inputStrings.get(3))).split(":")[1].trim());
								chat(chatRoomId,this,message+"\n\n");
							}
							else if(data.startsWith("LEAVE_CHATROOM")==true)
							{
								int chatRoomId =Integer.parseInt((((String)(inputStrings.get(0))).split(":")[1]).trim());
								leaveChatRoom(chatRoomId,this);
							}
							else if(data.startsWith("DISCONNECT")==true)
							{
								leaveAllChatRooms(this);
								socket.close();
							}
							else if(data.equals("KILL_SERVICE"))
							{

								kill=true;
								socket.close();
								killService();
						
							}
							else
							{
									
							}
						}
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
				}
			}
			
			public class ChatRoom
			{
				private String chatRoomName;
				private int chatRoomId;
				List<CThread> clientsConnected=new ArrayList<CThread>();

				ChatRoom(String chatRoomName,int chatRoomId)
				{
					this.chatRoomName=chatRoomName;
					this.chatRoomId=chatRoomId;
				}

				public void addClient(CThread CThread)
				{
					this.clientsConnected.add(CThread);
					String messagetoclient="JOINED_CHATROOM:"+this.chatRoomName+"\nSERVER_IP:"+ipAddr.getHostAddress()+"\nPORT:5000\nROOM_REF:"+this.chatRoomId+"\nJOIN_ID:"+CThread.clientId+"\n";
					sendMessage(messagetoclient,CThread);
					chat(CThread,CThread.clientName+" has joined this chatroom.\n\n");
				}
				
				public void removeClient(CThread CThread)
				{
					String messagetoclient="LEFT_CHATROOM:"+this.chatRoomId+"\nJOIN_ID:"+CThread.clientId+"\n";
					sendMessage(messagetoclient,CThread);
					chat(CThread,CThread.clientName+"has left this chatroom.\n\n");
					this.clientsConnected.remove(CThread);
					
				}
				
				public void disconnect(CThread CThread)
				{
					chat(CThread,CThread.clientName+"has left this chatroom.\n\n");
					this.clientsConnected.remove(CThread);
					
				}
				
				
				public void chat(CThread CThread, String message)
				{
					ListIterator listClientChat=clientsConnected.listIterator();
					String messageToClients = "CHAT:"+this.chatRoomId+"\nCLIENT_NAME:"+CThread.clientName+"\nMESSAGE:"+message;
					if(!listClientChat.hasNext())
					{
						sendMessage(messageToClients,CThread);
					}
					while(listClientChat.hasNext())
					{
						CThread ct = (CThread)listClientChat.next();
						sendMessage(messageToClients,ct);
					}
								
				}
			
				public void sendMessage(String messagetoclient,CThread CThread)
				{
					try
					{
						PrintWriter output=new PrintWriter(CThread.socket.getOutputStream(),true);
						System.out.println("Message to Client / Chat Room : " + messagetoclient);
						output.printf(messagetoclient);	
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
					
				}
			
				
			}
			
																																	
		}