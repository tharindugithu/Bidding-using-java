package companyGUI;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import company.ComConn;
import company.ComMsg;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class CompanyLogin extends JFrame {

	private JPanel contentPane;
	private JTextField uIdtextField;
	private JTextField PwdtextField;
	private JButton ClobtnNewButton;
	private JButton regbtnNewButton;
	private JButton logbtnNewButton;
	
	private Socket rsSoc; // for socket
	private DataInputStream in = null;
	private ComMsg commsg = null;
	private int subPort;            // for subscriber port number 
	private ComConn comcon = null;  // to create company side subscriber socket
	private String serIp;           // IP address


	/**
	 * Create the frame.
	 */
	public CompanyLogin(Socket rSoc, int sPort, String seIP) {
		setTitle("Company Login");
		
		commsg = new ComMsg();
		subPort = sPort;
		serIp   = seIP;
		rsSoc   = rSoc;
		
		// for reserving messages through socket
		try {
			in  = new DataInputStream(new BufferedInputStream(rsSoc.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 220);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("User ID          :");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(10, 38, 123, 24);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Password      :");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(10, 89, 123, 24);
		contentPane.add(lblNewLabel_1);
		
		uIdtextField = new JTextField();
		uIdtextField.setBounds(128, 40, 275, 20);
		contentPane.add(uIdtextField);
		uIdtextField.setColumns(10);
		
		PwdtextField = new JTextField();
		PwdtextField.setBounds(128, 91, 275, 20);
		contentPane.add(PwdtextField);
		PwdtextField.setColumns(10);
		
		//close button // send server to network close request and after reserving message from server close connection and close program
		ClobtnNewButton = new JButton("Close");
		ClobtnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		ClobtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				commsg.sentThSocket(rsSoc,"0" ); // for close connection and close GUI
				if(commsg.reserve(in,rsSoc).equals("0")) {
					setVisible(false);
					dispose();
					closeBuff();
					try {
						rsSoc.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		ClobtnNewButton.setBounds(10, 140, 97, 32);
		contentPane.add(ClobtnNewButton);
		
		// new Registration button // open registration interface
		regbtnNewButton = new JButton("Create Account");
		regbtnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		regbtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CompanyRegisterGUI crhob = new CompanyRegisterGUI(rsSoc);
				crhob.setVisible(true);
			}
		});
		regbtnNewButton.setBounds(128, 140, 123, 32);
		contentPane.add(regbtnNewButton);
		
		logbtnNewButton = new JButton("Log");
		logbtnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		logbtnNewButton.setForeground(new Color(0, 0, 0));
		logbtnNewButton.setBackground(new Color(0, 255, 0));
		logbtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//set type as logging and check it is reserved = 4
				commsg.sentThSocket(rsSoc,"4" ); // client server
				
				//check user name and password
				//if yes go else enter again
				if(chechUsePWd(uIdtextField.getText(),PwdtextField.getText(),rsSoc) == 1){
					System.out.print("OK");
					Socket subSoc =  createSubConn(subPort,serIp);// after successfully logging create publisher Subscriber Connection
					// create Action GUI, open and close this Interface
					CompanyActionGUI cagob = new CompanyActionGUI(rsSoc,uIdtextField.getText(), subSoc);
					cagob.setVisible(true);
					setVisible(false);
					dispose();
				}
				else {
					//wrong password message
					JOptionPane.showMessageDialog(CompanyLogin.this, "Wrong Password or UseName", "Error", JOptionPane.ERROR_MESSAGE);
					CompanyLogin clobj = new CompanyLogin(rsSoc, subPort, serIp);
					clobj.setVisible(true);
				}
				
				setVisible(false);
				dispose();
			}
		});
		logbtnNewButton.setBounds(294, 140, 109, 32);
		contentPane.add(logbtnNewButton);
	}
	
	//reserve result from sever and if yes return 1 else 0
	private int chechUsePWd(String unm, String upwd, Socket ucsoc) {
			
		String rs = "";
		int val = 0;
			
		System.out.println("Running");
		commsg.sentThSocket(ucsoc,unm);
		commsg.sentThSocket(ucsoc,upwd);
		
		rs = commsg.reserve(in,ucsoc);
		if(rs.equals("Successfully loged")) {
			val = 1;
		}
		
		return val;
	}
		
	// close DataInputStream
	private void closeBuff() {
		try {
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	// create connection through subscriber Port and return socket
	private Socket createSubConn(int sPort, String ip) {
		
		Socket subs = null;
		
		try {
			comcon = new ComConn(ip, sPort); // call to subCon set connection
		}
		catch(Exception ex) {
			System.err.println(ex);	
		}
		
		subs = comcon.getSocket(); // get created socket
		
		return subs;
	}
}
