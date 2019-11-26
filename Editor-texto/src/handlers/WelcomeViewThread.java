package handlers;

import client.Client;
import debug.Debug;

/**
 * O WelcomeViewThread cria um novo objeto Thread que envia a entrada do
 * WelcomeView para o servidor.
 */
public class WelcomeViewThread extends Thread {
	private static final boolean DEBUG = Debug.DEBUG;

	private final String message;
	private final Client client;
	
    /**
     * 
     * @param client o cliente que está executando a ação
     * @param message a mensagem que o cliente deseja enviar para o servidor
     */
	public WelcomeViewThread(Client client, String message) {

		this.message = message;
		this.client = client;
	}
    /**
     * Envia a mensagem do WelcomeView para o servidor.
     */
	public void run() {
		if (DEBUG) {System.out.println("mandando mensagem");}
		client.sendMessageToServer(message);
	}

}
