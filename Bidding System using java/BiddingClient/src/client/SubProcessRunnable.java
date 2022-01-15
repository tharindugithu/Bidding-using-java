package client;

// this class used to handle subscriber client

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import clientGui.ActionGUI;

public class SubProcessRunnable implements Runnable{
	
	private Socket subSoc;
	private String name;
	private ActionGUI acgui;
	private CMsg cmsg;
	private DataInputStream in = null;
	
	public SubProcessRunnable(Socket soc, String uNme, ActionGUI agui) {
		this.subSoc = soc;
		this.name  = uNme;
		this.acgui  = agui;
		cmsg = new CMsg();
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		subscriberSocHandle();
	}
	
	// first send user name and get subscriber list with details and set table
	// after reserve server messages and do related action
	private void subscriberSocHandle() {
		cmsg.sentThSocket(subSoc, name); // send user name
		acgui.createITMTablemodel(subSoc); // create table
		while(true) {
			String upd = cmsg.reserve(in, subSoc); // reserve message
			System.out.println("Update Reserved From BID : " + upd);
			doClientSubAction(upd); //call to action
			if(upd.equals("OKC")) {
				break; // end reserve to close connection
			}
		}
	}
	
	// do action related to message
	private void doClientSubAction(String cmd) {
		
		String[] token = cmd.split(" ");
		// update price and refresh table
		if(token[0].equals("PUPDATE")) {
			System.out.println(token[0] + "......" + token[1] + "....." +  token[2]);
			doPriceUpdateCreTble(token[1],token[2]);
		}
		else if(token[0].equals("PRUPDATE")) {
			System.out.println(token[0] + "......" + token[1] + "....." +  token[2]);
			doProfitUpdateCreTble(token[1],token[2]);
		}
		// close connection
		else if(token[0].equals("OKC")) {
			doCloseSubClient();
		}
	}
	
	// do price update and refresh table
	private void doPriceUpdateCreTble(String sym, String prc) {
		System.out.println("Going to create UPdate Table..............");
		acgui.createPUpdateTable(sym,prc);
	}
	
	// do profit update and refresh table
	private void doProfitUpdateCreTble(String sym, String pro){
		System.out.println("Going to create UPdate Table..............");
		acgui.createProUpdateTable(sym, pro);
	}
	
	// close subscriber connection
	private void doCloseSubClient() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			subSoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("All Closed Subscriber Client without Errors");
	}
}
