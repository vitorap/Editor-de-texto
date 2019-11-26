package gui;

import handlers.WelcomeViewThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.Client;


/**
 * WelcomeView representa a tela de boas-vindas apresentada aos clientes após
 * a conexão com o servidor
 */
@SuppressWarnings("all")
public class WelcomeView extends JPanel implements ActionListener {
	private final MainWindow frame;
	private JLabel welcomeLabel;
	private JLabel createNewLabel;
	private JTextField documentName;
	private JButton createNewButton, openDocumentButton;
	private Client client;

	/**
	 * Cria um novo WelcomeView com o quadro de nível superior e o Cliente
	 * 
	 * @param frame
	 * @param client
	 */
	public WelcomeView(MainWindow frame, Client client) {
		this.frame = frame;
		this.client = client;
		welcomeLabel = new JLabel("Seja bemvindo. Sou um editor de texto colaborativo.");
		System.out.println("Atualmente, estou fazendo uma visualização bem-vinda.");
		createNewLabel = new JLabel("Digite o nome do novo documento:");
		documentName = new JTextField();
		documentName.addActionListener(this);
		createNewButton = new JButton("Criar");
		createNewButton.addActionListener(this);
		
		openDocumentButton = new JButton("Abrir documento existente");
		openDocumentButton.addActionListener(this);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(welcomeLabel).addComponent(createNewLabel)
				.addComponent(documentName, 100, 150, Short.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
						.addComponent(createNewButton).addComponent(openDocumentButton))
				);
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(welcomeLabel)
				.addComponent(createNewLabel)
				.addComponent(documentName, GroupLayout.PREFERRED_SIZE, 25,
						GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup()
						.addComponent(createNewButton).addComponent(openDocumentButton)));

	}

	/**
	 * Ouvinte para o campo documentName. Verifica se o documentName é 
         * válido e, em seguida, inicia uma nova thread do WelcomeView que envia
         * uma mensagem "new" ao servidor
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == createNewButton || e.getSource() == documentName) {
			String newDocumentName = documentName.getText().trim();
			if (newDocumentName.matches("[\\w\\d]+")) {
				WelcomeViewThread thread = new WelcomeViewThread(client, "new "	+ newDocumentName);
				thread.start();
			} else {
				JOptionPane.showMessageDialog(null,
						"O nome do documento não pode estar vazio e deve conter apenas letras e dígitos.",
						"Nome de documento inválido", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource() == openDocumentButton){
			client.sendMessageToServer("look");
		}
	}

	/**
	 * Retorna o cliente do WelcomeView
	 * 
	 */
	public Client getClient() {
		return client;

	}
}
