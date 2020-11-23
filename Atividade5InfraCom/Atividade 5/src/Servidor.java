import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.BitSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;

public class Servidor {

	private static JFrame servidor;
	private static JPanel contentPane;
	private static JScrollPane scrollPane;
	private static JTextPane textPane;
	static DatagramSocket recebendo;
	static ServerSocket tcpSocket;
	static int porta = 23001;
	static int qtdBytes;
	static int opcao;
	static String digiteAqui;
	static double media;
	static int contadorPacotes = 0;
	static long minimo = 999999999;
	static long maximo = 0;

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
		recebendo = new DatagramSocket(porta);
		new Thread(receptorTCP).start();
	}

	public static Runnable receptorTCP = new Runnable() {
		public void run() {
			try {
				Socket socketRecebimento = tcpSocket.accept();
				// InputStream is = socketRecebimento.getInputStream();
				// ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				// byte [] armazenador = new byte[1024];
				// int nRead;
				// while((nRead = is.read(armazenador,1,armazenador.length))!=-1) {
				// buffer.write(armazenador,1,nRead);
				// }
				// String mensagem = armazenador.toString().trim();

				InputStreamReader entrada = new InputStreamReader(socketRecebimento.getInputStream());
				BufferedReader le = new BufferedReader(entrada);
				String mensagem = le.readLine();
				mensagem = mensagem + "\n" + le.readLine() + "\n" + le.readLine() + "\n";
				qtdBytes = Integer.parseInt(le.readLine());
				mensagem = mensagem + Integer.toString(qtdBytes) + "\n";
				opcao = Integer.parseInt(le.readLine());
				digiteAqui = le.readLine();
				mensagem = mensagem + Integer.toString(opcao) + "\n" + digiteAqui;
				textPane.setText(mensagem.substring(1));
				System.out.print("panda123");
				new Thread(receptorUDP).start();
				DataOutputStream saida = new DataOutputStream(socketRecebimento.getOutputStream());
				saida.write("1".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	public static Runnable receptorUDP = new Runnable() { // Qtd pacotes
		public void run() {
			long anterior = -2;
			long tempoAnterior = -1;
			long total = 0;
			int contadorIntervalos = 0;
			if (opcao == 1) {
				for (int i = 0; i < Integer.parseInt(digiteAqui); i++) {
					byte[] armazenador = new byte[qtdBytes];
					DatagramPacket pacoteRecebido = new DatagramPacket(armazenador, armazenador.length);
					try {
						recebendo.receive(pacoteRecebido);
						long tempo = System.currentTimeMillis();
						contadorPacotes++;
						BitSet cabecalho = new BitSet(armazenador[0]);
						long value = 0L;
						for (int j = 3; j < cabecalho.length(); ++j) {
							value += cabecalho.get(j) ? (1L << j) : 0L;
						}

						if ((value == anterior + 1) || (value == 0 && anterior == 3)) {
							if (minimo > tempo - tempoAnterior) {
								minimo = tempo - tempoAnterior;
							}
							if (maximo < tempo - tempoAnterior) {
								maximo = tempo - tempoAnterior;
							}
							total += tempo - tempoAnterior;
							contadorIntervalos++;
							System.out.println(contadorIntervalos);
						}
						anterior = value;
						tempoAnterior = tempo;
						System.out.println("ChegueiAqui");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				media = (double) total / contadorIntervalos;
				System.out.println(minimo + "\n" + maximo + "\n" + media);
			}
		}
	};
}
