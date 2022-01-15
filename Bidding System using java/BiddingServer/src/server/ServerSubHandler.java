package server;

// this class used to handle publisher subscriber client processes in server side

import java.net.Socket;

import msgHadle.Msg;

public class ServerSubHandler extends Thread{
	
	private final Socket subSoc;
	private final ServerSubConnRunnable sSubConR;
	private String useName = null;
	private Msg msg;
	private boolean status = false;
	private ServerAction sao = null;
	private ServerComAction scao = null;
	
	public ServerSubHandler(ServerSubConnRunnable sscr, Socket sSoc) {
		this.subSoc = sSoc;
		this.sSubConR = sscr;
		msg = new Msg();
	}
	
	@Override
	public void run() {
		handleSubClientSocket();
	}
	
	// get client user name
	public String getSubTUname() {
		return useName;
	}
	
	// check access to send messages
	public boolean getSendStatus() {
		return status;
	}
	
	// Subscriber client process handle
	private void handleSubClientSocket() {
		
		System.out.println("Subscribr Process.. here.....");
		sao = new ServerAction();
		
		this.useName = sao.getClientUname(subSoc); // get user name from client and set in this class
		System.out.println(useName.substring(0,1) + "......................................................................");
		if((useName.substring(0,1)).equals("C")) {
			scao = new ServerComAction();
			String pro = scao.cTypOfSevese(subSoc);
			System.out.println("My u Name is " + useName);
			int serSub = scao. proformCServese(subSoc,pro);
		}
		else {
			String pro = sao.typOfSevese(subSoc); // get service type
			System.out.println("My u Name is " + useName);
			int serSub = sao.proformServese(subSoc, pro);
		}
		
		status = true;
	}
	
	// send message to client using this class
	public synchronized void subSen(String mess) {
		msg.sentThSocket(subSoc, mess);
		System.out.println("Thread send " + useName);
	}
	
	// close publisher subscriber connection
	public void closeSubConnection() {
		disconnectSubClient(this);
		sao.closeClientCon(subSoc);
		System.out.println("Subccriber Closed");
	}
	
	// remove client from stored list
	private void disconnectSubClient(ServerSubHandler sw) {
		StoreSubConn ssco = new StoreSubConn(1);
		ssco.removeWorker(sw);
	}

}
