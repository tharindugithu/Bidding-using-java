package server;

//create server socket and waiting for client requests
//call to user authentication

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import java.io.IOException;

public class ServerConnRunnable implements Runnable{
	
	private ServerSocket ss;
	private int sPort;
	
	public ServerConnRunnable() {
		
	}
	
	public ServerConnRunnable(int sport) {
		
		this.sPort = sport;
		
		System.out.println("Server Started");
		
	}
	
	@Override
	public void run() {
		
		try {
			ss = new ServerSocket(sPort,300);
			System.out.println("Wait for client....");
			
			while(true) {
				Socket s = ss.accept();
				System.out.println("Clien connected");
				Thread ct = new Thread() {
					@Override
					public void run() {
						handleClientSocket(s);
					}
				};
				ct.start();
			}
			
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public void handleClientSocket(Socket soc) {
		
		ServerAction saobj = new ServerAction();
		String userName = null;
		
		//call to log or create new account to user
		ServerTask stob = new ServerTask();
		int logflag = stob.uselogingtyp(soc);
		System.out.println("Client loggrd successfully : " + logflag);
		
		if(logflag == 1) {
			userName = stob.getUname();
			System.out.println("Successfully get uname after loging : " + userName);
			while(true) {
				String serTyp = saobj.typOfSevese(soc);
				if(serTyp.equals("Close")) {
					getSubClientandClose(userName); // to say sub client close
					saobj.closeClientCon(soc); // close client server client
					System.out.println("Client disconnect");
					break;
				}
				saobj.proformServese(soc, serTyp); // do service
			}
		}
		if(logflag == 2) {
			ServerComAction scaobj = new ServerComAction();
			userName = stob.getUname();
			System.out.println("Successfully get uname after Company loging : " + userName);
			while(true) {
				String serTyp = scaobj.cTypOfSevese(soc);
				if(serTyp.equals("Close")) {
					getSubClientandClose(userName); // to say sub client close
					scaobj.closeCompanyCon(soc); // close client server client
					System.out.println("Company disconnected disconnect");
					break;
				}
				scaobj.proformCServese(soc, serTyp); // do service
			}
		}
		else {
			try {
				soc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Client disconnect");
		}
		
	}
	
	// close socket when needed
	public int socClose(Socket s) {
		if(s != null) {
			try {
				s.close();
				System.out.println("Soc Closed Last");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Null socket");
		}
		
		return 1;
	}
	
	// close server socket
	public int serverSocClose() {
		
		if(ss != null ) {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Server Soc Offed");
	}
		else {
			System.out.println("Server soc null");
		}
		
		return 1;
	}
	
	// close subscriber connection
	public void getSubClientandClose(String uName) {
		ServerSubHandler ssh = getWithName(uName);
		ssh.closeSubConnection();
	}
	
	// get subscriber client ServerSubHandler to close
	private ServerSubHandler getWithName(String userNam) {
		
		ServerSubHandler ressh = null;
		
		StoreSubConn sco = new StoreSubConn(1);
		
		List<ServerSubHandler> workersList = sco.getSubWorkers();
		System.out.println("Get for Successfully close :" + userNam);
		for(ServerSubHandler swo : workersList) {
			System.out.println("Get for Successfully close :.........." + userNam);
			// check with user name
			if(swo.getSubTUname().equals(userNam)) {
				ressh = swo;
				System.out.println("Get for Successfully close");
			}
		}
		
		return ressh;
	}
	

}
