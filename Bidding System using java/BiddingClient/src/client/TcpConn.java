package client;

//get and close socket

import java.net.Socket;
import java.io.IOException;

public class TcpConn {
	
	private String sIP; //Server IP
	private int port;//server port
	private Socket cs;
	
	//constructor for create client socket
	public TcpConn(String sip, int cPort){
		
		sIP  = sip;
		port =cPort;
		
		try {
			cs = new Socket(sIP, port);
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}
	
	//to close socket
	public void tcpClose() {
		
		try {
			cs.close();
			System.out.println("Port close");
		}
		catch(IOException e){
			System.err.println(e);
		}
		
	}
	
	//return socket
	public Socket getSocket() {
		return cs;
	}

}
