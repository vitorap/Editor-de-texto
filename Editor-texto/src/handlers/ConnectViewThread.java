package handlers;

import gui.ConnectView;

import java.io.IOException;

import javax.swing.JOptionPane;

/**
 *O ConnectViewThread cria uma nova thread, quando essa thread é executada, 
 * alterna a visualização de conexão atual do cliente para a visualização de
 * boas-vindas e também inicia o cliente para que o cliente possa receber 
 * as mensagens do servidor.
 *
 */
public class ConnectViewThread extends Thread {
    private final ConnectView connectView;
    
    /**
     * Constructor
     * @param connectView a view que estavamos antes
     */
	public ConnectViewThread(ConnectView connectView) {
		this.connectView=connectView;
	}

	/**
	 *  Inicia o cliente em uma nova thread do ConnectView e inicia o cliente.
     *
	 */
	public void run() {
		
		try {
			connectView.getClient().start();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
				    e.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e1){
			JOptionPane.showMessageDialog(null,
				    "Argumentos invalidos",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}
}


