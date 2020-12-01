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
		tcpSocket = new ServerSocket(porta); //inicialização do socket tcp
		recebendo = new DatagramSocket(porta);//inicialização do socket udp
		new Thread(receptorTCP).start(); //inicialização da thread para receber os pacotes tcp
	}

	public static Runnable receptorTCP = new Runnable() {
		public void run() {
			try {
				Socket socketRecebimento = tcpSocket.accept(); //aceita a conexão tcp
				InputStreamReader entrada = new InputStreamReader(socketRecebimento.getInputStream());
				BufferedReader le = new BufferedReader(entrada);
				String mensagem = le.readLine(); //A partir daqui recebe as informações iniciais do cliente e prepara o programa servidor para receber dados udp
				mensagem = mensagem + "\nPorta de destino: " + le.readLine() + "\nIP de destino: " + le.readLine()
						+ "\nTamanho da mensagem: ";
				qtdBytes = Integer.parseInt(le.readLine());
				mensagem = mensagem + Integer.toString(qtdBytes) + " bytes\nOpção: ";
				opcao = Integer.parseInt(le.readLine());
				String opcaoEscolhida;

				digiteAqui = le.readLine();
				if (opcao == 1) {
					opcaoEscolhida = "Número de pacotes";
					mensagem = mensagem + opcaoEscolhida + " = " + digiteAqui + " pacotes";
				} else if (opcao == 2) {
					opcaoEscolhida = "Total de bytes";
					mensagem = mensagem + opcaoEscolhida + " = " + digiteAqui + " bytes";
				} else {
					opcaoEscolhida = "Duração do teste";
					mensagem = mensagem + opcaoEscolhida + " = " + digiteAqui + " segundos";
				}
				new Thread(receptorUDP).start(); //Após o programa servidor ser preparado para receber os pacotes pelo udp, inicializa a thread udp
				DataOutputStream saida = new DataOutputStream(socketRecebimento.getOutputStream());
				saida.write("1\n".getBytes()); //Aqui avisa ao programa cliente que o servidor está pronto para receber os pacotes udp
				InputStreamReader sinalTeste = new InputStreamReader(socketRecebimento.getInputStream());
				BufferedReader le2 = new BufferedReader(sinalTeste);
				String sinal = le2.readLine(); //Essa string serve para armazenar um sinal quando o cliente acabar de enviar os pacotes udp
				Thread.currentThread().sleep(3000); //Após o sinal, espera-se três segundos, para caso o pacote que enviou o sinal chegue antes de pacotes udps no meio do caminho
				emCurso = false; //Seta o sinal de cliente enviando dados para false
				qtdBytesEnviados = le2.readLine();
				String mensagem2 = le2.readLine();
				Long tempoInicialEnvio = Long.parseLong(mensagem2);
				Long tempoEnvio = (tempo + offsetValue) - tempoInicialEnvio;
				double taxaTransferencia = ((double) Long.parseLong(qtdBytesEnviados) / tempoEnvio) * 1000; //Calculo a taxa de transferência
				String mensagem3 = le2.readLine();
				contadorPacotesEnviados = Integer.parseInt(mensagem3);
				double perdaPacotes = (1 - (double) contadorPacotes / contadorPacotesEnviados) * 100; //Calculo da perda de pacotes
				textPane.setText("Porta de origem: " + mensagem.substring(1));
				String mensagemTaxa;
				if (taxaTransferencia > 1000000) {
					taxaTransferencia = taxaTransferencia / 1000000;
					mensagemTaxa = " MB/s\nPorcentagem de perda de pacotes: ";
				} else if (taxaTransferencia > 1000) {
					taxaTransferencia = taxaTransferencia / 1000;
					mensagemTaxa = " KB/s\nPorcentagem de perda de pacotes: ";
				} else {
					mensagemTaxa = " Bytes/s\nPorcentagem de perda de pacotes: ";
				}
				textPane.setText(textPane.getText() + "\nQuantidade de bytes enviados: " + qtdBytesEnviados //Printa no textPane o resultado dos testes
						+ " bytes\nQuantidade de bytes recebidos: " + Integer.toString(qtdBytesRecebidos)
						+ " bytes\nTaxa de transferência: " + String.format("%.3f", taxaTransferencia) + mensagemTaxa
						+ String.format("%.3f", perdaPacotes) + "%\nJitter mínimo: " + Long.toString(minimo)
						+ " ms\nJitter máximo: " + Long.toString(maximo) + " ms\nJitter médio: "
						+ String.format("%.3f", media) + " ms\n");
				saida.write(textPane.getText().getBytes()); //Envia para o cliente o resultado dos testes
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
			while (emCurso) { //Enquanto o cliente estiver enviando pacotes, fica nesse while para receber pacotes
				byte[] armazenador = new byte[qtdBytes]; //seta o tamanho do array de bytes para ser igual ao que o cliente setou
				DatagramPacket pacoteRecebido = new DatagramPacket(armazenador, armazenador.length);
				try {
					recebendo.receive(pacoteRecebido); //Aqui o pacote é recebido
					tempo = System.currentTimeMillis(); //Aqui salva o tempo que o último pacote até o momento chegou
					contadorPacotes++;
					byte[] auxCabeca = { armazenador[0] }; //A partir daqui, analiza o cabeçalho do pacote udp
					BitSet cabecalho = BitSet.valueOf(auxCabeca);
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

					if ((value == anterior + 1) || (value == 0 && anterior == 3)) { //Aqui é calculado os jitters mínimo e máximo
						if (minimo > tempo - tempoAnterior) {
							minimo = tempo - tempoAnterior;
						}
						if (maximo < tempo - tempoAnterior) {
							maximo = tempo - tempoAnterior;
						}
						total += tempo - tempoAnterior;
						contadorIntervalos++;

					}
					anterior = value;
					tempoAnterior = tempo;
					media = (double) total / contadorIntervalos; //Aqui é calculado o jitter médio
					qtdBytesRecebidos += qtdBytes;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	};
}
