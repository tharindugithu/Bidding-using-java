package clientGui;

// This is client logging interface
// Get user Name and Password

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.CMsg;
import client.TcpConn;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import java.net.Socket;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class ClientLogin extends JFrame {

	private JPanel contentPane;
	private JTextField usNametextField;
	private JTextField PwdtextField_1;
	
	private Socket rsSoc; // for socket
	private DataInputStream in = null;
	private CMsg cmsg = null;
	private int subPort;            // for subscriber port number 
	private TcpConn subCon = null;  // to create client side subscriber socket
	private String serIp;           // IP address

	public ClientLogin(Socket rSoc, int sPort, String seIP) {
		setTitle("Client Login");
		
		cmsg = new CMsg();
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
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(10, 38, 123, 24);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Password      :");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setBounds(10, 89, 123, 24);
		contentPane.add(lblNewLabel_1);
		
		usNametextField = new JTextField();
		usNametextField.setBounds(128, 40, 275, 20);
		contentPane.add(usNametextField);
		usNametextField.setColumns(10);
		
		PwdtextField_1 = new JTextField();
		PwdtextField_1.setBounds(128, 91, 275, 20);
		contentPane.add(PwdtextField_1);
		PwdtextField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("Log");
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setBackground(new Color(0, 255, 0));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				//set type as logging and check it is reserved = 1
				cmsg.sentThSocket(rsSoc,"1" ); // client server
				
				//check user name and password
				//if yes go else enter again
				if(chechUsePWd(usNametextField.getText(),PwdtextField_1.getText(),rsSoc) == 1){
					System.out.print("OK");	
					Socket subSoc =  createSubConn(subPort,serIp); // after successfully logging create publisher Subscriber Connection
					// create Action GUI, open and close this Interface
					ActionGUI actObj = new ActionGUI(rsSoc,usNametextField.getText(), subSoc);
					actObj.setVisible(true);
					setVisible(false);
					dispose();
				}
				else {
					//wrong password message
					JOptionPane.showMessageDialog(ClientLogin.this, "Wrong Password or UseName", "Error", JOptionPane.ERROR_MESSAGE);
					ClientLogin obj = new ClientLogin(rsSoc, subPort, serIp);
					obj.setVisible(true);
				}
				
				setVisible(false);
				dispose();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton.setBounds(294, 140, 109, 32);
		contentPane.add(btnNewButton);
		
		// new Registration button // open registration interface
		JButton btnNewButton_1 = new JButton("Create Account");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientRegisterGUI1 objClg = new ClientRegisterGUI1(rsSoc);
				objClg.setVisible(true);
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_1.setBounds(128, 140, 123, 32);
		contentPane.add(btnNewButton_1);
		
		//close button // send server to network close request and after reserving message from server close connection and close program
		JButton btnNewButton_2 = new JButton("Close");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmsg.sentThSocket(rsSoc,"0" ); // for close connection and close GUI
				if(cmsg.reserve(in,rsSoc).equals("0")) {
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
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_2.setBounds(10, 140, 97, 32);
		contentPane.add(btnNewButton_2);
	}
	
	//reserve result from sever and if yes return 1 else 0
	private int chechUsePWd(String unm, String upwd, Socket ucsoc) {
		
		String rs = "";
		int val = 0;
		
		System.out.println("Running");
		cmsg.sentThSocket(ucsoc,unm);
		cmsg.sentThSocket(ucsoc,upwd);
		
		rs = cmsg.reserve(in,ucsoc);
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
			subCon = new TcpConn(ip, sPort); // call to subCon set connection
		}
		catch(Exception ex) {
			System.err.println(ex);	
		}
		
		subs = subCon.getSocket(); // get created socket
		
		return subs;
	}

}
