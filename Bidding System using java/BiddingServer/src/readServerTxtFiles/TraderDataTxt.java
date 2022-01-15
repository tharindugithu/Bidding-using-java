package readServerTxtFiles;

import java.io.File;
import java.util.Scanner;

public class TraderDataTxt {

	private String fpath = "txtFiles\\useDetTxt.txt";
	
	// call to add new user or conform current user identity
	// if val3 = 1 confirm user - dealWithTraderTxt(user name, password, 1)
	// if val3 = 2 add new user - dealWithTraderTxt(user name, password, 1)
	public synchronized int dealWithTraderTxt(String val1, String val2, int val3) {
		
		int flag = 0;
		
		if(val3 == 1) {
			flag = readTraderTxtToUCmf(val1, val2); // check user name and password
		}
		else {
			FileHandle fho = new FileHandle();
			flag = fho.addNewToFile(val1, fpath); // add new user
		}
		
		return flag;
	}
	
	// check user name and password
	private int readTraderTxtToUCmf(String uName, String uPwd) {
		
		int rety = 0;
		
		String[] values = new String[4];
		
		try{
			File fru = new File(fpath);
			Scanner scn = new Scanner(fru);
			
			while(scn.hasNextLine()){
				String a = scn.nextLine();
				System.out.println(a);
				values = a.split(",");
				
				if(values[0].equals(uName)) {
					if(values[1].equals(uPwd)) {
						rety = 1;
						break;
					}
				}
				
			}
			
			scn.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		return rety;
		
	}
}
