package msgHadle;

import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Msg {
	
	//sent message through given socket
	public synchronized void sentThSocket(Socket s, String str) {
		
		DataOutputStream out = null;
		
		try {
			out = new DataOutputStream(s.getOutputStream());
			out.writeUTF(str);
		}
		catch(IOException e) {
			System.err.println(e);
		}
		
	}
	
	// reserve and return string from given socket
	public synchronized String reserve(DataInputStream ins,Socket s) {
		
		String str = null;
		
		try {
			
			while(true) {
				str = ins.readUTF();
				
				System.out.println("Reserved: " + str);
				
				if(str.equals(null)) {
					sentThSocket(s, "Null Inpit. Please Input valied value");
					continue;
				}
				
				break;
			}
		}
		catch(IOException e) {
			System.err.println(e);
		}
		
		return str;
	}
	
	//check not null
	public int checkString(String cStr, String checker) {
		
		int chek = 1;
		
		if(cStr.equals(null)) {
			chek = -1;
		}
		
		return chek;
	}

}
