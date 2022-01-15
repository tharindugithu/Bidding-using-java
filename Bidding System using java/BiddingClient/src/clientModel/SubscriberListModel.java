package clientModel;

//this class used to get Symbol list as string and return Model to create jTable

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SubscriberListModel extends AbstractTableModel{
	
	public static final int OBJECT_COL = -1;
	public static final int ITEM      = 0;
	
	private String[] colName = {"Company Symbol"};
	
	private List<String> symbles;
	
	public SubscriberListModel(List<String> theSymble) {
		symbles = theSymble;
	}
	
	@Override
	public int getColumnCount() {
		return colName.length;
	}

	@Override
	public int getRowCount() {
		return symbles.size();
	}

	@Override
	public String getColumnName(int col) {
		return colName[col];
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		
		String tempSymble = symbles.get(row);
		
		switch (col) {
		case ITEM: 
			return tempSymble;
		default:
			return "<Empty>";
		}
	}
	
	@Override
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

}
