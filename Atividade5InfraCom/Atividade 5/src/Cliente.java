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
				new Thread(TCP).start(); //inicialização da thread tcp
			}
		});
		btnNewButton_3.setBackground(Color.LIGHT_GRAY);
		btnNewButton_3.setBounds(196, 175, 114, 23);
		contentPane.add(btnNewButton_3);

		lblNewLabel_5 = new JLabel("Escolha uma op\u00E7\u00E3o");
		lblNewLabel_5.setBounds(205, 64, 91, 14);
		contentPane.add(lblNewLabel_5);

		cliente.setVisible(true);

		try { //Sincroniza com o servidor ntp, pegando o offset, utilizado para verificar a diferença em relação ao relógio do servidor
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
	
	}

	public static Runnable TCP = new Runnable() {
		public void run() {
			myPorta = Integer.parseInt(textField.getText());
			serverPorta = Integer.parseInt(textField_1.getText());
			try {
				clienteTCP = new ServerSocket(myPorta);	//inicialização dos sockets tcp e udp
				usuario = new DatagramSocket(myPorta);
				ip = InetAddress.getByName(textField_2.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String aux = textField.getText() + "\n" + textField_1.getText() + "\n" + textField_2.getText() + "\n"	//Preparação das opções do cliente a serem mandadas ao servidor
					+ textField_3.getText() + "\n" + Integer.toString(opcao) + "\n" + textField_4.getText() + "\n";
			byte[] cabecalho = new byte[aux.length() + 1];
			ByteBuffer auxiliar = ByteBuffer.wrap(cabecalho); //Setando o cabeçalho no tcp
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
				saida.write(mensagem.getBytes()); //Enviando as opções do cliente para o servidor
				InputStreamReader entrada = new InputStreamReader(socket.getInputStream());
				BufferedReader le = new BufferedReader(entrada);
				le.readLine(); //Aqui serve para quando o servidor envia uma mensagem de pronto para receber pacotes
				if (opcao == 1) {//Aqui executa o método de enviar pacotes udp de acordo com a opção escolhida pelo usuário
					emissorNum();
				} else if (opcao == 2) {
					emissorBytes();
				} else {
					emissorTempo();
				}
				while (emCurso) {//Permanece nesse while até que todos pacotes tenham sido enviados

				}
				String saidaAux = "1\n" + Integer.toString(qtdBytesEnviados) + "\n"//Prepara os dados sobre envio dos pacotes udp
						+ String.valueOf(tempoInicialEnvio + offsetValue) + "\n" + Integer.toString(contadorPacotes)
						+ "\n";
				DataOutputStream sinalTeste = new DataOutputStream(socket.getOutputStream());
				sinalTeste.write(saidaAux.getBytes()); //Aqui envia os dados sobre o envio dos pacotes udp
				String mensagemResposta = le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n" //Aqui armazena em uma string os resultados dos testes realizados no servidor
						+ le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n"
						+ le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n" + le.readLine() + "\n"
						+ le.readLine() + "\n";
				textPane.setText(mensagemResposta); //Printa os resultados do teste no textPane

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	public static void emissorNum() throws IOException {
		String aux = textField_4.getText(); //Pega a informação digitada pelo usuario para ser a base do teste de quantidade de pacotes
		int qtdPacotes = Integer.parseInt(aux);
		int valor = 0;

		for (int i = 0; i < qtdPacotes; i++) { 	//Esse "for" serve para enviar os pacotes até a quantidade determinada pelo usuário
			byte[] dados = new byte[Integer.parseInt(textField_3.getText())];// A partir daqui, está sendo preparado o cabeçalho de tamanho 1 byte(8 bits)
			byte[] auxiliar = new byte[Integer.parseInt(textField_3.getText()) - 1];
			ByteBuffer buff = ByteBuffer.wrap(dados);
			BitSet cabecalho = new BitSet(8);
			cabecalho.set(0);// Os três primeiros bits do cabeçalho são a versão do protocolo, nesse caso, versão 1, pois apenas o bit 0 é setado 
			switch (valor) {// O quarto e quinto bit são utilizados para número de sequência de pacote, utilizado para cálculo de jitter, nesse switch eles são setados de acordo com o valor

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
			valor++;
			if (valor == 4) {
				valor = 0;
			}

			buff.put(cabecalho.toByteArray());// Aqui os bits do cabeçalho juntam-se com os dos dados
			buff.put(auxiliar);
			dados = buff.array();
			DatagramPacket enviarDados = new DatagramPacket(dados, dados.length, ip, serverPorta);
			if (i == 0) {
				tempoInicialEnvio = System.currentTimeMillis(); //Aqui salva o tempo inicial, para cálculo de tempo
			}
			usuario.send(enviarDados);//Aqui envia os dados
			contadorPacotes++;
			qtdBytesEnviados = qtdBytesEnviados + Integer.parseInt(textField_3.getText());
		}
		emCurso = false; //Quando o envio acaba, a variável que diz que está sendo enviado pacotes é setada para false

	};

	public static void emissorBytes() throws IOException {
		String aux = textField_4.getText(); //Pega a informação digitada pelo usuario para ser a base do teste de número de bytes
		int qtdBytes = Integer.parseInt(aux);
		int valor = 0;
		while (qtdBytes > 0) { //Continua nesse while até que a quantidade de bytes enviados seja maior ou igual que a determinada
			byte[] dados = new byte[Integer.parseInt(textField_3.getText())];
			byte[] auxiliar = new byte[Integer.parseInt(textField_3.getText()) - 1];
			ByteBuffer buff = ByteBuffer.wrap(dados);
			BitSet cabecalho = new BitSet(8);
			cabecalho.set(0); //Aqui seta o cabeçalho
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
			valor++;
			if (valor == 4) {
				valor = 0;
			}

			buff.put(cabecalho.toByteArray());
			buff.put(auxiliar);
			dados = buff.array();
			DatagramPacket enviarDados = new DatagramPacket(dados, dados.length, ip, serverPorta);
			if (contadorPacotes == 0) {

				tempoInicialEnvio = System.currentTimeMillis(); //Aqui salva o tempo inicial
			}
			usuario.send(enviarDados); //Aqui envia o pacote
			qtdBytes -= Integer.parseInt(textField_3.getText());
			contadorPacotes++;
			qtdBytesEnviados = qtdBytesEnviados + Integer.parseInt(textField_3.getText());
		}
		emCurso = false; //Quando acaba o teste, seta a variável que diz que os pacotes estão sendo enviados para false
	};

	public static void emissorTempo() throws IOException {
		String aux = textField_4.getText(); //Pega a informação digitada pelo usuario para ser a base do teste de duração do teste
		int tempoTeste = Integer.parseInt(aux) * 1000;
		int valor = 0;
		long tempoInicial = System.currentTimeMillis(); //Aqui salva o tempo inicial, de início do teste
		while (System.currentTimeMillis() - tempoInicial < tempoTeste) { //Enquanto o tempo de duração do teste for maior que a variação do tempo de verificação e o tempo inicial, continua enviando pacotes
			byte[] dados = new byte[Integer.parseInt(textField_3.getText())];
			byte[] auxiliar = new byte[Integer.parseInt(textField_3.getText()) - 1];
			ByteBuffer buff = ByteBuffer.wrap(dados);
			BitSet cabecalho = new BitSet(8); //Aqui começa a ser setado o cabeçalho
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
			valor++;
			if (valor == 4) {
				valor = 0;
			}

			buff.put(cabecalho.toByteArray());
			buff.put(auxiliar);
			dados = buff.array();
			DatagramPacket enviarDados = new DatagramPacket(dados, dados.length, ip, serverPorta);
			if (contadorPacotes == 0) {
				tempoInicialEnvio = System.currentTimeMillis(); //Aqui seta o tempo inicial de envio
			}
			usuario.send(enviarDados); //Aqui envia o pacote
			contadorPacotes++;
			qtdBytesEnviados = qtdBytesEnviados + Integer.parseInt(textField_3.getText());
		}
		emCurso = false; //Quando acaba o teste, seta a variável que diz que os pacotes estão sendo enviados para false
	};

}
