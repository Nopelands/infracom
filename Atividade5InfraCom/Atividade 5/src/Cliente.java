import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;
import java.util.BitSet;

public class Cliente{

	private static JFrame cliente;
	private static JPanel contentPane;
	private static JTextField textField;
	private static JTextField textField_1;
	private static JTextField textField_2;
	private static JTextField textField_3;
	private static JTextField textField_4;
	private static JLabel lblNewLabel;
	private static JLabel lblNewLabel_1;
	private static JLabel lblNewLabel_2;
	private static JLabel lblNewLabel_3;
	private static JButton btnNewButton;
	private static JButton btnNewButton_1;
	private static JButton btnNewButton_2;
	private static JScrollPane scrollPane;
	private static JTextPane textPane;
	private static JLabel lblNewLabel_4;
	private static JButton btnNewButton_3;
	private static JLabel lblNewLabel_5;
	static DatagramSocket usuario;
	static int portaEnvio = 23000;
	static InetAddress ip;
	
	
	/**
	 * Launch the application.
	 * @throws SocketException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws IOException {
		cliente = new JFrame();
		
		cliente.setTitle("Cliente");
		cliente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cliente.setBounds(100, 100, 536, 356);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(211, 211, 211));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		cliente.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblNewLabel = new JLabel("Porta de Origem");
		lblNewLabel.setBounds(10, 11, 84, 14);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(104, 8, 84, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		lblNewLabel_1 = new JLabel("Porta de Destino");
		lblNewLabel_1.setBounds(246, 11, 84, 14);
		contentPane.add(lblNewLabel_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(340, 8, 86, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		lblNewLabel_2 = new JLabel("Tamanho da mensagem (em bytes)");
		lblNewLabel_2.setBounds(246, 36, 168, 14);
		contentPane.add(lblNewLabel_2);
		
		textField_2 = new JTextField();
		textField_2.setBounds(424, 33, 86, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		lblNewLabel_3 = new JLabel("IP de destino");
		lblNewLabel_3.setBounds(10, 36, 70, 14);
		contentPane.add(lblNewLabel_3);
		
		textField_3 = new JTextField();
		textField_3.setBounds(92, 36, 114, 20);
		contentPane.add(textField_3);
		textField_3.setColumns(10);
		
		btnNewButton = new JButton("N\u00FAmero de pacotes");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnNewButton.setBackground(Color.LIGHT_GRAY);
		btnNewButton.setBounds(20, 89, 142, 23);
		contentPane.add(btnNewButton);
		
		btnNewButton_1 = new JButton("Total de bytes");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnNewButton_1.setBackground(Color.LIGHT_GRAY);
		btnNewButton_1.setBounds(196, 89, 114, 23);
		contentPane.add(btnNewButton_1);
		
		btnNewButton_2 = new JButton("Dura\u00E7\u00E3o do teste");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnNewButton_2.setBackground(Color.LIGHT_GRAY);
		btnNewButton_2.setBounds(373, 89, 123, 23);
		contentPane.add(btnNewButton_2);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(29, 206, 467, 85);
		contentPane.add(scrollPane);
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		textField_4 = new JTextField();
		textField_4.setBounds(162, 144, 190, 20);
		contentPane.add(textField_4);
		textField_4.setColumns(10);
		
		lblNewLabel_4 = new JLabel("Digite Aqui");
		lblNewLabel_4.setBounds(226, 123, 70, 14);
		contentPane.add(lblNewLabel_4);
		
		btnNewButton_3 = new JButton("Iniciar Teste");
		btnNewButton_3.setBackground(Color.LIGHT_GRAY);
		btnNewButton_3.setBounds(196, 175, 114, 23);
		contentPane.add(btnNewButton_3);
		
		lblNewLabel_5 = new JLabel("Escolha uma op\u00E7\u00E3o");
		lblNewLabel_5.setBounds(205, 64, 91, 14);
		contentPane.add(lblNewLabel_5);
		
		cliente.setVisible(true);
		usuario = new DatagramSocket(portaEnvio);
		ip = InetAddress.getByName("localhost");//por enquanto, local host s� pra testes
		
		
	}

	public static void emissor () throws IOException { //Num de pacotes
		String aux = textField_4.getText();
		int qtdPacotes = Integer.parseInt(aux);
		for(int i=0; i<qtdPacotes; i++) {
			byte[] dados = new byte[64];
			byte[] auxiliar= new byte[63];
			ByteBuffer buff = ByteBuffer.wrap(dados);
			BitSet cabecalho = new BitSet(8);
			cabecalho.set(2);
			buff.put(cabecalho.toByteArray());
			buff.put(auxiliar);			
			dados = buff.array();
			DatagramPacket enviarDados = new DatagramPacket(dados, dados.length);
			usuario.send(enviarDados);			
		}
		
	}
	/**
	 * Create the frame.
	 */
	/*public Cliente() {
		setTitle("Cliente");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 536, 356);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(211, 211, 211));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Porta de Origem");
		lblNewLabel.setBounds(10, 11, 84, 14);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(104, 8, 84, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Porta de Destino");
		lblNewLabel_1.setBounds(246, 11, 84, 14);
		contentPane.add(lblNewLabel_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(340, 8, 86, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Tamanho da mensagem (em bytes)");
		lblNewLabel_2.setBounds(246, 36, 168, 14);
		contentPane.add(lblNewLabel_2);
		
		textField_2 = new JTextField();
		textField_2.setBounds(424, 33, 86, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("IP de destino");
		lblNewLabel_3.setBounds(10, 36, 70, 14);
		contentPane.add(lblNewLabel_3);
		
		textField_3 = new JTextField();
		textField_3.setBounds(92, 36, 114, 20);
		contentPane.add(textField_3);
		textField_3.setColumns(10);
		
		JButton btnNewButton = new JButton("N\u00FAmero de pacotes");
		btnNewButton.setBackground(Color.LIGHT_GRAY);
		btnNewButton.setBounds(20, 89, 142, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Total de bytes");
		btnNewButton_1.setBackground(Color.LIGHT_GRAY);
		btnNewButton_1.setBounds(196, 89, 114, 23);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Dura\u00E7\u00E3o do teste");
		btnNewButton_2.setBackground(Color.LIGHT_GRAY);
		btnNewButton_2.setBounds(373, 89, 123, 23);
		contentPane.add(btnNewButton_2);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(29, 206, 467, 85);
		contentPane.add(scrollPane);
		
		JTextPane textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		textField_4 = new JTextField();
		textField_4.setBounds(162, 144, 190, 20);
		contentPane.add(textField_4);
		textField_4.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Digite Aqui");
		lblNewLabel_4.setBounds(226, 123, 70, 14);
		contentPane.add(lblNewLabel_4);
		
		JButton btnNewButton_3 = new JButton("Iniciar Teste");
		btnNewButton_3.setBackground(Color.LIGHT_GRAY);
		btnNewButton_3.setBounds(196, 175, 114, 23);
		contentPane.add(btnNewButton_3);
		
		JLabel lblNewLabel_5 = new JLabel("Escolha uma op\u00E7\u00E3o");
		lblNewLabel_5.setBounds(205, 64, 91, 14);
		contentPane.add(lblNewLabel_5);
	}*/
}
