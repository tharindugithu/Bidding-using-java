package clientGui;

// this is launching class of the client side program
// used to get server IP address, Subscriber Port and Client server Port

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.TcpConn;
import clientSupMethod.ClientSupMethods;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class ClientStartGUI extends JFrame {

	private String sIp; //server iP
	private int sPort; //server port
	private int subPort; //subscriber Port
	private TcpConn cCon = null; //to create client side socket
	private Socket rsSoc = null;
	private ClientSupMethods clsupMeO = new ClientSupMethods();

	private JPanel contentPane;
	private JTextField iptextField;
	private JTextField porttextField_1;
	private JTextField subPorttextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientStartGUI frame = new ClientStartGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientStartGUI() {
		setTitle("Client Start GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 385, 189);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server IP address :");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(10, 25, 135, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Client Server Port  :");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(10, 55, 135, 14);
		contentPane.add(lblNewLabel_1);
		
		iptextField = new JTextField();
		iptextField.setBounds(148, 22, 173, 20);
		contentPane.add(iptextField);
		iptextField.setColumns(10);
		
		porttextField_1 = new JTextField();
		porttextField_1.setBounds(148, 50, 173, 20);
		contentPane.add(porttextField_1);
		porttextField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("OK");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// check Empty Fields
				if(!(iptextField.getText().isBlank() || porttextField_1.getText().isBlank() || subPorttextField.getText().isBlank())) {
					//check, Port numbers are valid Integer
					if(clsupMeO.isInteger(porttextField_1.getText()) && clsupMeO.isInteger(subPorttextField.getText())) {
						
						sIp = iptextField.getText(); // get IP
						sPort = Integer.parseInt(porttextField_1.getText()); // get Client server Port
						subPort = Integer.parseInt(subPorttextField.getText()); // get Publisher Subscriber Port
					
						//create connection
						try {
							cCon = new TcpConn(sIp, sPort); // to get Connection to Client Server Port 
						}
						catch(Exception ex) {
							System.err.println(ex);
						}
						
						rsSoc = cCon.getSocket(); // get client server socket
						
						// Open ClientLogin interface and Close this Interface
						ClientLogin logObj = new ClientLogin(rsSoc, subPort,sIp);
						logObj.setVisible(true);
						setVisible(false);
						dispose();
					}
					else {
						JOptionPane.showMessageDialog(ClientStartGUI.this, "Enter Valid Port", "Port Error", JOptionPane.ERROR_MESSAGE); // Port Error Message
					}
				}
				else {
					JOptionPane.showMessageDialog(ClientStartGUI.this, "Enter IP and Port Number", "Error", JOptionPane.ERROR_MESSAGE); // Empty Field Error
				}
				
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton.setBounds(232, 111, 89, 23);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel_2 = new JLabel("Subscriber Port      :");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_2.setBounds(10, 84, 135, 14);
		contentPane.add(lblNewLabel_2);
		
		subPorttextField = new JTextField();
		subPorttextField.setBounds(148, 81, 173, 20);
		contentPane.add(subPorttextField);
		subPorttextField.setColumns(10);
	}
}
