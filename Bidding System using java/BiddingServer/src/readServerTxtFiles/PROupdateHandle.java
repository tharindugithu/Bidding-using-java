package readServerTxtFiles;

//add details to PROupdates.txt file

public class PROupdateHandle {
	
	private String fpath = "txtFiles\\proUpdates.txt";
	
	public int updatePro(String sym, String uNam, int preP, int newP, String t) {
		
		int flg = 0;
		
		FileHandle fho = new FileHandle();
		flg = fho.addNewToFile(String.format("%s,%s,%d,%d,%s", sym, uNam, preP, newP, t), fpath);
		
		return flg;
	}
	
	

}
