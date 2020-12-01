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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;
import java.util.BitSet;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class Cliente {

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
	static ServerSocket clienteTCP;
	static DatagramSocket usuario;
	static int myPorta;
	static int serverPorta;
	static InetAddress ip;
	static int opcao;
	static boolean emCurso = true;
	static int contadorPacotes = 0;
	static int qtdBytesEnviados = 0;
	static long offsetValue;
	static long tempoInicialEnvio;

	/**
	 * Launch the application.
	 * 
	 * @throws InterruptedException
	 * 
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
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
		textField_2.setBounds(90, 36, 119, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		lblNewLabel_3 = new JLabel("IP de destino");
		lblNewLabel_3.setBounds(10, 36, 70, 14);
		contentPane.add(lblNewLabel_3);

		textField_3 = new JTextField();
		textField_3.setBounds(426, 33, 84, 20);
		contentPane.add(textField_3);
		textField_3.setColumns(10);

		btnNewButton = new JButton("N\u00FAmero de pacotes");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opcao = 1;
			}
		});
		btnNewButton.setBackground(Color.LIGHT_GRAY);
		btnNewButton.setBounds(20, 89, 142, 23);
		contentPane.add(btnNewButton);

		btnNewButton_1 = new JButton("Total de bytes");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opcao = 2;
			}
		});
		btnNewButton_1.setBackground(Color.LIGHT_GRAY);
		btnNewButton_1.setBounds(196, 89, 114, 23);
		contentPane.add(btnNewButton_1);

		btnNewButton_2 = new JButton("Dura\u00E7\u00E3o do teste");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opcao = 3;
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
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(TCP).start();
			}
		});
		btnNewButton_3.setBackground(Color.LIGHT_GRAY);
		btnNewButton_3.setBounds(196, 175, 114, 23);
		contentPane.add(btnNewButton_3);

		lblNewLabel_5 = new JLabel("Escolha uma op\u00E7\u00E3o");
		lblNewLabel_5.setBounds(205, 64, 91, 14);
		contentPane.add(lblNewLabel_5);

		cliente.setVisible(true);

		try {
			String ntpServer = "a.st1.ntp.br";

			NTPUDPClient timeClient = new NTPUDPClient();
			InetAddress inetAddress = InetAddress.getByName(ntpServer);
			TimeInfo timeInfo = timeClient.getTime(inetAddress);
			timeInfo.computeDetails();
			offsetValue = timeInfo.getOffset();

		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// por enquanto, local host só pra testes

	}

	public static Runnable TCP = new Runnable() {
		public void run() {
			myPorta = Integer.parseInt(textField.getText());
			serverPorta = Integer.parseInt(textField_1.getText());
			try {
				clienteTCP = new ServerSocket(myPorta);
				usuario = new DatagramSocket(myPorta);
				ip = InetAddress.getByName(textField_2.getText());
				usuario.setSendBufferSize(50000000);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String aux = textField.getText() + "\n" + textField_1.getText() + "\n" + textField_2.getText() + "\n"
					+ textField_3.getText() + "\n" + Integer.toString(opcao) + "\n" + textField_4.getText() + "\n";
			byte[] cabecalho = new byte[aux.length() + 1];
			ByteBuffer auxiliar = ByteBuffer.wrap(cabecalho);
			BitSet protocolo = new BitSet(8);
			protocolo.set(0);
			auxiliar.put(protocolo.toByteArray());
			auxiliar.put(aux.getBytes());
			cabecalho = auxiliar.array();
			String mensagem = new String(cabecalho);
			Socket socket;
			try {
				socket = new Socket(ip, serverPorta);
				DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
				saida.write(mensagem.getBytes());
				InputStreamReader entrada = new InputStreamReader(socket.getInputStream());
				BufferedReader le = new BufferedReader(entrada);
				le.readLine();
				if (opcao == 1) {
					emissorNum();
				} else if (opcao == 2) {
					emissorBytes();
				} else {
					emissorTempo();
				}
				while (emCurso) {

				}
				String saidaAux = "1\n" + Integer.toString(qtdBytesEnviados) + "\n"
						+ String.valueOf(tempoInicialEnvio + offsetValue) + "\n" + Integer.toString(contadorPacotes)
						+ "\n";
				System.out.println(textField_3.getText() + "\n" + Integer.parseInt(textField_3.getText()) + "\n"
						+ qtdBytesEnviados + "\n" + Integer.toString(qtdBytesEnviados) + "\n"
						+ Integer.parseInt(Integer.toString(qtdBytesEnviados)));
				DataOutputStream sinalTeste = new DataOutputStream(socket.getOutputStream());
				sinalTeste.write(saidaAux.getBytes());
				String mensagemResposta = le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n" + 
						le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n" + 
						le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n";
				textPane.setText(mensagemResposta);
				System.out.print("kakaka");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	public static void emissorNum() throws IOException {
		String aux = textField_4.getText();
		int qtdPacotes = Integer.parseInt(aux);
		System.out.println(qtdPacotes);
		int valor = 0;

		for (int i = 0; i < qtdPacotes; i++) {
			byte[] dados = new byte[Integer.parseInt(textField_3.getText())];
			byte[] auxiliar = new byte[Integer.parseInt(textField_3.getText()) - 1];
			ByteBuffer buff = ByteBuffer.wrap(dados);
			BitSet cabecalho = new BitSet(8);
			cabecalho.set(0);
			switch (valor) {

			case 1:
				cabecalho.set(3);
				break;
			case 2:
				cabecalho.set(4);
				break;
			case 3:
				cabecalho.set(3);
				cabecalho.set(4);
				break;
			default:
			}
			System.out.println(valor + " " + cabecalho);
			valor++;
			if (valor == 4) {
				valor = 0;
			}

			buff.put(cabecalho.toByteArray());
			buff.put(auxiliar);
			dados = buff.array();
			DatagramPacket enviarDados = new DatagramPacket(dados, dados.length, ip, serverPorta);
			if (i == 0) {
				tempoInicialEnvio = System.currentTimeMillis();
			}
			usuario.send(enviarDados);
			contadorPacotes++;
			qtdBytesEnviados = qtdBytesEnviados + Integer.parseInt(textField_3.getText());
		}
		emCurso = false;

	};

	public static void emissorBytes() throws IOException {
		String aux = textField_4.getText();
		int qtdBytes = Integer.parseInt(aux);
		System.out.println(qtdBytes);
		int valor = 0;
		while (qtdBytes > 0) {
			byte[] dados = new byte[Integer.parseInt(textField_3.getText())];
			byte[] auxiliar = new byte[Integer.parseInt(textField_3.getText()) - 1];
			ByteBuffer buff = ByteBuffer.wrap(dados);
			BitSet cabecalho = new BitSet(8);
			cabecalho.set(0);
			switch (valor) {

			case 1:
				cabecalho.set(3);
				break;
			case 2:
				cabecalho.set(4);
				break;
			case 3:
				cabecalho.set(3);
				cabecalho.set(4);
				break;
			default:
			}
			System.out.println(valor + " " + cabecalho);
			valor++;
			if (valor == 4) {
				valor = 0;
			}

			buff.put(cabecalho.toByteArray());
			buff.put(auxiliar);
			dados = buff.array();
			DatagramPacket enviarDados = new DatagramPacket(dados, dados.length, ip, serverPorta);
			if (contadorPacotes == 0) {

				tempoInicialEnvio = System.currentTimeMillis();
			}
			usuario.send(enviarDados);
			qtdBytes -= Integer.parseInt(textField_3.getText());
			contadorPacotes++;
			qtdBytesEnviados = qtdBytesEnviados + Integer.parseInt(textField_3.getText());
		}
		emCurso = false;
	};

	public static void emissorTempo() throws IOException {
		String aux = textField_4.getText();
		int tempoTeste = Integer.parseInt(aux) * 1000;
		int valor = 0;
		long tempoInicial = System.currentTimeMillis();
		while (System.currentTimeMillis() - tempoInicial < tempoTeste) {
			byte[] dados = new byte[Integer.parseInt(textField_3.getText())];
			byte[] auxiliar = new byte[Integer.parseInt(textField_3.getText()) - 1];
			ByteBuffer buff = ByteBuffer.wrap(dados);
			BitSet cabecalho = new BitSet(8);
			cabecalho.set(0);
			switch (valor) {

			case 1:
				cabecalho.set(3);
				break;
			case 2:
				cabecalho.set(4);
				break;
			case 3:
				cabecalho.set(3);
				cabecalho.set(4);
				break;
			default:
			}
			System.out.println(valor + " " + cabecalho);
			valor++;
			if (valor == 4) {
				valor = 0;
			}

			buff.put(cabecalho.toByteArray());
			buff.put(auxiliar);
			dados = buff.array();
			DatagramPacket enviarDados = new DatagramPacket(dados, dados.length, ip, serverPorta);
			if (contadorPacotes == 0) {
				tempoInicialEnvio = System.currentTimeMillis();
			}
			usuario.send(enviarDados);
			contadorPacotes++;
			qtdBytesEnviados = qtdBytesEnviados + Integer.parseInt(textField_3.getText());
			System.out.println(contadorPacotes - 1 + "\n" + qtdBytesEnviados);
		}
		emCurso = false;
	};

}
