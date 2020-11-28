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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

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
	static int qtdBytesRecebidos = 0;
	static long minimo = 999999999;
	static long maximo = 0;
	static boolean emCurso = true;
	static int contadorPacotesEnviados = 0;
	static String qtdBytesEnviados;
	static long offsetValue;
	static long tempo;

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
		tcpSocket = new ServerSocket(porta);
		recebendo = new DatagramSocket(porta);
		new Thread(receptorTCP).start();
	}

	public static Runnable receptorTCP = new Runnable() {
		public void run() {
			try {
				Socket socketRecebimento = tcpSocket.accept();
				InputStreamReader entrada = new InputStreamReader(socketRecebimento.getInputStream());
				BufferedReader le = new BufferedReader(entrada);
				String mensagem = le.readLine();
				mensagem = mensagem + "\nPorta de destino: " + le.readLine() + "\nIP de destino: " + le.readLine()
						+ "\nTamanho da mensagem: ";
				qtdBytes = Integer.parseInt(le.readLine());
				mensagem = mensagem + Integer.toString(qtdBytes) + " (bytes)\nOpção: ";
				opcao = Integer.parseInt(le.readLine());
				digiteAqui = le.readLine();
				mensagem = mensagem + Integer.toString(opcao) + "\nCampo digite aqui: " + digiteAqui;
				textPane.setText("Porta de origem: " + mensagem.substring(1));
				new Thread(receptorUDP).start();
				DataOutputStream saida = new DataOutputStream(socketRecebimento.getOutputStream());
				saida.write("1\n".getBytes());
				InputStreamReader sinalTeste = new InputStreamReader(socketRecebimento.getInputStream());
				BufferedReader le2 = new BufferedReader(sinalTeste);
				String sinal = le2.readLine();
				Thread.currentThread().sleep(3000);
				emCurso = false;
				qtdBytesEnviados = le2.readLine();
				System.out.println("eaeee");
				String mensagem2 = le2.readLine();
				System.out.println("eaeee");
				Long tempoInicialEnvio = Long.parseLong(mensagem2);
				Long tempoEnvio = (tempo + offsetValue) - tempoInicialEnvio;
				System.out.println(qtdBytesEnviados + "\n" + tempoEnvio);
				double taxaTransferencia = (double) (Long.parseLong(qtdBytesEnviados) / tempoEnvio) / 1000L;
				System.out.println(taxaTransferencia);
				String mensagem3 = le2.readLine();
				contadorPacotesEnviados = Integer.parseInt(mensagem3);
				double perdaPacotes = (1 - (double) contadorPacotes / contadorPacotesEnviados) * 100;
				textPane.setText(textPane.getText() + "\n" + qtdBytesEnviados + "\n" + Integer.toString(qtdBytesRecebidos) + "\n"
								+ String.valueOf(taxaTransferencia) + "\n" + String.valueOf(perdaPacotes) + "%\n"
								+ Long.toString(minimo) + "\n" + Long.toString(maximo) + "\n" + String.valueOf(media)+"\n");
				saida.write(textPane.getText().getBytes());
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		}
	};

	public static Runnable receptorUDP = new Runnable() {
		public void run() {
			long anterior = -2;
			long tempoAnterior = -1;
			long total = 0;
			int contadorIntervalos = 0;
			while (emCurso) {
				byte[] armazenador = new byte[qtdBytes];
				DatagramPacket pacoteRecebido = new DatagramPacket(armazenador, armazenador.length);
				try {
					recebendo.receive(pacoteRecebido);
					tempo = System.currentTimeMillis();
					contadorPacotes++;
					byte[] auxCabeca = { armazenador[0] };
					BitSet cabecalho = BitSet.valueOf(auxCabeca);
					System.out.println(cabecalho);
					int value = 0;
					if (cabecalho.get(3)) {
						if (cabecalho.get(4)) {
							value = 3;
						} else {
							value = 1;
						}
					} else if (cabecalho.get(4)) {
						value = 2;
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

					}
					System.out.println(contadorIntervalos + " " + value);
					anterior = value;
					tempoAnterior = tempo;
					System.out.println("ChegueiAqui");
					media = (double) total / contadorIntervalos;
					System.out.println(minimo + "\n" + maximo + "\n" + media);
					qtdBytesRecebidos += qtdBytes;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	};
}
