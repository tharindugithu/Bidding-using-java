package company;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import companyCore.Item;

public class ComSubscriberList {
	
	private DataInputStream in = null;
	ComMsg commsg = new ComMsg();
	
	private Map<String, Float> symPrcMap   = new Hashtable<>(); //Symbol with price
	private Map<String, Integer> symProMap = new Hashtable<>(); //Symbol with profit
	
	/*
	 *  CRS  - company request from server to get Symbol list from server                 (company send)
	 *  CRSY - say to client/company to ready to reserve list                             (Server  send)
	 *  CRSE - say to client/company to symbol list is end                                (Server  send)
	 *  
	 *  CRSI - company request, subscribe symbol                                          (Company send)
	 *  CRSIY- say to company, "RSIY" request accepted                                    (Server  send)
	 *  CSUB0- say to company, Subscribed fail                                            (Server  send)
	 *  CSUB1- say to company, subscribed success                                         (Server  send)
	 *  
	 *  CSYM - company request to check given symbol is valid and if valid get price and profit (Company send)
	 *  CSYMY- say to company, server accept "CSYM" and ask Symbol from company           (Server  send)
	 *  S1   - say to company, symbol is available and ready to reserve                   (Server  send)
	 *  S0   - say to company, symbol is not available                                    (Server  send)
	 *  CSYME- sat to company, "CSYM" process is completed                                (Server  send)
	 *  
	 *  CPUR - company request for update profit of Item                                  (Company send)
	 *  CPURY- say to company, server accept "CPUR" request and ask details for update    (Server  send)
	 *  0    - say to company, invalid symbol                                             (Server  send)
	 *  2    - say to company, wrong Security code                                        (Server  send)
	 *  3    - say to company, Success profit update                                      (Server  send)
	 *  
	 *  CSUB - company request subscribed list with profit and base price                 (company send)
	 *  CSUBY- server reply to say server accept "CSUB" request ask user name             (Server  send)
	 *  NEMPTY- server sent to company to say, be ready to start reserve data             (Server  send)
	 *  EMPTY- server send to company to say, no subscribed items                         (Server  send)
	 *  CSUBE- server send to company to say, process is completed                        (Server  send)
	 *  
	 *  Close- company request, for close request                                         (Company send)
	 *  OKC  - say to company, server accept connection close request                     (Server  send)
	 *  
	 */
	
	// this is used to get symbol list from the sever and return as List
	public List<String> reqSbsList(Socket soc) {
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		commsg.sentThSocket(soc, "CRS"); // CRS = to ask to get Symbol list from server 
		
		String strtFlag = commsg.reserve(in, soc); // reserve server ack
		
		if(strtFlag.equals("CRSY")){
			System.out.println("Start Reserve Subscribe List"); //reserve subscribe list has start
		}
		else {
			System.out.println("Starting Problem");
		}
		
		int size = Integer.parseInt(commsg.reserve(in, soc)); //reserve subscribe list size
		
		List<String> sublist = new ArrayList<>(size);
		
		for(int i = 0; i < size; i++ ) {
			sublist.add(commsg.reserve(in, soc)); //Reserve and add items to list
		}
		
		String stopFlag = commsg.reserve(in, soc); // reserve end symbol of Symbol list sending
		
		if(stopFlag.equals("CRSE")){
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
			
		commsg.sentThSocket(soc, "CRSI"); // company ask to subscribe Symbol
		
		String strtFlag = commsg.reserve(in, soc); // reserve start statement
		
		if(strtFlag.equals("CRSIY")){
			System.out.println("Start Update ITEM"); //reserve subscribe list has start
		}
		
		String subFormat = uname + " " + sym1;
		
		commsg.sentThSocket(soc, subFormat); // send user name with symbol
		
		String condiFlag = commsg.reserve(in, soc); // 
		
		if(condiFlag.equals("CSUB0")){
			System.out.println("Reserving Updated  Symbles Success"); //reserve subscribe list has start
			subflg = 0;
		}
		else {
			System.out.println("Alrady subscribed Symbles");
			subflg = 1;
		}
		
		return subflg;
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
		
		commsg.sentThSocket(soc, "CSYM"); // to ask to check about given symbol and get details
		
		commsg.reserve(in, soc); // reserve accept message
		
		commsg.sentThSocket(soc, sym1); // sent Symbol
		
		String symAvailable = commsg.reserve(in, soc); //to reserve available // 0 - not Available, 1 - Available
		
		// if valid reserve price and profit and create item
		if(symAvailable.equals("S1")) {
			String sym = commsg.reserve(in, soc);
			String prc = commsg.reserve(in, soc);
			String pro = commsg.reserve(in, soc);
			tmpItm = new Item(sym, Float.parseFloat(prc), Integer.parseInt(pro));
		}
		// else create Empty Item
		else {
			tmpItm = new Item("Empty", 0, 0);
		}
		
		// stop statement
		if(commsg.reserve(in, soc).equals("CSYME")) {
			System.out.println("Reserved Details End");
		}
		
		return tmpItm;
	}
	
	// profit update
	public int profitUpdate(Socket soc, String uname, String sym2, String profit, String spwd) {
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		commsg.sentThSocket(soc, "CPUR"); // ask to update profit
		
		commsg.reserve(in, soc); //reserve start statement
		
		String sStat = uname + " " + sym2 + " " + profit + " " + spwd;
		
		commsg.sentThSocket(soc, sStat); // sent details ("userName symbol price Security_Code")
		
		String bsflg = commsg.reserve(in, soc); // invalid symbol = 0; Wrong Security Code = 2; success profit update = 3
		
		return Integer.parseInt(bsflg);
		
	}
	
	// close client given connection
	public void closeConCompany(Socket soc) {
		
		try {
			in = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		commsg.sentThSocket(soc,"Close" ); // for close connection and close GUI
		
		if(commsg.reserve(in,soc).equals("OKC")) {
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
		
		commsg.sentThSocket(soc, "CSUB"); // ask subscribed items with details from server
		
		String strtFlag = commsg.reserve(in, soc); //reserve start statement
		
		if(strtFlag.equals("CSUBY")){
			System.out.println("Start Reserve ITEM WITH DETAILS"); //reserve subscribe list has start
		}
		
		commsg.sentThSocket(soc, uname); // sent user name
		
		String sizeFlg = commsg.reserve(in, soc);
		
		// if subscribed list not empty create Item List
		if(sizeFlg.equals("NEMPTY")) {
			while(true) {
				symbol = commsg.reserve(in, soc); //reserve Symbol or Stop flag
				if(symbol.equals("CSUBE")) {
					break;
				}
				
				symPrcMap.put(symbol, Float.parseFloat(commsg.reserve(in, soc))); // reserve price and create hash table with symbol
				symProMap.put(symbol, Integer.parseInt(commsg.reserve(in, soc))); // reserve profit and create hash table with symbol
			}
			itmLst = createItemLst();
		}
		// else create List was Item("Empty", 0. 0)
		else {
			symbol = commsg.reserve(in, soc); // reserve "CSUBE" end command
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
	
	// update price update and get new list ( for publisher subscriber)
	public List<Item> getPriceUpdateItemWithD(String sym, String npr){
		
		List<Item> itmLst = new ArrayList<>();
		
		symPrcMap.put(sym, Float.parseFloat(npr));
		
		itmLst = createItemLst();
		
		return itmLst;
	}
	
	// update profit update and get new list ( for publisher subscriber)
	public List<Item> getProfitUpdateItemWithD(String sym, String npro){
		
		List<Item> itmLst = new ArrayList<>();
		
		symProMap.put(sym, Integer.parseInt(npro));
		
		itmLst = createItemLst();
		
		return itmLst;
	}

}
