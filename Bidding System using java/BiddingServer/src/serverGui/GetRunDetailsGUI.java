package serverGui;

//This is server starting class with interface
//get input as server client server port number and publisher subscriber port number

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import supMethod.SupMethods;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GetRunDetailsGUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private SupMethods supob = new SupMethods();
	private JTextField subPorttextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GetRunDetailsGUI frame = new GetRunDetailsGUI();
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
	public GetRunDetailsGUI() {
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 288, 175);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Client Server Port :");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(22, 22, 121, 21);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(153, 22, 94, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		// get input as port number and call to ServerTimeGUI
		JButton btnNewButton = new JButton("OK");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(supob.isInteger(textField.getText()) && supob.isInteger(subPorttextField.getText())) { // check empty text fields
					int port = Integer.parseInt(textField.getText()); // get client server port
					int subPort = Integer.parseInt(subPorttextField.getText()); // get publisher subscriber port number
					//close this interface and open ServerTimrtGUI
					setVisible(false);
					dispose();
					ServerTimeGUI stobj = new ServerTimeGUI(port, subPort);
					stobj.setVisible(true);
				}
				else {
					JOptionPane.showMessageDialog(GetRunDetailsGUI.this, "Enter Port Number Correctly", "Error Input", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton.setBounds(158, 102, 89, 23);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel_1 = new JLabel("Subscriber Port     :");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(22, 64, 121, 14);
		contentPane.add(lblNewLabel_1);
		
		subPorttextField = new JTextField();
		subPorttextField.setBounds(153, 61, 94, 20);
		contentPane.add(subPorttextField);
		subPorttextField.setColumns(10);
	}

}
