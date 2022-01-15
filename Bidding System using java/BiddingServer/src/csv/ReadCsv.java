package csv;

//read stock.csv file
//store in hash maps

import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.util.Map;
import java.util.Set;

import readServerTxtFiles.CompanySubItem;
import readServerTxtFiles.PROupdateHandle;
import readServerTxtFiles.PersonSubItem;
import readServerTxtFiles.SYMfileHandle;
import server.ServerSubHandler;
import server.ServerTimerRunnable;
import server.StoreSubConn;
import serverCore.Item;
import serverGui.ServerRunningGUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class ReadCsv {
	
	private String fpath = "doc\\stocks.csv";
	ServerTimerRunnable stro = new ServerTimerRunnable();
	private static boolean biddStat = true; // this true can bid else bidding time is end
	private static ServerRunningGUI srg;
	
	// to store and map CSV file data and mapping them with symbol
	// Hash table used to thread safety
	private static Map<String, String>  symPwdMap = new Hashtable<>(); //Symbol with security
	private static Map<String, Float> symBidMap   = new Hashtable<>(); //Symbol with price
	private static Map<String, Integer> symFunMap = new Hashtable<>(); //Symbol with profit
	
	// read stock.csv file and store data in hash maps
	public ReadCsv(ServerRunningGUI thesrg){
		
		srg = thesrg;
		
		int numCol = 4;
		String line;
		String[] value = new String[numCol];
		
		try {
			
			FileReader f = new FileReader(fpath);
			BufferedReader bf = new BufferedReader(f);
			
			bf.readLine(); //skip 1st row
			while((line = bf.readLine()) != null) {
				
				value = line.split(",");
				
				symPwdMap.put(value[0], value[2]);
				symBidMap.put(value[0], Float.parseFloat(value[1]));
				symFunMap.put(value[0], Integer.parseInt(value[3]));
				
			}
			
			bf.close();
			f.close();
			
			System.out.println("CSV read");
			
		}
		catch(Exception e) {
			System.err.println(e);
		}
		
	}
	
	// this constructor can used to access methods in this class
	public ReadCsv(int emty) {
		
	}
	
	// get symbol list from symPwdmap Hash map
	public List<String> getkeySymList(){
		
		List<String> keyList = new ArrayList<>(symPwdMap.keySet());
		
		return keyList;
	}
	
	// check given symbol is valid if yes return 1 else 0
	public int checkSymble(String symbleN) {
		
		int cSflg =  0;
		
		if(symPwdMap.containsKey(symbleN)) {
			cSflg =  1;
		}
		
		return cSflg;
	}
	
	//get details about given symbol list and create Item List and return it
	public List<Item> getItmWuthD(List<String> symlst){
		
		List<Item> symWithD = new ArrayList<>();
		
		for(int i = 0; i < symlst.size(); i++) {
			String sym = symlst.get(i);
			System.out.println(symBidMap.get(sym));
			Item tmpTtem = new Item(sym, symBidMap.get(sym), symFunMap.get(sym));
			symWithD.add(tmpTtem);
			System.out.println("OK");
		}
		System.out.println(symWithD);
		return symWithD;
	}
	
	// invalid symbol = 0; less bid = 1; bidding time end = 2; success bid = 3
	public synchronized int updateBidPrice(String uNam, String upSym, String price) {
		
		int flg = 0;
		float pri = Float.parseFloat(price);
		float prePri;
		
		if(biddStat) {
			if(checkSymble(upSym) == 1) {
				prePri = symBidMap.get(upSym);
				System.out.println("Old Value " + upSym + " " + prePri);
				if(prePri < pri) {
					symBidMap.put(upSym, pri);
					stro.addTimerMintifLmin(); // to ask add to  to server run time 
					String ct = getCurrentTime(); // get current time
					sentNotfiToSubscribers(upSym,price, 1); // send notification to subscribed users
					srg.setItemTable(); // server table update
					SYMfileHandle symfho = new SYMfileHandle();
					flg = symfho.updateSYM(upSym, uNam, prePri, pri, ct); // update file
					System.out.println("New Value " + upSym + " " + symBidMap.get(upSym));
				}
				else {
					flg = 1;
				}
			}
			else {
				System.out.println("Wrong Symble :" + upSym);
			}
		}
		else {
			flg = 2;
		}
		
		return flg;
	}
	
	// invalid symbol = 0; Wrong Security Code = 2; success profit update = 3 
	public synchronized int updateProfit(String uNam, String upSym, String profit, String secCod) {
		
		int flg = 0;
		int prePro;
		int pro = Integer.parseInt(profit);
		
		if(checkSymble(upSym) == 1) {
			if((symPwdMap.get(upSym)).equals(secCod)) {
				System.out.println("Security Code correct :" + upSym);
				prePro = symFunMap.get(upSym);
				System.out.println("Old Value " + upSym + " " + prePro);
				symFunMap.put(upSym, pro);
				String ct = getCurrentTime(); // get current time
				sentNotfiToSubscribers(upSym,profit, 2); // send notification to subscribed users
				srg.setItemTable(); // server table update
				PROupdateHandle profho = new PROupdateHandle();
				flg = profho.updatePro(upSym, uNam, prePro, pro, ct);
				System.out.println("New Profit Value " + upSym + " " + symFunMap.get(upSym));
			}
			else {
				flg = 2;
				System.out.println(" Wrong Security Code :" + upSym);
			}
		}
		else {
			System.out.println("Wrong Symble for Profit Update :" + upSym);
		}
		
		
		return flg;
		
	}
	
	// get current time
	public String getCurrentTime() {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	// send updated notifications through subscribed clients
	// noTy 1 = price Update     // noTy 2 = profit update
	// pr - profit or price
	private void sentNotfiToSubscribers(String sym, String pr, int noTy) {
		
		String upd = null; // send message
		
		List<String> NameLst = getNameListGivenSym(sym); // get given symbol subscribed user names list 
		
		List<ServerSubHandler> sshl = getServerSubList(); // get current available publisher subscriber ServerSubHandler list
		System.out.println("Subscriber Thread OK");
		
		if(noTy == 1) {
			upd = "PUPDATE " + sym + " " + pr;
		}
		else if(noTy == 2) {
			upd = "PRUPDATE " + sym + " " + pr; 
		}
		
		for(ServerSubHandler subW : sshl) {
			System.out.println("I m here");
			// send subscribed users who have access to send
			if(NameLst.contains(subW.getSubTUname()) && subW.getSendStatus()) {
				System.out.println("SEND " + upd);
				subW.subSen(upd); //send message
			}
		}
		
		System.out.println("UPDATE SEND TO ALL CLIENTS");
		
	}
	
	// get subscribed clients and companys for given symbol
	private List<String> getNameListGivenSym(String sym){
		
		List<String> userNLst = new ArrayList();
		
		List<String> uName = getUserList(sym); // get given symbol subscribed user names list 
		
		List<String> cUname = getComList(sym);
		
		userNLst.addAll(uName);
		userNLst.addAll(cUname);
		
		System.out.println("user name list ok");
		for(String cn :userNLst) {
			System.out.println("SUB UN " + cn);
		}
		
		return userNLst;
	}
	
	// get currently available ServerSubHandler list
	private List<ServerSubHandler> getServerSubList(){
		StoreSubConn sscr = new StoreSubConn(1);
		return sscr.getSubWorkers();
	}
	
	// get list of users who subscribed some item
	// ty = 1, client      // ty = 2, company
	private List<String> getUserList(String sym){
		PersonSubItem psio = new PersonSubItem();
		return psio.addOrRes("EMPTY", sym);
	}
	
	private List<String> getComList(String sym){
		CompanySubItem csio = new CompanySubItem();
		return csio.addOrResCom("EMPTY", sym);
	}
	
	// this method use to set bidding states as stopped
	public void setStopBiddingStates() {
		this.biddStat = false;
	}
	
	public boolean getBidStat() {
		return biddStat;
	}
	
	public List<Item> getAllList(){
		
		List<Item> symWithD = new ArrayList<>();
		
		List<String> symbols = new ArrayList<>(symBidMap.keySet());
		
		Collections.sort(symbols);
		
		for(String sym : symbols) {
			Item tmpTtem = new Item(sym, symBidMap.get(sym), symFunMap.get(sym));
			symWithD.add(tmpTtem);
		}
		
		
		return symWithD;
		
	}
	

}
