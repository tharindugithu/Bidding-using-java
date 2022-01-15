package company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ComMsg {
	
	// send message through given port
	public void sentThSocket(Socket s, String str) {
			
		DataOutputStream out = null;
			
		try {
			out = new DataOutputStream(s.getOutputStream());
			out.writeUTF(str);
		}
		catch(IOException e) {
			System.err.println(e);
		}
			
	}
	
	// reserve message from given port
	public String reserve(DataInputStream ins,Socket s) {
		
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

}
