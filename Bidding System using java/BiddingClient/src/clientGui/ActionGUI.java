package clientGui;

/*
 * this is action interface and contain subscribed item details table, symbol list, section to give symbol and get details related that symbol,
 * 	bidding section 
 * Constructor get input as Socket and user name of the client
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.SubProcessRunnable;
import client.SubscribeList;
import clientCore.Item;
import clientModel.ItemModel;
import clientModel.SubscriberListModel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ActionGUI extends JFrame {

	private JPanel contentPane;
	private Socket cSoc, subSoc;
	private String uName;
	private SubscriberListModel submod = null;
	private ItemModel itmmodl = null;
	private JTextField symtextField;
	
	private SubscribeList sublst;
	private List<Item> itmLst = null;
	private List<String> symboles = null; // create list for symbols
	private JTable table3;
	private JTable table;
	private JTable table_1;
	private JTextField bidSymtextField;
	private JTextField pricetextField;
	
	private int guiSynCntrl; // avoid interruption when same time call to methods

	/**
	 * Create the frame.
	 */
	public ActionGUI(Socket soc, String uname, Socket sSoc) {
		setTitle("Client Action Interface");
		this.cSoc = soc;
		this.uName = uname;
		this.subSoc = sSoc;
		
		sublst = new SubscribeList(); // create object of SubscribeList class
		
		//createITMTablemodel(cSoc);
		
		createSymbolTable(cSoc); // reserve symbol list and create symbol model
			
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 664, 544);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(485, 28, 153, 436);
		contentPane.add(scrollPane_2);
		
		table_1 = new JTable();
		scrollPane_2.setViewportView(table_1);
		
		//display symbol table
		table_1.setModel(submod);
		
		// subscribed item
		JButton SubLstbtnNewButton = new JButton("Subscribe Item");
		SubLstbtnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		SubLstbtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(guiSynCntrl == 0) {
					guiSynCntrl = 1; // check access
					int row1 = table_1.getSelectedRow(); // get selected row number
					
					if(row1 < 0 ) { 
						JOptionPane.showMessageDialog(ActionGUI.this, "You must select a Symbol", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					String selSym1 = (String) table_1.getValueAt(row1, SubscriberListModel.ITEM); // get selected symbol
					
					int subSus = sublst.SubscribeItem(cSoc, uName, selSym1); // to subscribe 
					guiSynCntrl = 0; // set access for other process
					
					//refresh table with subscribed Item
					if(subSus == 0) { 
						
						createITMTablemodel(cSoc);
						
						JOptionPane.showMessageDialog(ActionGUI.this, "Subscribed Susseful : " + selSym1, "Subscribed",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					else {
						JOptionPane.showMessageDialog(ActionGUI.this, "Already Subscribed : " + selSym1, "Subscribed",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				
			}
		});
		SubLstbtnNewButton.setBounds(505, 471, 122, 23);
		contentPane.add(SubLstbtnNewButton);
		
		symtextField = new JTextField();
		symtextField.setBounds(98, 290, 122, 20);
		contentPane.add(symtextField);
		symtextField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Symbole :");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(31, 293, 68, 14);
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 318, 457, 52);
		contentPane.add(scrollPane_1);
		
		table = new JTable();
		scrollPane_1.setViewportView(table);
		
		// check details of given symbol
		JButton bidbtnNewButton = new JButton("Check Item Details");
		bidbtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(guiSynCntrl == 0) {
					if(!symtextField.getText().isBlank()) {
						guiSynCntrl = 1;
						List<Item> chkItmLst = new ArrayList<>(); // to store given item
						
						Item tmpItm = sublst.checkItem(cSoc, symtextField.getText()); // get given Item Details
						
						// create table if valid symbol
						if(!tmpItm.getSym().equals("Empty")) {
							chkItmLst.add(tmpItm);
							ItemModel chkItmmodl = new ItemModel(chkItmLst);
							table.setModel(chkItmmodl);
						}
						else {
							JOptionPane.showMessageDialog(ActionGUI.this, "Wrong Symbol : " + symtextField.getText(), "Wrong Symbol", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(ActionGUI.this, "Plese Enter Symbol", "Error", JOptionPane.ERROR_MESSAGE);
					}
					guiSynCntrl = 0;
				}
			}
		});
		bidbtnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		bidbtnNewButton.setBounds(261, 289, 176, 23);
		contentPane.add(bidbtnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 28, 457, 207);
		contentPane.add(scrollPane);
		
		table3 = new JTable();
		scrollPane.setViewportView(table3);
		
		//close
		JButton btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					sublst.closeConClient(cSoc);
				}
				catch(Exception ex) {
					System.out.println("Server Closed Unexpectadly");
				}
				
				setVisible(false);
				dispose();
				
			}
		});
		btnNewButton.setBounds(10, 471, 89, 23);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel_1 = new JLabel("Subsctibed Items");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(10, 3, 307, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Check Item Price And Profit");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_2.setBounds(10, 265, 272, 14);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Bid On Item");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_3.setBounds(10, 397, 176, 14);
		contentPane.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Symbole :");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_4.setBounds(31, 422, 68, 14);
		contentPane.add(lblNewLabel_4);
		
		bidSymtextField = new JTextField();
		bidSymtextField.setBounds(100, 416, 89, 20);
		contentPane.add(bidSymtextField);
		bidSymtextField.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Price :");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_5.setBounds(214, 422, 34, 14);
		contentPane.add(lblNewLabel_5);
		
		pricetextField = new JTextField();
		pricetextField.setBounds(261, 416, 86, 20);
		contentPane.add(pricetextField);
		pricetextField.setColumns(10);
		
		// do new bid
		JButton btnNewButton_1 = new JButton("Bid");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(guiSynCntrl == 0) {
					if(!(bidSymtextField.getText().isBlank() || pricetextField.getText().isBlank())) {
						guiSynCntrl = 1;
						int bidflg = sublst.bidOnItem(cSoc, uName, bidSymtextField.getText(), pricetextField.getText());
						
						if(bidflg == 0) {
							JOptionPane.showMessageDialog(ActionGUI.this, "Wrong Symbol : " + bidSymtextField.getText(), "Wrong Symbol", JOptionPane.ERROR_MESSAGE);
						}
						else if(bidflg == 1) {
							JOptionPane.showMessageDialog(ActionGUI.this, bidSymtextField.getText() + " Your Bid Is less then Current max bid", "Bidding Fail", JOptionPane.ERROR_MESSAGE);
						}
						else if(bidflg == 2) {
							JOptionPane.showMessageDialog(ActionGUI.this, bidSymtextField.getText() + " Bidding Time is end. can not bid more", "Bidding Time Out", JOptionPane.ERROR_MESSAGE);
						}
						else if(bidflg == 3) {
							JOptionPane.showMessageDialog(ActionGUI.this, "Bidding Success : " + bidSymtextField.getText(), "Success..", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(ActionGUI.this, "Plese Enter Symbol and Price", "Error", JOptionPane.ERROR_MESSAGE);
					}
					guiSynCntrl = 0;
				}
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_1.setBounds(378, 418, 89, 23);
		contentPane.add(btnNewButton_1);
		
		// start subscriber process
		SubProcessRunnable spro = new SubProcessRunnable(subSoc,uName,ActionGUI.this);
		Thread subTr = new Thread(spro);
		subTr.start();
		
	}
	
	private void createSymbolTable(Socket pSoc) {
		symboles = sublst.reqSbsList(pSoc); // get symbol list from server 
		Collections.sort(symboles); // sort symbol list
		submod = new SubscriberListModel(symboles); //create model of symbols for create table
		
	}
	
	public synchronized void createITMTablemodel(Socket pSoc) {
		itmLst = sublst.getAlrdySubItemWithDitail(pSoc, uName); // get already subscribed Symbol list with it's details from server
		itmmodl = new ItemModel(itmLst); // set model
		setSubTable(itmmodl);
	}
	
	// update table for price update
	public void createPUpdateTable(String sym, String npr) {
		List<Item> itLst = sublst.getPriceUpdateItemWithD(sym, npr);
		ItemModel tl = new ItemModel(itLst);
		setSubTable(tl);
	}
	
	// update table for profit update
	public void createProUpdateTable(String sym, String npro) {
		List<Item> itLst = sublst.getProUpdateItemWithD(sym, npro);
		ItemModel tl = new ItemModel(itLst);
		setSubTable(tl);
	}
	
	// set subscribed item table
	private synchronized void setSubTable(ItemModel itm) {
		table3.setModel(itm);
	}

}
