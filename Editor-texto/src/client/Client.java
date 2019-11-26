package client;


import gui.MainWindow;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import debug.Debug;

/**
 * A classe client recebe a mensagem do servidor e envia a mensagem para ser 
 * processada pelo ClientActionListener.
 */
public class Client {

	private static final boolean DEBUG = Debug.DEBUG;
	private String nameOfDocument;
	private String textOfDocument;
	private int versionOfDocument;
	private String userName;
	private ClientActionListener actionListener;
	private Socket socket;
	private int port;
	private String host;
	private PrintWriter out;
	private MainWindow mainWindow;

	/**
	 * Constructor
	 * 
	 * @param port
	 *            a porta que o client esta conectado
	 * @param host
	 *            ip que o client vai conectar
	 * @param main
	 *           janela principal que chama o client
	 */
	public Client(int port, String host, MainWindow main) {
		this.port = port;
		this.host = host;
		mainWindow = main;

	}

	/**
	 *Começa com um welcomeView e inicia um ouvinte de açoes que escuta atualizações do servidor.
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		socket = new Socket(host, port);
		//mainWindow.switchToWelcomeView();
		mainWindow.openUsernameDialog();
		if (DEBUG){System.out.println("Client conectado ao server. ");}
		actionListener = new ClientActionListener(this, socket);
		actionListener.run();
		out = new PrintWriter(socket.getOutputStream());
		

	}

	/**
	 * Define o campo mainWindow como uma janela
	 * 
	 * @param frame
	 *            
	 */
	public void setMainWindow(MainWindow frame) {
		this.mainWindow = frame;
	}

	/**
	 * Envia a mensagem para o servidor executando out.println (message)
	 * 
	 * @param message
	 *            para enviar para o servidor
	 * 
	 */
	public void sendMessageToServer(String message) {
		if (DEBUG) {System.out.println("agora o client deve mandar uma msg");}
		try {
			out = new PrintWriter(socket.getOutputStream());
			if (DEBUG) {System.out.println("socket eh" + socket.getLocalPort());}
			out.write(message + "\n");
			out.flush();
		} catch (IOException e) {
			mainWindow.openErrorView(e.getMessage());
		}
	}
	
	/**
	 * Define o nome de usuário do cliente como o nome especificado e abre 
         * a visualização de boas-vindas do cliente
	 * @param name
	 */
	public void setUsername(String name){
		System.out.println("Definindo nome de usuario");
		userName = name;
		mainWindow.setUsername(name);
		mainWindow.switchToWelcomeView();
	}
	
	/**
	 * Retorna o nome de usuario do cliente
	 * @return userName
	 */
	public String getUsername(){
		return userName;
	}

	/**
	 * 
	 * @return o campo privado nameOfDocument
	 */
	public String getDocumentName() {
		return nameOfDocument;
	}

	/**
	 * @return o campo privado textOfDocument
	 */
	public String getText() {
		return textOfDocument;
	}
 
	/**
	 * 
	 * @return a versao atual
	 */
	public int getVersion(){
		 return versionOfDocument;
	}
	/**
	 * 
	 * @return socket\
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * 
	 * @return o campo privado mainWindow
	 */
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	//*********************Métodos de processo do back-end*****************************
	/**
	 *  O método mutator que altera o nameOfDocument a ser nomeado. 
         * Chamado quando o cliente recebe ou abre uma mensagem do servidor
	 * 
	 * @param name essa string é o novo nome do documento
	 */
	public void updateDocumentName(String name) {
		System.out.println("Atualizando nome do documento");
		nameOfDocument = name;
	}

	/**
	 * O método mutator que altera o textOfDocument para ser texto. 
         * Chamado quando o cliente recebe, abre ou altera uma mensagem do servidor.
	 * @param text  a String que é o novo texto do documento
	 */
	public void updateText(String text) {
		textOfDocument = text;
	}

	/**
	 * O método mutador que altera o número da versão
	 * @param newVersion o novo número da versão do documento
	 */
	public void updateVersion(int newVersion) {
		versionOfDocument = newVersion;
	}

}
