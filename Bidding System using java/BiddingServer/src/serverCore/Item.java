package serverCore;

public class Item {
	
	private String symble;
	private float price;
	private int profit;
	
	public Item(String theSym, float thePrice, int theProfit  ) {
		this.symble    = theSym;
		this.price     = thePrice;
		this.profit    = theProfit;
	}
	
	public String getSym() {
		return symble;
	}
	
	public float getPrice() {
		return price;
	}
	
	public int getProfit() {
		return profit;
	}

}
