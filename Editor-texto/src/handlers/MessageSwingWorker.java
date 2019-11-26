package handlers;

import javax.swing.SwingWorker;

import client.Client;


/**
 * MessageSwingWorker representa uma classe SwingWorker que envia mensagens para 
 * o servidor em uma thread em segundo plano.
 *
 */
@SuppressWarnings("unused")
public class MessageSwingWorker extends SwingWorker<Void, Void>{
	private Client client;
	private String message;
	private boolean sent;
	
	/**
	 * Constrói uma nova instância de um MessageSwingWorker com o client e a mensagem
	 * @param client
	 * @param message
	 * @param sent - booleano que representa o cliente enviou esta mensagem
	 */
	public MessageSwingWorker(Client client, String message, boolean sent){
		this.client = client;
		this.message = message;
		this.sent = sent;
	}
	
	/**
	 * Conecta-se ao servidor em segundo plano.
	 */
	protected Void doInBackground() {
		client.sendMessageToServer(message);
		done();
		return null;
	}
	/**
	 * Repita a GUI após a conexão com o servidor e as ações serem concluídas.
	 */
	@Override
	protected void done() {
		client.getMainWindow().repaint();
		sent = false;
		
	}
	

}
