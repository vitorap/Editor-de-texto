package gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import client.Client;
import debug.Debug;

/**
 *Classe que representa um ouvinte de janela na GUI.
 *
 */
public class ExitWindowListener implements WindowListener {
	private final Client client;
	private final static boolean VERBOSE = Debug.VERBOSE;
	
	public ExitWindowListener(Client client){
		this.client = client;
	}
	
	
	@Override
	public void windowOpened(WindowEvent paramWindowEvent) {
		
	}


	/**
	 * Envie uma mensagem de "bye" para o servidor enquanto fecha a janela.
	 */
	@Override
	public void windowClosing(WindowEvent paramWindowEvent) {
		if(VERBOSE){
		System.out.println("mandando tchau");
		}
		if(client != null && !client.getSocket().isClosed()){
		client.sendMessageToServer("bye");
		System.exit(0);
		}
	}


	@Override
	public void windowClosed(WindowEvent paramWindowEvent) {	
	}

	@Override
	public void windowIconified(WindowEvent paramWindowEvent) {	
	}

	@Override
	public void windowDeiconified(WindowEvent paramWindowEvent) {	
	}

	@Override
	public void windowActivated(WindowEvent paramWindowEvent) {
	}

	@Override
	public void windowDeactivated(WindowEvent paramWindowEvent) {
	}

}
