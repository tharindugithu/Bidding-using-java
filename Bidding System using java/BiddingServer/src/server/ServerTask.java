package server;

//check user identity or create new user

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import msgHadle.Msg;
import readServerTxtFiles.TraderDataTxt;
import readServerTxtFiles.CompanyDataTxt;
import readServerTxtFiles.NextCompanyUname;
import readServerTxtFiles.NextUame;

public class ServerTask {
	
	private NextUame nUmOb = new NextUame(); //get next user name
	private NextCompanyUname ncun = new NextCompanyUname();
	private DataInputStream in = null;
	private String uName = null;
	
	//if client log success return 1 else 0 // if company log success return 2 else 0
	public int uselogingtyp(Socket s){
		Msg msg = new Msg();
		int typ = 10, sockflag = 10;
		String rs = null;
		
		// create to reserve data from client
		try {
			in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true) {
			
			//reserve type // 1 - client logging // 0 - close // 2 - client registration // 4 - company logging // 5 - company registration
			rs = msg.reserve(in,s);
			typ = Integer.parseInt(rs);
			
			// current user logging
			if(typ == 1) {
				
				//Check user authentication
				if(confirmCurrentUser(s) == 1) {
					msg.sentThSocket( s, "Successfully loged");
					sockflag = 1;
					break;
				}
					
				msg.sentThSocket(s,"User Name or Password is wrong");
				continue;
			}
			// current company logging
			else if(typ == 4) {
				if(confirmCurrentCompany(s) == 1) {
					msg.sentThSocket( s, "Successfully loged");
					sockflag = 2;
					break;
				}
				
				msg.sentThSocket(s,"User Name or Password is wrong");
				continue;
			}
			//close connection
			else if(typ == 0) {
				msg.sentThSocket( s, "0"); //close socket
				sockflag = 0;
				break;
			}
			
			//new user
			else if(typ == 2){
				
				if(addNewUser(s) == 3) {
					msg.sentThSocket( s, "3"); //success
					continue; // after go to logging
				}
				
				msg.sentThSocket(s,"2"); //error
			}
			else if(typ == 5) {
				if(addNewCompany(s) == 3) {
					msg.sentThSocket( s, "3"); //success
					continue; // after go to logging
				}
				
				msg.sentThSocket(s,"2"); //error
				
			}
			
		}
		
		return sockflag;
	}
	
	//confirm current user
	private int confirmCurrentUser(Socket cs) {
		
		System.out.println("Running oo");
		
		String uNam = getUserName(cs);
		String uPwd = getUserPwd(cs);
		
		TraderDataTxt trobj = new TraderDataTxt();
		int syn = trobj.dealWithTraderTxt(uNam, uPwd, 1);
		
		if(syn == 1) {
			this.uName = uNam;
		}
		
		return syn;
	}
	
	private int confirmCurrentCompany(Socket cs) {
		
		System.out.println("Running coo");
		
		String uNam = getUserName(cs);
		String uPwd = getUserPwd(cs);
		
		CompanyDataTxt coobj = new CompanyDataTxt();
		int syn = coobj.dealWithCompanyTxt(uNam, uPwd, 1);
		
		if(syn == 1) {
			this.uName = uNam;
		}
		
		return syn;
	}
	
	//add new user to 
	private int addNewUser(Socket cs) {
		
		int flag = 2;
		
		String newUseName = nUmOb.genPin(); // get next available user ID
		System.out.println(newUseName);
		seeUserName(cs ,newUseName );
		String newUser = getUserDetails(cs);
		System.out.println(newUser);
		
		// if success return 3 else 2
		while(true) {
			flag = addUserToFile(newUser);
			if(flag == 3) {
				break;
			}
		}
		
		return flag;
	}
	
	// add new Company
	private int addNewCompany(Socket cs) {
		
		int flag = 2;
		
		String newUseName = ncun.genComPin(); // get next available user ID
		System.out.println(newUseName);
		seeUserName(cs ,newUseName );
		String newUser = getUserDetails(cs);
		System.out.println(newUser);
		
		// if success return 3 else 2
		while(true) {
			flag = addCompanyToFile(newUser);
			if(flag == 3) {
				break;
			}
		}
		
		return flag;
	}
	
	//get use name from client
	private String getUserName(Socket cs) {
		Msg msg = new Msg();
		String uN = msg.reserve(in,cs);
		return uN;
	}
	
	//get password from client
	private String getUserPwd(Socket cs) {
		Msg msg = new Msg();
		String uP = msg.reserve(in,cs);
		return uP;
	}
	
	private String getUserDetails(Socket cs) {
		Msg msg = new Msg();
		String uN = msg.reserve(in,cs);
		return uN;
	}
	
	private void seeUserName(Socket cs, String msgusNm) {
		Msg msg = new Msg();
		msg.sentThSocket(cs, msgusNm);
		
	}
	
	// add new client to file
	private int addUserToFile(String useD) {
		
		TraderDataTxt trUobj = new TraderDataTxt();
		int stat = trUobj.dealWithTraderTxt(useD, "", 2);
		
		return stat;
	}
	
	// add new company to file
	private int addCompanyToFile(String useD) {
		
		CompanyDataTxt coUobj = new CompanyDataTxt();
		int stat = coUobj.dealWithCompanyTxt(useD, "", 2);
		
		return stat;
	}
	
	// get use name
	public String getUname() {
		return uName;
	}

}
