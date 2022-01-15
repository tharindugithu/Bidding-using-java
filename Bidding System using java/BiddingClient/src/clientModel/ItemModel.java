package clientModel;

//this class get input as Item list and create Model for create table

import java.util.List;

import javax.swing.table.AbstractTableModel;

import clientCore.Item;

public class ItemModel  extends AbstractTableModel{
	
	public static final int OBJECT_COL = -1;
	private static final int SYMBOL    = 0;
	private static final int PRICE     = 1;
	private static final int PROFIT    = 2;
	
	private String[] colName = {"Symbol", "Price", "Profit"};
	
	private List<Item> symWithD;
	
	public ItemModel(List<Item> theSymWithD) {
		symWithD = theSymWithD;
		System.out.println(theSymWithD);
	}
	
	@Override
	public int getColumnCount() {
		return colName.length;
	}

	@Override
	public int getRowCount() {
		return symWithD.size();
	}
	
	@Override
	public String getColumnName(int col) {
		return colName[col];
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		
		Item tmpItem = symWithD.get(row);
		
		switch (col) {
		case SYMBOL:
			return tmpItem.getSym();
		case PRICE:
			return tmpItem.getPrice();
		case PROFIT:
			return tmpItem.getProfit();
		case OBJECT_COL:
			return tmpItem;
		default :
			return tmpItem.getSym();
		}
		
	}
	
	@Override
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

}
