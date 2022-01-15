package serverGui;

/* this is main action interface for Server
 * start timer, client server connection, start publisher subscriber connection
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import csv.ReadCsv;
import server.ServerConnRunnable;
import server.ServerSubConnRunnable;
import server.ServerTimerRunnable;
import serverCore.Item;
import serverModel.ItemModels;
import readServerTxtFiles.NextCompanyUname;
import readServerTxtFiles.NextUame;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ServerRunningGUI extends JFrame {

	private JPanel contentPane;
	private ReadCsv res; // for csv
	private NextUame NeUObj = new NextUame(); 
	private NextCompanyUname cmobj = new NextCompanyUname();
	private Thread sConThread, subConThread, timerThred;
	private int port = 0; //port number to connect server
	private int subPort = 0; // port number for subscribe server
	private ServerTimerRunnable stronob;
	private JTable table;
	private List<Item> itmLst = null;
	private ItemModels itmmdls = null;
	
	public ServerRunningGUI(Thread sTThred, int sPort, int suPort, ServerTimerRunnable srgob) {
		setTitle("Server");
		
		port = sPort;
		subPort = suPort;
		stronob = srgob;
		
		res = new ReadCsv(ServerRunningGUI.this); // read .csv and load
		NeUObj.getLastpin(); // get last user name
		cmobj.getComLastpin(); // get last company user name
		
		timerThred = sTThred; // assign timer thread
		
		//create server sockets and waiting for client requests
		ServerConnRunnable sConR = new ServerConnRunnable(port);
		ServerSubConnRunnable subConR = new ServerSubConnRunnable(subPort);
		sConThread = new Thread(sConR); // client server Thread
		subConThread = new Thread(subConR); // subscriber Thread
		
		timerThred.start(); // start timer
		sConThread.start(); // run connection thread
		subConThread.start(); // run subscriber Thread
		
		//set Table model
		itmLst = res.getAllList();
		itmmdls = new ItemModels(itmLst);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(21, 11, 392, 239);
		contentPane.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		// create table
		table.setModel(itmmdls);
	}
	
	// set Item Table
	public void setItemTable() {
		itmLst = res.getAllList();
		itmmdls = new ItemModels(itmLst);
		table.setModel(itmmdls);
	}
}
