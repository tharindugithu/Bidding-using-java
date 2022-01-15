package readServerTxtFiles;

// this contain file handling methods

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandle {
	
	public void CreFile(File fp){
		try {
			if(fp.createNewFile()){
				System.out.println("File Created "+fp.getName());
			}
			else{
				System.out.println("File Exist");
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
	// add string to given file
	public int addNewToFile(String udats, String fpath) {
		
		int stat = 2;
		
		try {
			FileWriter fwu = new FileWriter(fpath,true);
			fwu.write(udats);
			fwu.write(System.getProperty( "line.separator" ));
			stat = 3;
			fwu.close();
		}
		catch(IOException e) {
			System.err.println(e);
		}
		
		return stat;
	}

}
