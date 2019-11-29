package server;

import handlers.Edit;
import handlers.Edit.Type;
import handlers.Encoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import debug.Debug;

/**
 * 
 * O OurThreadClass cuida da criação de um novo encadeamento que lida com uma 
 * conexão de cliente. Enquanto o encadeamento estiver em execução, ele poderá 
 * escutar as mensagens do cliente, manipular a mensagem e enviar de volta a
 * mensagem do servidor para o cliente. Para testar a estratégia, consulte 
 * OurThreadClassTest.java.
 * 
 */
public class OurThreadClass extends Thread {

	private static final boolean DEBUG = Debug.DEBUG;
	final Socket socket;
	private boolean alive;
	private String username;
	private final Server server;
	private final String regex = "(bye)|(new [\\w\\d]+)|(look)|(open [\\w\\d]+)|(change .+)|(name [\\w\\d]+)";
	private final String error1 = "Erro: o documento já existe.";
	private final String error2 = "Erro: Esse documento não existe.";
	private final String error3 = "Erro: ainda não existem documentos.";
	private final String error4 = "Erro: inserido na posição inválida.";
	private final String error5 = "Erro: você deve inserir um nome ao criar um novo documento.";
	private final String error6 = "Erro: Argumentos invalidos";
	private final String error7 = "Erro: Nome de usuário não está disponível";
	private final boolean sleep = false; // for debugging 

	/**
	 * Construtor para o objeto da thread
	 * 
	 * @param socket
	 *           o socket para conectar-se ao servidor
	 * @param server
         * * o servidor com o qual o cliente está tentando se comunicar
	 */
	public OurThreadClass(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		this.alive = true;
	}

	/**
	* Executa o servidor, ouvindo as conexões do cliente e tratando-as.
        * Nunca retorna, a menos que uma exceção seja lançada.
	 * 
	 */
	public void run() {
		try {
			handleConnection(socket);
		} catch (IOException e) {
		}
	}

	
	/**
	 * Manipula uma conexão de cliente único. Retorna quando o cliente se desconecta.
	 * 
	 * 
	 *
	 */
	private void handleConnection(Socket socket) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		try {
			for (String line = in.readLine(); line != null; line = in
					.readLine()) {
				String output = handleRequest(line);
				//pra debug só
				if (sleep) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
                                // se a msg for bye desliga
				if (output != null && output.equals("bye")) {
					out.close();
					in.close();
					server.removeThread(this);

				}

				// se for a mensagem ChangText, retorne a mensagem
                                //para todos os outros clientes conectados
				else if (output != null && output.startsWith("change")) {
					server.returnMessageToEveryOtherClient(output, this);
				}

				
				if (output != null) {
					out.println(output);
				}
			}
		} finally {
			out.close();
			in.close();
		}
	}

	
	/**
	 * handler for client input. Our grammar: 
	 * Message :== Edit | Open | New | Look| Bye |Name
	 * Edit :== change DocumentName Username Version (Remove|Insert) 
	 * Remove :==remove Position Position 
	 * Insert :== insert Chars Position 
	 * Open:== open DocumentName 
	 * New :== new DocumentName 
	 * Look :== look 
	 * Bye::=="bye"
	 * Name ::== name Username
	 * Username ::== Chars
	 * Chars:==.+ 
	 * Position :== Int 
	 * DocumentName :== Chars 
	 * Chars ::== \\d\\w
	 * Version :== [0-9]+
	 * Int :== [0-9]
	 * 
	 * make requested mutations on documenMap of the server if applicable, then
	 * return appropriate message to the user.
	 * 
	 * @param input
	 *            the string that is the request coming from the client
	 * @return the string that is the returning message to the user
	 */

	public String handleRequest(String input) {
		if (!alive) {
			throw new RuntimeException(
					"Não devia chegar aqui, pois o cliente já desconectou.");
		}
		String returnMessage = "";
		input = input.trim();
		String[] tokens = input.split(" ");
		if (DEBUG) {
			System.out.println("A mensagem de entrada do cliente é " + input);
		}
		if (!input.matches(regex)) {
			if (tokens.length == 1 && tokens[0].equals("new")) {
				return error5;
			} else {
				return error6;
			}
		} else {
			if (tokens[0].equals("bye")) {
				// 'bye' request
				alive = false;
				returnMessage = "bye";

			} else if (tokens[0].equals("new")) {
				String documentName = tokens[1];
				if (DEBUG) {
					System.out.println("criando novo documento");
				}
				if (server.getDocumentMap().containsKey(documentName)) {
					returnMessage = error1;
				} else {
					server.addNewDocument(documentName);
					returnMessage = "new " + documentName;
				}
			}else if(tokens[0].equals("name")){
				if (DEBUG){System.out.println(tokens[1]);}
				if(server.nameIsAvailable(tokens[1])){
					this.username = tokens[1];
					server.addUsername(this, tokens[1]);
					returnMessage = "name "+tokens[1];
				}
				else{
					returnMessage = error7;
				}

			} else if (tokens[0].equals("look")) {
				// se o servidor não tiver nenhum documento, retorna mensagem de erro
				// senao retorne uma sequência de nomes separados por um espaço
				String result = "alldocs";
				if (server.documentMapisEmpty()) {
					returnMessage = error3;
				} else {
					result = result + server.getAllDocuments();
					returnMessage = result;
				}

			} else if (tokens[0].equals("open")) {
				// 'open', deve abrir um documento se ele existir no servidor
				String documentName = tokens[1];
				if (!server.getDocumentMap().containsKey(documentName)
						|| !server.getDocumentVersionMap().containsKey(
								documentName)) {
					returnMessage = error2;
				} else {
					int version = server.getVersion(documentName);
					String documentText = Encoding.encode(server
							.getDocumentText(documentName));
					returnMessage = "open " + documentName + " " + version
							+ " " + documentText;
				}

			} else if (tokens[0].equals("change")) {
				// 'change' altera a string salva no servidor
				int version = Integer.parseInt(tokens[3]);
				int offset, changeLength;
				Edit edit;
				String documentName = tokens[1];
				String editType = tokens[4];
				String username = tokens[2];
				if (!server.getDocumentMap().containsKey(documentName) || !server.getDocumentVersionMap().containsKey(
						documentName)) {
					// if the server does not have the document
					returnMessage = error2;
				} else {
					Object lock = new Object();
					// Apenas um segmento deve estar abaixo, porque, conforme alteramos o número da versão,
                                        ///pode haver uma condição de corrida.
                                        //Ou seja, uma thread verifica se o número da versão está correto, esse numero é
                                        //alterado posteriormente por outra thread.
					synchronized (lock) {
						if (server.getVersion(documentName) != version) {
							if(editType.equals("insert")){
								offset = Integer.parseInt(tokens[6]);
							}
							else{offset = Integer.parseInt(tokens[5]);}
							String updates = server.manageEdit(documentName,version, offset);
							String[] updatedTokens = updates.split(" ");
							version = Integer.parseInt(updatedTokens[1]);
							offset = Integer.parseInt(updatedTokens[2]);
						} 
						int length = server.getDocumentLength(documentName);
						if (editType.equals("remove")) {
							offset = Integer.parseInt(tokens[5]);
							int endPosition = Integer.parseInt(tokens[6]);
							server.delete(documentName, offset, endPosition);
							changeLength = offset - endPosition; // negative
							edit = new Edit(documentName, Type.REMOVE, "",
									version, offset, changeLength);
							server.logEdit(edit);
							server.updateVersion(documentName, version + 1);
							returnMessage = createMessage(documentName, username,
									version + 1, offset, changeLength,
									Encoding.encode(server
											.getDocumentText(documentName)));// encode the message!
						} else if (editType.equals("insert")) {
							Type type = Type.INSERT;
							offset = Integer.parseInt(tokens[6]);
							String text = Encoding.decode(tokens[5]);
							if (offset > length) {
								returnMessage = error4;
							} else {
								server.insert(documentName, offset, text);
								changeLength = text.length();
								edit = new Edit(documentName, type, text,
										version, offset, changeLength);
								server.logEdit(edit);
								server.updateVersion(documentName, version + 1);
								returnMessage = createMessage(documentName, username,
										version + 1, offset, changeLength,
										Encoding.encode(server.getDocumentText(documentName)));
							}
						}
					}
				}
			}
		}
		return returnMessage;
	}
	
	
	/**
	 * Gere uma mensagem de retorno a partir dos argumentos fornecidos
	 * 
	 */
	private String createMessage(String documentName, String username, int version, int offset,
			int changeLength, String documentText) {
		String message = "change " + documentName + " " +username+" "+ version + " "
				+ offset + " " + changeLength + " " + documentText;
		return message;
	}
	
	
	/**
	 * retornar o socket do cliente
	 * @return socket, o socket do client
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 *retorne o userName do cliente para essa thread
	 * @return userName
	 */
	public String getUsername() {
		return username;
	}

	
	/**
	 * @return alive, um booleano que nos diz se um client saiu e se desconectou
         * 
	 */
	public boolean getAlive() {
		return alive;
	}
}
