package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//create server subscriber socket and waiting for client requests
//call to user authentication

public class ServerSubConnRunnable implements Runnable{
	
	private ServerSocket subSoc ;
	private int subPort;
	private StoreSubConn ssc;
	
	// this constructor used to access methods in this class
	public ServerSubConnRunnable() {
		
	}
	
	// to create thread
	public ServerSubConnRunnable(int sport) {
		subPort = sport;
		System.out.println("Server Subscriber Started");
		ssc = new StoreSubConn();
	}
	
	
	
	@Override
	public void run() {
		
		try {
			subSoc = new ServerSocket(subPort, 300); // create publisher subscriber server socket
			System.out.println("Wait for subscriber client....");
			
			while(true) {
				Socket sub = subSoc.accept(); // wait for client connection request
				System.out.println("Subscribrt Client connected");
				ServerSubHandler scho = new ServerSubHandler(this,sub); // create thread
				ssc.setWorker(scho); // add ServerSubHandler objects to list
				scho.start(); // start thread
			}
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}
	
	
	
	

}
