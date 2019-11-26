package client;

import gui.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import debug.Debug;

/**
 * Escuta as atualizaçoes do servidor e lida com as mensagens do servidor.
 * 
 * 
 */
public class ClientActionListener {

	private static boolean DEBUG = Debug.DEBUG;
	private Client client;
	private Socket socket;
	private BufferedReader in;
	private final String regex = "(Error: .+)|"
			+ "(alldocs [\\w|\\d]+)|(new [\\w|\\d]+)|(open [\\w|\\d]+\\s(\\d+)\\s?(.+)?)|"
			+ "(change [\\w|\\d]+\\s[\\w|\\d]+\\s(\\d+)\\s(\\d+)\\s(-?\\d+)\\s?(.+)?)|(name [\\d\\w]+)";
	private final int groupChangeVersion = 8;
	private final int groupChangePosition = 9;
	private final int groupChangeLength = 10;
	private final int groupChangeText = 11;
	private final int groupOpenVersion = 5;
	private final int groupOpenText = 6;
	private MainWindow main;

	/**
	 * Cria um novo ClientActionListener com um cliente e um socket
	 * 
	 * @param client
	 * @param socket
	 */
	public ClientActionListener(Client client, Socket socket) {
		this.client = client;
		this.socket = socket;
		this.main = client.getMainWindow();
	}
    
	/**
	 * ouve atualizações do servidor e manipula a mensagem
	 * @throws IOException
	 */
	public void run() throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		try {
			for (String line = in.readLine(); line != null; line = in
					.readLine()) {
				handleMessageFromServer(line);
			}
		}

		finally {
			in.close();
		}
	}

	/**
	 * 
	 * Lida com mensagens do servidor atualizando a GUI e o nameOfDocument, textOfDocument também
	 * 
	 */
	public void handleMessageFromServer(String input) {
		input = input.trim();
		if(DEBUG){ System.out.println("A mensagem de entrada que o cliente recebe do servidor é" + input);}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (!matcher.find()) {
			// entrada invalida
			main.openErrorView("da CAL: falha de regex");
		}
		String[] tokens = input.split(" ");
		
		// 'error', msg de erro, atualizar apenas o front-end
		if (tokens[0].equals("Error:")) {
			main.openErrorView(input);
		}

		// "alldocs" msg do tipo alldocs, atualizar apenas o front-end
		else if (tokens[0].equals("alldocs")) {
			ArrayList<String> names = new ArrayList<String>();
			for (int i = 1; i < tokens.length; i++) {
				names.add(tokens[i]);
			}
			main.displayOpenDocuments(names);

		}
		else if (tokens[0].equals("name")){
			client.setUsername(tokens[1]);
			
			
		}

		//Cria um documento com nome válido, precisa atualizar a front e back
		else if (tokens[0].equals("new")) {
			main.switchToDocumentView(tokens[1], "");
			client.updateDocumentName(tokens[1]);
			client.updateVersion(1);
		}

		// "Abrir o documento", atualiza o front e end
		else if (tokens[0].equals("open")) {
			client.updateDocumentName(tokens[1]);
			client.updateVersion(Integer.parseInt(matcher.group(groupOpenVersion)));
			String documentText = matcher.group(groupOpenText);
			client.updateText(documentText);
			if (DEBUG){System.out.println("A mensagem de abertura recebe o documento com texto:" + documentText);}
			main.switchToDocumentView(tokens[1], documentText);

			

		}

		// Change the document.
		else if (tokens[0].equals("change")) {
			// first, need to check the documents are the same
			if(DEBUG){System.out.println("da CAL: atualizando o documento (em ClientActionListener.java)");}
			int version = Integer.parseInt(matcher.group(groupChangeVersion));
			if (client.getDocumentName()!=null) {
				if(client.getDocumentName().equals(tokens[1]) ){
				String username = tokens[2];
				String documentText = matcher.group(groupChangeText);
				if(DEBUG){System.out.println(documentText);}
				int editPosition = Integer.parseInt(matcher.group(groupChangePosition));
				int editLength = Integer.parseInt(matcher.group(groupChangeLength));
				if(DEBUG){System.out.println(documentText);}
				main.updateDocument(documentText, editPosition, editLength, username, version);
				client.updateText(documentText);
				client.updateVersion(version);

			}

		}
		}

	}

}
