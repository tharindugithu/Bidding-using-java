package serverGui;

/* 
 * Enter server running time as Integer in minutes
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import server.ServerTimerRunnable;
import supMethod.SupMethods;

public class ServerTimeGUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private ServerTimeGUI stgui;
	private SupMethods supob = new SupMethods();

	public ServerTimeGUI(int port, int subPort) {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 367, 159);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton sStrtBtnNewButton = new JButton("Start");
		sStrtBtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String severStartCmd = textField.getText(); // get time
					
				// if time >0 and start command is correct,
				// call to timer and pass the thread,
				// open serverRunning GUI,
				// close this GUI
				if(supob.isInteger(severStartCmd)) {
					int sRunT = Integer.parseInt(severStartCmd);
					if(sRunT > 0) {
						//create timer thread
						ServerTimerRunnable sTRun = new ServerTimerRunnable(sRunT);
						Thread sTRunThtras = new Thread(sTRun);
						setVisible(false);
						dispose();
						//open next GUI
						ServerRunningGUI obj= new ServerRunningGUI(sTRunThtras, port, subPort, sTRun);
						obj.setVisible(true);
						
					}
					else {
						JOptionPane.showMessageDialog(stgui,
								"Enter Valid Time","Time",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				else {
					JOptionPane.showMessageDialog(stgui,
							"Enter time in minutes","Error Time",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		sStrtBtnNewButton.setBounds(201, 86, 140, 23);
		contentPane.add(sStrtBtnNewButton);
		
		textField = new JTextField();
		textField.setBounds(176, 51, 165, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Welcome to the server...!");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		lblNewLabel.setBounds(83, 11, 192, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server Run Time (min) :");
		lblNewLabel_1.setBounds(10, 54, 156, 14);
		contentPane.add(lblNewLabel_1);
	}

}
