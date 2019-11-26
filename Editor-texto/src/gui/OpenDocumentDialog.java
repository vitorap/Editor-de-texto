package gui;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import client.Client;


/**
 * OpenDocumentView representa a exibição que os clientes veem ao abrir um
 * documento existente
 */
public class OpenDocumentDialog extends JOptionPane {
	private static final long serialVersionUID = 1L;

	/**
	 *Cria um novo OpenDocumentDialog como um JOptionPane que mostra ao cliente 
         * quais documentos estão no servidor. Quando os clientes fazem uma 
         * escolha, é enviada como uma mensagem "aberta" para o servidor.
	 * 
	 * @param documentNames
	 *           lista de nomes dos documentos
	 * @param client o cliente que está escolhendo o documento a ser aberto
	 */
	public OpenDocumentDialog(ArrayList<String> documentNames, Client client) {

		if (documentNames == null) {
			JOptionPane.showMessageDialog(null,
					"Ainda não há documento no servidor", "Error",
					JOptionPane.ERROR_MESSAGE);
		} 
		else {
			Object[] documentsOnServer = new Object[documentNames.size()];
			for (int i = 0; i < documentNames.size(); i++) {
				documentsOnServer[i] = documentNames.get(i);
			}
			String s = (String) JOptionPane.showInputDialog(null,
					"Escolha um documento:\n", "Abrir uma caixa de diálogo de documento",
					JOptionPane.PLAIN_MESSAGE, icon, documentsOnServer,
					documentsOnServer[0]);

			if (s != null) {
				client.sendMessageToServer("open " + s);
			}
		}
	}
}
