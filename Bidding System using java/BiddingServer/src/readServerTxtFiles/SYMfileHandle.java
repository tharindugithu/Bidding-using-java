package readServerTxtFiles;

//add details to SYM.txt file

public class SYMfileHandle {
	
	private String fpath = "txtFiles\\sym.txt";
	
	public int updateSYM(String sym, String uNam, float preP, float newP, String t) {
		
		int flg = 0;
		
		FileHandle fho = new FileHandle();
		flg = fho.addNewToFile(String.format("%s,%s,%f,%f,%s", sym, uNam, preP, newP, t), fpath);
		
		return flg;
	}

}
