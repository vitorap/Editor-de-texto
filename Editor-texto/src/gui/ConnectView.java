package gui;

import handlers.ConnectViewThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.Client;

@SuppressWarnings("all")
/**
 * Interface que solicita aos clientes o endereço do servidor e inicia a conexão
 * Host e porta não podem ser nulos.
 */
public class ConnectView extends JPanel implements ActionListener {

	private final MainWindow frame;
	private final JLabel serverAddressLabel;
	private final JLabel hostLabel;
	private final JTextField host;
	private final JLabel portLabel;
	private final JTextField port;
	private final JButton connectButton;
	private Client client;
	private boolean DEBUG;

	/**
	 * * Cria a janela ConnectView que possui:
         * 3 JLabels: serverAddress, portLabel e hostLabel; 
         * 2 JTextfields: host e porta  para que o usuário insira as informações 
         * 1 botão "conectar" para conectar e alternar para o WelcomeView. 
	 * 
	 */
	public ConnectView(MainWindow frame) {
		this.frame = frame;
		serverAddressLabel = new JLabel("Digite o endereco do servidor:");
		hostLabel = new JLabel("IP:");
		host = new JTextField();
		host.addActionListener(this);
		portLabel = new JLabel("Porta:");
		port = new JTextField();
		port.addActionListener(this);
		connectButton = new JButton("Conectar");
		connectButton.addActionListener(this);
		this.client = frame.getClient();

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addComponent(serverAddressLabel)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup()
												.addComponent(hostLabel)
												.addComponent(portLabel))
								.addGroup(
										layout.createParallelGroup()
												.addComponent(host, 100, 150,
														Short.MAX_VALUE)
												.addComponent(port, 100, 150,
														Short.MAX_VALUE)))
				.addComponent(connectButton));
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(serverAddressLabel)
				.addGroup(
						layout.createParallelGroup()
								.addComponent(hostLabel)
								.addComponent(host, GroupLayout.PREFERRED_SIZE,
										25, GroupLayout.PREFERRED_SIZE))
				.addGroup(
						layout.createParallelGroup()
								.addComponent(portLabel)
								.addComponent(port, GroupLayout.PREFERRED_SIZE,
										25, GroupLayout.PREFERRED_SIZE))
				.addComponent(connectButton));

	}

	/**
	Quando o botão enter ou connect for pressionado, verifique se há entradas 
        * válidas. Se as entradas forem válidas, o ConnectViewThread será 
        * iniciado para lidar com a mudança para o WindowView. 
        * Se uma entrada inválida for detectada, uma janela de erro 
        * "entrada inválida" será exibida e os campos host e porta serão limpos.
	 * 
	 * @param e
	 *            evento sobre o botao conectar ou enter pressionado
	 */
	public void actionPerformed(ActionEvent e) {
		String hostInput = host.getText().trim();
		String portInput = port.getText().trim();
		String portRegex = "\\d\\d?\\d?\\d?\\d?";
		if (hostInput.length() != 0 && portInput.matches(portRegex)) {
			try {
				client = new Client(Integer.parseInt(portInput), hostInput,
						frame);
				frame.setClient(client);
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(null, "Argumentos Invalidos",
						"Error", JOptionPane.ERROR_MESSAGE);

			}
			client.setMainWindow(frame);
			if (DEBUG) {
				System.out
						.println("Estou aqui, e agora irei setar a nova janela principal atraves de setMainWindow. ConnectView");
			}
			// inicie um nova thread ConnectViewThread que cuide de 
                        // alternar a exibição de conexão para a janela e também inicie 
                        //o cliente para que o cliente comece a ouvir atualizações do servidor.
			ConnectViewThread thread = new ConnectViewThread(this);
			// comeca a thread
			thread.start();

		} else {
			JOptionPane.showMessageDialog(null, "Argumentos invalidos", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Retorna o campo do Client
	 * 
	 * 
	 */
	public Client getClient() {
		return client;
	}
}
