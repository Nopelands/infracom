import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;

public class Servidor{

	private static JFrame servidor;
	private static JPanel contentPane;
	private static JScrollPane scrollPane;
	private static JTextPane textPane;
	static DatagramSocket recebendo;
	static ServerSocket tcpSocket;
	static int porta = 23001;
	
	public static void main(String[] args) throws IOException {
		servidor = new JFrame();
		servidor.setTitle("Servidor");
		servidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		servidor.setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		servidor.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 79, 414, 154);
		contentPane.add(scrollPane);
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		JLabel lblNewLabel = new JLabel("Resultados");
		lblNewLabel.setBounds(10, 54, 166, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Estat\u00EDsticas");
		lblNewLabel_1.setBounds(182, 11, 83, 14);
		contentPane.add(lblNewLabel_1);
		servidor.setVisible(true);
		tcpSocket = new ServerSocket(porta);
	}
	
	public static Runnable receptorTCP = new Runnable() {
		public void run() {
			try {
				Socket socketRecebimento = tcpSocket.accept();
				InputStreamReader entrada = new InputStreamReader(socketRecebimento.getInputStream());
				BufferedReader le = new BufferedReader(entrada);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	};
	
	public static Runnable receptorUDP = new Runnable() { //Qtd pacotes
		public void run() {
			
			
		}
	};
}
