package companyGUI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import company.ComSubProcessRunnable;
import company.ComSubscriberList;
import companyCore.Item;
import companySupMethod.CompanySupMethods;
import comModels.ComSubModels;
import comModels.ItemModel;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;

public class CompanyActionGUI extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTable table_1;
	private JTextField chPriProextField;
	private JTextField upProSymtextField;
	private JTextField protextField;
	private JTextField ssCodtextField;
	private JTable table_3;
	
	private Socket cSoc, subSoc;
	private String uName;
	private ItemModel itmmodl = null;
	private ComSubModels submod = null;
	private ComSubscriberList comSubL;
	private List<String> symboles = null; // create list for symbols
	private List<Item> itmlst = null;
	private CompanySupMethods csupIb = new CompanySupMethods();
	
	private int guiSynCntrl; // avoid interruption when same time call to methods

	/**
	 * Create the frame.
	 */
	public CompanyActionGUI(Socket soc, String uname, Socket sSoc) {
		
		this.cSoc = soc;
		this.uName = uname;
		this.subSoc = sSoc;
		
		comSubL = new ComSubscriberList(); // create object of ComSubscribeList class
		
		createSymbolTable(cSoc); // reserve symbol list and create symbol model
		
		setTitle("Company Action GUI");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 664, 581);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Subsctibed Items");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(10, 3, 307, 14);
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 28, 457, 207);
		contentPane.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JLabel lblNewLabel_1 = new JLabel("Check Item Price And Profit");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(10, 265, 272, 14);
		contentPane.add(lblNewLabel_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 318, 457, 52);
		contentPane.add(scrollPane_1);
		
		table_1 = new JTable();
		scrollPane_1.setViewportView(table_1);
		
		JLabel lblNewLabel_2 = new JLabel("Symbole :");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_2.setBounds(31, 293, 68, 14);
		contentPane.add(lblNewLabel_2);
		
		chPriProextField = new JTextField();
		chPriProextField.setBounds(98, 290, 122, 20);
		contentPane.add(chPriProextField);
		chPriProextField.setColumns(10);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(485, 28, 153, 466);
		contentPane.add(scrollPane_3);
		
		table_3 = new JTable();
		scrollPane_3.setViewportView(table_3);
		
		table_3.setModel(submod);
		
		// check details of given symbol
		JButton chkDtlsbtnNewButton = new JButton("Check Item Details");
		chkDtlsbtnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		chkDtlsbtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(guiSynCntrl == 0) {
					if(!chPriProextField.getText().isBlank()) {
						guiSynCntrl = 1;
						List<Item> chkItmLst = new ArrayList<>(); // to store given item
						
						Item tmpItm = comSubL.checkItem(cSoc, chPriProextField.getText()); // get given Item Details
						
						// create table if valid symbol
						if(!tmpItm.getSym().equals("Empty")) {
							chkItmLst.add(tmpItm);
							ItemModel chkItmmodl = new ItemModel(chkItmLst);
							table_1.setModel(chkItmmodl);
						}
						else {
							JOptionPane.showMessageDialog(CompanyActionGUI.this, "Wrong Symbol : " + chPriProextField.getText(), "Wrong Symbol", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(CompanyActionGUI.this, "Plese Enter Symbol", "Error", JOptionPane.ERROR_MESSAGE);
					}
					guiSynCntrl = 0;
				}
				
			}
		});
		chkDtlsbtnNewButton.setBounds(261, 289, 176, 23);
		contentPane.add(chkDtlsbtnNewButton);
		
		JLabel lblNewLabel_3 = new JLabel("Update Profit");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_3.setBounds(10, 397, 176, 14);
		contentPane.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Symbole :");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_4.setBounds(31, 422, 68, 14);
		contentPane.add(lblNewLabel_4);
		
		upProSymtextField = new JTextField();
		upProSymtextField.setBounds(128, 422, 122, 20);
		contentPane.add(upProSymtextField);
		upProSymtextField.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Profit :");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_5.setBounds(277, 422, 40, 14);
		contentPane.add(lblNewLabel_5);
		
		protextField = new JTextField();
		protextField.setBounds(336, 419, 118, 20);
		contentPane.add(protextField);
		protextField.setColumns(10);
		
		JLabel lblNewLabel_6 = new JLabel("Security Code :");
		lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_6.setBounds(31, 454, 104, 14);
		contentPane.add(lblNewLabel_6);
		
		ssCodtextField = new JTextField();
		ssCodtextField.setBounds(128, 451, 122, 20);
		contentPane.add(ssCodtextField);
		ssCodtextField.setColumns(10);
		
		//profit update
		JButton upProbtnNewButton_1 = new JButton("Update Profit");
		upProbtnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(guiSynCntrl == 0) { // check access
					
					// check blank text fields
					if(!(upProSymtextField.getText().isBlank() || protextField.getText().isBlank() || ssCodtextField.getText().isBlank())) {
						
						// check profit data type is Integer
						if(csupIb.isInteger(protextField.getText())) {
							guiSynCntrl = 1; // close access for other process
							
							// call to profit update
							int bidflg = comSubL.profitUpdate(cSoc, uName, upProSymtextField.getText(), protextField.getText(), ssCodtextField.getText());
							
							if(bidflg == 0) {
								JOptionPane.showMessageDialog(CompanyActionGUI.this, "Wrong Symbol : " + upProSymtextField.getText(), "Wrong Symbol", JOptionPane.ERROR_MESSAGE);
							}
							else if(bidflg == 2) {
								JOptionPane.showMessageDialog(CompanyActionGUI.this, "Wrong Security Code", "Security Code", JOptionPane.ERROR_MESSAGE);
							}
							else if(bidflg == 3) {
								JOptionPane.showMessageDialog(CompanyActionGUI.this, "Profit Updated : " +protextField.getText(), "Success..", JOptionPane.INFORMATION_MESSAGE);
							}
						}
						else {
							JOptionPane.showMessageDialog(CompanyActionGUI.this, "Profit shoud be Natural Number", "Error", JOptionPane.ERROR_MESSAGE);
						}	
					}
					else {
						JOptionPane.showMessageDialog(CompanyActionGUI.this, "Plese Enter Symbol and Price with Password", "Error", JOptionPane.ERROR_MESSAGE);
					}
					guiSynCntrl = 0; // give access to other buttons
					
				}
				
			}
		});
		upProbtnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		upProbtnNewButton_1.setBounds(298, 450, 156, 23);
		contentPane.add(upProbtnNewButton_1);
		
		// close 
		JButton closbtnNewButton = new JButton("Close");
		closbtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					comSubL.closeConCompany(cSoc); // close connection
				}
				catch(Exception ex) {
					System.out.println("Connection Lossed with Server");
				}
				setVisible(false);
				dispose();
			}
		});
		closbtnNewButton.setBounds(22, 508, 89, 23);
		contentPane.add(closbtnNewButton);
		
		// subscribed item
		JButton btnNewButton = new JButton("Subscribe Item");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(guiSynCntrl == 0) {
					guiSynCntrl = 1;
					int row1 = table_3.getSelectedRow(); // select row number
					
					if(row1 < 0 ) { 
						JOptionPane.showMessageDialog(CompanyActionGUI.this, "You must select a Symbol", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					String selSym1 = (String) table_3.getValueAt(row1,ComSubModels.ITEM); // get symbol
					
					int subSus = comSubL.SubscribeItem(cSoc, uName, selSym1); // call to subscribe
					guiSynCntrl = 0;
					
					 // refresh table with subscribed Item
					if(subSus == 0) {
						createITMTablemodel(cSoc);
						
						JOptionPane.showMessageDialog(CompanyActionGUI.this, "Subscribed Susseful : " + selSym1, "Subscribed",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					else {
						JOptionPane.showMessageDialog(CompanyActionGUI.this, "Already Subscribed : " + selSym1, "Subscribed",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton.setBounds(485, 508, 153, 23);
		contentPane.add(btnNewButton);
		
		// start subscriber process
		ComSubProcessRunnable cspro = new ComSubProcessRunnable(subSoc,uName,CompanyActionGUI.this);
		Thread csTr = new Thread(cspro);
		csTr.start();
	}
	
	private void createSymbolTable(Socket pSoc) {
		symboles = comSubL.reqSbsList(pSoc); // get symbol list from server 
		Collections.sort(symboles); // sort symbol list
		submod = new ComSubModels(symboles); //create model of symbols for create table
		
	}
	
	public synchronized void createITMTablemodel(Socket pSoc) {
		
		itmlst = comSubL.getAlrdySubItemWithDitail(pSoc, uName); // get already subscribed Symbol list with it's details from server
		itmmodl = new ItemModel(itmlst);
		setSubTable(itmmodl);
	}
	
	// update table for price update
	public void createPUpdateTable(String sym, String npr) {
		List<Item> itLst = comSubL.getPriceUpdateItemWithD(sym, npr);
		ItemModel tl = new ItemModel(itLst);
		setSubTable(tl);
	}
	
	// update table for profit update
	public void createProUpdateTable(String sym, String npro) {
		List<Item> itLst = comSubL.getProfitUpdateItemWithD(sym, npro);
		ItemModel tl = new ItemModel(itLst);
		setSubTable(tl);
	}
	
	// set subscribed item table
	private synchronized void setSubTable(ItemModel itm) {
		table.setModel(itm);
	}
}
