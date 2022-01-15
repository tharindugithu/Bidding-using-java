package company;

// this class handle company subscriber connection and process handling

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import companyGUI.CompanyActionGUI;

public class ComSubProcessRunnable implements Runnable{
	
	private Socket subSoc;
	private String name;
	private CompanyActionGUI comacgui;
	private ComMsg comsg;
	private DataInputStream in = null;
	
	public ComSubProcessRunnable(Socket soc, String uNme, CompanyActionGUI thegui) {
		this.subSoc = soc;
		this.name  = uNme;
		this.comacgui  = thegui;
		comsg = new ComMsg();
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		coSubscriberSocHandle();
	}
	
	// first send company name and get subscriber list with details and set table
	// after reserve server messages and do related action
	private void coSubscriberSocHandle() {
		comsg.sentThSocket(subSoc, name); // send user name
		comacgui.createITMTablemodel(subSoc);
		
		while(true) {
			String upd = comsg.reserve(in, subSoc); // reserve message
			System.out.println("Update Reserved From BID : " + upd);
			doCompanySubAction(upd); //call to action
			if(upd.equals("OKC")) {
				break; // end reserve to close connection
			}
		}
	}
	
	// do action related to message
	private void doCompanySubAction(String cmd) {
		
		String[] token = cmd.split(" ");
		// update price and refresh table
		if(token[0].equals("PUPDATE")) {
			System.out.println(token[0] + "......" + token[1] + "....." +  token[2]);
			doPriceUpdateCreTble(token[1],token[2]);
		}
		//update profit and refresh table
		else if(token[0].equals("PRUPDATE")) {
			System.out.println(token[0] + "......" + token[1] + "....." +  token[2]);
			doProfitUpdateCreTble(token[1],token[2]);
		}
		// close connection
		else if(token[0].equals("OKC")) {
			doCloseSubCompany();
		}
	}
	
	// do price update and refresh table
	private void doPriceUpdateCreTble(String sym, String prc) {
		System.out.println("Going to create UPdate Table..............");
		comacgui.createPUpdateTable(sym,prc);
	}
	
	// do price update and refresh table
	private void doProfitUpdateCreTble(String sym, String pro) {
		System.out.println("Going to create UPdate Table..............");
		comacgui.createProUpdateTable(sym,pro);
	}
	
	// close subscriber connection
	private void doCloseSubCompany() {
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
		
		System.out.println("All Closed Subscriber Company without Errors");
	}
}
