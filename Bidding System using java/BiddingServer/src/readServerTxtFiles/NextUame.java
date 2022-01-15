package readServerTxtFiles;

//this class use to get last company ID
//store it in variable
//calculate next user name and return
//store last user in lastUserId.txt at end of the program

import java.io.File;
import java.util.Scanner;

//read useDetTxt.txt file and store last Id

public class NextUame {
	
	String ProDataF = "txtFiles\\useDetTxt.txt";
	FileHandle fobj = new FileHandle();
	
	private String ppin= "";
	private static int cpin = 0 ;
	
	//read last value in string file
	private String getPPin(String pdf){
		
		String[] values = new String[4];
		
		try{
			File fru = new File(pdf);
			Scanner scn = new Scanner(fru);
			
			String a = "";
			
			while(scn.hasNextLine()){
				a = scn.nextLine();
				System.out.println("p :" + a);
			}
			values = a.split(",");
			
			scn.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		ppin = values[0];
		
		System.out.println("Read next valye" + ppin);
		return ppin;
	}
	
	//store last user name
	public void getLastpin() {
		 cpin = Integer.parseInt(getPPin(ProDataF));
		 System.out.println("Pre pin Updated");
	}
	
	//Generate next user name and keep it for next calculation
	public synchronized String genPin(){
		cpin = cpin + 1;
		return String.format("%06d", cpin);
	}

}
