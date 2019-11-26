package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.Client;
import debug.Debug;

/**
 * A MainWindow da GUI que é uma subclasse de JFrame. É o contêiner de nível 
 * superior para a página de conexão, a página do documento, a página de 
 * boas-vindas e a página openDocument
 * 
 * 
 */
@SuppressWarnings("unused")
public class MainWindow extends JFrame {

	private static final boolean DEBUG = Debug.DEBUG;
	private static final long serialVersionUID = 1L;
	private WelcomeView welcomeView;
	private DocumentView documentView;
	private ConnectView connectView;
	private OpenDocumentDialog openDocumentDialog;
	private ArrayList<String> documentNames;
	private Client client;
	private String username;

	/**
	 * Cria uma janela principal, com a primeira tela sendo o connectView
	 */
	public MainWindow() {
		setTitle("Editor de Texto - T3");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(250, 200));
		connectView = new ConnectView(this);
		add(connectView, BorderLayout.CENTER);
		pack();
	}

	// O mainWindow deve obter o cliente do connectView depois que ele se conectar

	/**
	 * Mudar do connectView para o WelcomeView
	 */
	public void switchToWelcomeView() {
		setVisible(false);
		getContentPane().remove(connectView);

		setPreferredSize(new Dimension(350, 150));
		setMinimumSize(new Dimension(350, 150));
		setMaximumSize(new Dimension(350, 150));
		welcomeView = new WelcomeView(this, client);
		add(welcomeView, BorderLayout.CENTER);

		if (DEBUG) {
			System.out.println("Mudando para a WelcomeView");
		}
		setVisible(true);
	}

	/**
	 * Mostra a caixa de diálogo que solicita ao usuário um nome de entrada 
         * e envia a entrada ao servidor.
	 */
	public void openUsernameDialog() {
		String username = JOptionPane.showInputDialog("Digite um nome de Usuario", "");
		if(username==null){
			JOptionPane.showMessageDialog(null, "Por favor, digite um nome valido", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		else{
		client.sendMessageToServer("name " + username);
		}
	}
	
	public void setUsername(String name){
		this.username = name;
	}
	
	public String getUsername(){
		return username;
	}

	/**
	 * Mudar para o DocumentView a partir do WelcomeView
	 */
	public void switchToDocumentView(String documentName, String documentText) {
		setVisible(false);
		removeAllViews();
		setPreferredSize(new Dimension(600, 500));
		setMinimumSize(new Dimension(600, 500));
		setMaximumSize(new Dimension(600, 500));
		documentView = new DocumentView(this, documentName, documentText);
		this.addWindowListener(new ExitWindowListener(client));
		getContentPane().add(documentView, BorderLayout.CENTER);
		getContentPane().validate();
		getContentPane().repaint();
		setVisible(true);
		if (DEBUG) {
			System.out.println("alternando para a visualização do documento");
		}
	}

	/**
	 * Remove todas as visualizações do contentPane
	 */
	private void removeAllViews() {
		if (welcomeView != null) {
			getContentPane().remove(welcomeView);
		}
		if (connectView != null) {
			getContentPane().remove(connectView);
		}
		if (documentView != null) {
			getContentPane().remove(documentView);
		}
	}

	/**
	 * Abre um openDocumentDialog que exibe documentos existentes no servidor.
	 * 
	 * @param documentNames
	 *            lista de nomes dos documentos
	 */
	public void displayOpenDocuments(ArrayList<String> documentNames) {
		if (DEBUG) {
			System.out.println("alternando para abrir a exibição de documento existente");
		}
		openDocumentDialog = new OpenDocumentDialog(documentNames, client);
	}

	/**
	 * Envia um comando para o documentView para atualizar o texto do 
         * documento com o novo texto do documento.
	 * 
	 * @param documentText
	 *            texto do documento
	 * @param editPosition
	 *            a posição da edição
	 * @param editLength
	 *            o comprimento do texto inserido ou removido
	 * @param version
	 *            a versão do documento em que a edição foi feita
	 */
	public void updateDocument(String documentText, int editPosition,
			int editLength, String username, int version) {
		if (DEBUG) {
			System.out.println("atualizando documento");
		}
		if (documentView != null) {
			documentView.updateDocument(documentText, editPosition, editLength,
					username, version);
			getContentPane().repaint();
		}

	}

	/**
	 * Cria e mostra uma caixa de diálogo de mensagem
	 * 
	 * @param error
	 *            mensagem de erro
	 */
	public void openVersionErrorView(String error) {
		int n = JOptionPane.showConfirmDialog(null, error, "Error",
				JOptionPane.ERROR_MESSAGE);
		client.sendMessageToServer("open " + client.getDocumentName());
		if (DEBUG) {
			System.out.println("Envia mensagem");
		}
	}

	/**
	 * Cria e mostra uma caixa de diálogo com a mensagem de erro especificada.
	 * 
	 * @param error
	 */
	public void openErrorView(String error) {
		JOptionPane.showMessageDialog(null, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Define o cliente do MainWindow
	 * 
	 * @param client
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * Retorna o cliente desse quadro
	 */
	public Client getClient() {
		return client;
	}

}
