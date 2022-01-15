package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import csv.ReadCsv;
import msgHadle.Msg;
import readServerTxtFiles.PersonSubItem;
import serverCore.Item;

public class ServerAction {
	
	private DataInputStream in = null;
	private Msg msg = new Msg();
	private ReadCsv readcsv = new ReadCsv(1);
	private PersonSubItem persubob = new PersonSubItem();
	
	/*
	 *  202  - client/company request from server to get Symbol list from server          (Client  send)
	 *  203  - say to client/company to ready to reserve list                             (Server  send)
	 *  204  - say to client/company to symbol list is end                                (Server  send)
	 *  
	 *  SUB  - client request subscribed list with profit and base price                  (client  send)
	 *  SUBY - server reply to say server accept "SUB" request ask user name              (Server  send)
	 *  NEMPTY - server sent to client to say, be ready to start reserve data             (Server  send)
	 *  EMPTY- server send to client to say, no subscribed items                          (Server  send)
	 *  SUBC - server send to client to say, process is completed                         (Server  send)
	 *  
	 *  SYMC - client request to check given symbol is valid and if valid get price and profit (Client send)
	 *  SYNCY- say to client, server accept "SYMC" and ask Symbol from client             (Server  send)
	 *  S1   - say to client, symbol is available and ready to reserve                    (Server  send)
	 *  S0   - say to client, symbol is not available                                     (Server  send)
	 *  SYNCE- sat to client, "SYMC" process is completed                                 (Server  send)
	 *  
	 *  BID  - client request, bid on item                                                (Client  send)
	 *  BIDY - say to client, "BID" is accepted and ask symbol,user name,bid              (Server  send)
	 *  3    - say to client, bidding success and process end                             (Server  send)
	 *  2    - say to client, bidding time was ended. can not bid                         (Server  send)
	 *  0    - say to client, symbol wrong and process end                                (Server  send)
	 *  1    - say to client, bid price less than current value                           (Server  send)
	 *  
	 *  PRFT - client request, subscribe symbol                                           (Client  send)
	 *  PRFTY- say to client, "PRFT" request accepted                                     (Server  send)
	 *  SUB0 - say to client. Subscribed fail                                             (Server  send)
	 *  SUB1 - say to client, subscribed success                                          (Server  send)
	 *  
	 *  Close- client request, close connection                                           (Client  send)
	 *  OKC  - say to client, accept Close request from client                            (Server  send)
	 */
	
	// to reserve about service type from client and return
	public String typOfSevese(Socket s) {
		
		String sTyp = null;
		
		try {
			in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sTyp = msg.reserve(in, s); // reserve service type
		
		return sTyp;
	}
	
	// call to service  request 
	public int proformServese(Socket s, String flg) {
		
		int flag = 0;
		
		if(flg.equals("202")) {
			flag = releseSubList(s);
		}
		else if(flg.equals("PRFT")){
			flag = SubscribeItem(s);
		}
		else if(flg.equals("SUB")) {
			flag = subItemWithDetails(s);
		}
		else if(flg.equals("SYMC")) {
			flag = checkAvaSym(s);
		}
		else if(flg.equals("BID")) {
			flag = makeBid(s);
		}
		
		return flag;
	}
	
	// send Symbol list to client ad return 1 if end function 
	public int releseSubList(Socket s) {
		
		int subLstReFlag = 0;
		
		try {
			in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		msg.sentThSocket(s,"203"); // say to client to ready to reserve list
		
		List<String> symList = getSymList(); // get symbol list
		int sizeArr = symList.size();
		
		msg.sentThSocket(s,Integer.toString(sizeArr)); // send size of the Symbol list
		
		for(int i = 0; i < sizeArr; i++) {
			msg.sentThSocket(s, symList.get(i)); // sent symbol
		}
		
		msg.sentThSocket(s,"204"); // say to client, Symbol list is end
		
		subLstReFlag = 1;
		
		return subLstReFlag;
		
	}
	
	// get symbol list
	private List<String> getSymList() {
		
		List<String> sym = readcsv.getkeySymList(); // get key set of symbols
		
		return sym;
	}
	
	// to subscribe Symbol and send result to client and after process end return 1 // client
	public int SubscribeItem(Socket soc) {
		
		int sFlg = 0, upSymflg = 0;
		
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		msg.sentThSocket(soc,"PRFTY"); // to start send
		
		String subItmN = msg.reserve(in, soc); // reserve subscribe Item with Name
		
		String[] val = subItmN.split(" ");
		
		// send as wrong symbol
		if(readcsv.checkSymble(val[1]) == 0) {
			msg.sentThSocket(soc,"SUB0");
		}
		else {
			
			try {
				List<String> chek = persubob.addOrRes(val[0], val[1]);
				if(chek.contains("1")){
					upSymflg = 1;
				}
				else {
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(upSymflg == 0) {
				sFlg = 1;
				msg.sentThSocket(soc,"SUB0");
			}
			else {
				msg.sentThSocket(soc,"SUB1");
				sFlg = 1;
			}
		}
		
		return sFlg;
	}
	
	// get subscribed item with profit and price and sent to client and after process return 1
	public int subItemWithDetails(Socket soc) {
		
		int flg = 0;
		//Item theItem = new Item("Empty", 0, 0);
		List<Item> symDtaList = new ArrayList<>();
		
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		msg.sentThSocket(soc,"SUBY"); // to say to client to, server accept request and ask user Name
		
		String uName = msg.reserve(in, soc); //reserve User name
		
		symDtaList = getSymWithDataList(uName); // get subscribed item with details
		
		if(symDtaList != null) {
			msg.sentThSocket(soc, "NEMPTY"); // send to say to ready to reserve subscribe list
		}
		else {
			msg.sentThSocket(soc, "EMPTY"); //  send to say to client. no subscribed item
		}
		
		if(symDtaList != null) {
			
			for(int i = 0; i < symDtaList.size(); i++) {
				Item tmpItem = symDtaList.get(i);
				msg.sentThSocket(soc,tmpItem.getSym()); // to sent symbol
				msg.sentThSocket(soc,Float.toString(tmpItem.getPrice())); // to sent price
				msg.sentThSocket(soc,Integer.toString(tmpItem.getProfit())); // to sent profit
			}
		}
		
		msg.sentThSocket(soc, "SUBC"); // sent as finish Item List
		
		flg = 1;
		
		return flg;
	}
	
	// get symbol with price and profit Item List and return Item list
	private List<Item> getSymWithDataList(String uName) {
		
		List<Item> itmWithD = null;
		
		List<String> ItmLst = getSubSymList(uName);
		
		if(!ItmLst.isEmpty()) {
			itmWithD = readcsv.getItmWuthD(ItmLst);
		}
		
		return itmWithD;
	}
	
	// get subscribe symbol list with given user name
	private List<String> getSubSymList(String uName) {
		
		List<String> ItmLst = persubob.addOrRes(uName, "NOSYMBOL"); // get subscribed symbol list
		return ItmLst;
		
	}
	
	// get item details of given Symbol and send to client and after end process return 1
	public int checkAvaSym(Socket soc) {
		
		int sFlg = 0;
		List<String> symlst = new ArrayList<>();
		List<Item> itmlst   = new ArrayList<>();
		
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		msg.sentThSocket(soc,"SYNCY"); // to start reserve symbol
		
		String symbol = msg.reserve(in, soc); //reserve symbol
		
		int chekval = readcsv.checkSymble(symbol); // check Symbol availability
		
		if(chekval == 1) {
			msg.sentThSocket(soc, "S1"); // send as available symbol
			symlst.add(symbol);
			itmlst = readcsv.getItmWuthD(symlst);
			Item tmpItem = itmlst.get(0);
			msg.sentThSocket(soc,tmpItem.getSym()); // to sent symbol
			msg.sentThSocket(soc,Float.toString(tmpItem.getPrice())); // to sent price
			msg.sentThSocket(soc,Integer.toString(tmpItem.getProfit())); // to sent profit
		}
		else {
			msg.sentThSocket(soc, "S0");	// no Symbol like that
		}
		
		msg.sentThSocket(soc, "SYNCE"); // end statement
		
		sFlg = 1;
		
		return sFlg;
	}
	
	// bid price update and keep track it send result to the client and return 1 after process
	public int makeBid(Socket soc) {
		
		int bflg = 0;
		
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		msg.sentThSocket(soc,"BIDY"); // to start reserve bidding details
		
		String bidF = msg.reserve(in, soc); //reserve "userName symbol bidPrice"
		
		String[] val = bidF.split(" ");
		
		int chekval = readcsv.updateBidPrice(val[0], val[1], val[2]); // update value and get result
		
		if(chekval == 3){
			msg.sentThSocket(soc, "3"); // success bid
		}
		else if(chekval == 2) {
			msg.sentThSocket(soc, "2"); // bidding time end, can't bid
		}
		else if(chekval == 1) {
			msg.sentThSocket(soc, "1"); // not Updated
		}
		else {
			msg.sentThSocket(soc, "0");	// no Symbol like that
		}
		
		
		bflg = 1;
		
		return bflg;
	}
	
	// close given connection 
	public void closeClientCon(Socket soc) {
		
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		msg.sentThSocket(soc,"OKC"); // say to server accept connection close request
		
		try {
			in.close(); // close DataInputStream
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			soc.close(); // close socket
			System.out.println("Client disconnected");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// get user name from client (used in publisher subscriber process)
	public String getClientUname(Socket soc) {
		
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String un = msg.reserve(in, soc);
		
		return un;
	}

}
