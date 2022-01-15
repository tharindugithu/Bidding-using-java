package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import clientCore.Item;

public class SubscribeList {
	
	private DataInputStream in = null;
	CMsg cmsg = new CMsg();
	
	private Map<String, Float> symPrcMap   = new Hashtable<>(); //Symbol with price
	private Map<String, Integer> symProMap = new Hashtable<>(); //Symbol with profit
	
	/*
	 *  202  - client request from server to get Symbol list from server     (client send)
	 *  203  - say to client to ready to reserve list                        (Server send)
	 *  204  - say to client to symbol list is end                           (Server send)
	 *  
	 *  SUB  - client request subscribed list with profit and base price     (client send)
	 *  SUBY - server reply to say server accept "SUB" request ask user name (Server send)
	 *  NEMPTY - server sent to client to say, be ready to start reserve data(Server send)
	 *  EMPTY- server send to client to say, no subscribed items             (Server send)
	 *  SUBC - server send to client to say, process is completed            (Server send)
	 *  
	 *  SYMC - client request to check given symbol is valid and if valid get price and profit (Client send)
	 *  SYNCY- say to client, server accept "SYMC" and ask Symbol from client(Server send)
	 *  S1   - say to client, symbol is available and ready to reserve       (Server send)
	 *  S0   - say to client, symbol is not available                        (Server send)
	 *  SYNCE- sat to client, "SYMC" process is completed                    (Server send)
	 *  
	 *  BID  - client request, bid on item                                   (Client send)
	 *  BIDY - say to client, "BID" is accepted and ask symbol,user name,bid (Server send)
	 *  3    - say to client, bidding success and process end                (Server send)
	 *  2    - say to client, bidding time was ended. can not bid            (Server send)
	 *  0    - say to client, symbol wrong and process end                   (Server send)
	 *  1    - say to client, bid price less than current value              (Server send)
	 *  
	 *  PRFT - client request, subscribe symbol                              (Client send)
	 *  PRFTY- say to client, "PRFT" request accepted                        (Server send)
	 *  SUB0 - say to client. Subscribed fail                                (Server send)
	 *  SUB1 - say to client, subscribed success                             (Server send)
	 *  
	 *  Close- client request, close connection                              (Client  send)
	 *  OKC  - say to client, accept Close request from client               (Server  send)
	 */
	
	
	// this is used to get symbol list from the sever and return as List
	public List<String> reqSbsList(Socket soc) {
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cmsg.sentThSocket(soc, "202"); // 202 = to ask to get Symbol list from server 
		
		String strtFlag = cmsg.reserve(in, soc); // reserve server ack
		
		if(strtFlag.equals("203")){
			System.out.println("Start Reserve Subscribe List"); //reserve subscribe list has start
		}
		else {
			System.out.println("Starting Problem");
		}
		
		int size = Integer.parseInt(cmsg.reserve(in, soc)); //reserve subscribe list size
		
		List<String> sublist = new ArrayList<>(size);
		
		for(int i = 0; i < size; i++ ) {
			sublist.add(cmsg.reserve(in, soc)); //Reserve and add items to list
		}
		
		String stopFlag = cmsg.reserve(in, soc); // reserve end symbol of Symbol list sending
		
		if(stopFlag.equals("204")){
			System.out.println("Reserved Sucess Symbles"); //reserve subscribe list has start
		}
		else {
			System.out.println("Reserving Symbles has problem");
		}
		
		return sublist;
	}
	
	// subscribe Item
	public int SubscribeItem(Socket soc, String uname, String sym1) {
		
		int subflg = 3;
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		cmsg.sentThSocket(soc, "PRFT"); // client ask to subscribe Symbol
		
		String strtFlag = cmsg.reserve(in, soc); // reserve start statement
		
		if(strtFlag.equals("PRFTY")){
			System.out.println("Start Update ITEM"); //reserve subscribe list has start
		}
		
		String subFormat = uname + " " + sym1;
		
		cmsg.sentThSocket(soc, subFormat); // send user name with symbol
		
		String condiFlag = cmsg.reserve(in, soc); // 
		
		if(condiFlag.equals("SUB0")){
			System.out.println("Reserving Updated  Symbles Success"); //reserve subscribe list has start
			subflg = 0;
		}
		else {
			System.out.println("Alrady subscribed Symbles");
			subflg = 1;
		}
		
		return subflg;
	}
	
	// get already subscribed Symbol list and it's details and return that Item list
	public List<Item> getAlrdySubItemWithDitail(Socket soc, String uname){
		
		String symbol;
		List<Item> itmLst = new ArrayList<>();
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cmsg.sentThSocket(soc, "SUB"); // ask subscribed items with details from server
		
		String strtFlag = cmsg.reserve(in, soc); //reserve start statement
		
		if(strtFlag.equals("SUBY")){
			System.out.println("Start Reserve ITEM WITH DETAILS"); //reserve subscribe list has start
		}
		
		cmsg.sentThSocket(soc, uname); // sent user name
		
		String sizeFlg = cmsg.reserve(in, soc);
		
		// if subscribed list not empty create Item List
		if(sizeFlg.equals("NEMPTY")) {
			while(true) {
				symbol = cmsg.reserve(in, soc); //reserve Symbol or Stop flag
				if(symbol.equals("SUBC")) {
					break;
				}
				
				symPrcMap.put(symbol, Float.parseFloat(cmsg.reserve(in, soc))); // reserve price and create hash table with symbol
				symProMap.put(symbol, Integer.parseInt(cmsg.reserve(in, soc))); // reserve profit and create hash table with symbol
			}
			itmLst = createItemLst();
		}
		// else create List was Item("Empty", 0. 0)
		else {
			symbol = cmsg.reserve(in, soc); // reserve "SUBC" end command
			Item tempItem = new Item("Epmty", 0, 0);
			itmLst.add(tempItem);
		}
		
		return itmLst;
	}
	
	// create Item list using hash maps
	private List<Item> createItemLst(){
		
		List<Item> tmpItemLst = new ArrayList<>();
		
		Set<String> keys = symPrcMap.keySet();
		
		for(String key : keys) {
			Item tempItem = new Item(key, symPrcMap.get(key), symProMap.get(key));
			tmpItemLst.add(tempItem);
		}
		
		return tmpItemLst;
	}
	
	// check, get details from server about given item and return price and profit as a Item
	public Item checkItem(Socket soc,  String sym1) {
		
		Item tmpItm = null;
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cmsg.sentThSocket(soc, "SYMC"); // to ask to check about given symbol and get details
		
		cmsg.reserve(in, soc); // reserve accept message
		
		cmsg.sentThSocket(soc, sym1); // sent Symbol
		
		String symAvailable = cmsg.reserve(in, soc); //to reserve available // 0 - not Available, 1 - Available
		
		// if valid reserve price and profit and create item
		if(symAvailable.equals("S1")) {
			String sym = cmsg.reserve(in, soc);
			String prc = cmsg.reserve(in, soc);
			String pro = cmsg.reserve(in, soc);
			tmpItm = new Item(sym, Float.parseFloat(prc), Integer.parseInt(pro));
		}
		// else create Empty Item
		else {
			tmpItm = new Item("Empty", 0, 0);
		}
		
		// stop statement
		if(cmsg.reserve(in, soc).equals("SYNCE")) {
			System.out.println("Reserved Details End");
		}
		
		return tmpItm;
	}
	
	// bid on item and if success return 3, wrong symbol return 0, less than current max price 1, bidding time end return 2
	public int bidOnItem(Socket soc, String uname, String sym2, String price) {
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cmsg.sentThSocket(soc, "BID"); // ask to bid
		
		cmsg.reserve(in, soc); //reserve start statement
		
		String sStat = uname + " " + sym2 + " " + price;
		
		cmsg.sentThSocket(soc, sStat); // sent details ("userName symbol price)
		
		String bsflg = cmsg.reserve(in, soc); // reserve result
		
		return Integer.parseInt(bsflg);
	}
	
	// close client given connection
	public void closeConClient(Socket soc) {
		
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cmsg.sentThSocket(soc,"Close" ); // for close connection and close GUI
		
		if(cmsg.reserve(in,soc).equals("OKC")) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				soc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// update price update and get new list ( for publisher subscriber)
	public List<Item> getPriceUpdateItemWithD(String sym, String npr){
		
		List<Item> itmLst = new ArrayList<>();
		
		symPrcMap.put(sym, Float.parseFloat(npr));
		
		itmLst = createItemLst();
		
		return itmLst;
	}
	
	// update profit update and get new list ( for publisher subscriber)
	public List<Item> getProUpdateItemWithD(String sym, String npro){
		
		List<Item> itmLst = new ArrayList<>();
		
		symProMap.put(sym, Integer.parseInt(npro));
		
		itmLst = createItemLst();
		
		return itmLst;
	}

}
