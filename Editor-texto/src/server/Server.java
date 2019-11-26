package server;

import handlers.Edit;
import handlers.EditManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import debug.Debug;

/**
O servidor escuta a mensagem enviada pela rede a partir do cliente. 
* Ele atualiza seu próprio estado e envia a mensagem apropriada de volta ao 
* cliente que enviou a mensagem ou a todos os clientes ativos, dependendo da 
* mensagem de entrada.
*
*  campos privados: documentMap: um mapa que mapeia o nome do documento para seu texto.
*  Armazenamos todo o documento no servidor.
*  serverSocket: o soquete do servidor
*  threadList: uma lista de Threads, cada um é para uma conexão de cliente editManager: representa uma fila de edições
*/

public class Server {
	private static final boolean DEBUG = Debug.DEBUG;
	private final Map<String, StringBuffer> documentMap;
	private final Map<String, Integer> documentVersionMap;
	private ServerSocket serverSocket;
	private ArrayList<OurThreadClass> threadList;
	private ArrayList<String> usernameList;
	private final EditManager editManager;

	/**
	 * Crie um servidor que escute as conexões na porta.
	 * 
	 * @param port
	 *            numero da porta
	 * @requires 0 <= port <= 65535.
	 * @throws IOException
	 */
	public Server(int port, Map<String, StringBuffer> documents,
			Map<String, Integer> version) {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Servidor criado. Ouvindo na porta " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		documentMap = Collections.synchronizedMap(documents);
		threadList = new ArrayList<OurThreadClass>();
		documentVersionMap = Collections.synchronizedMap(version);
		usernameList = new ArrayList<String>();
		editManager = new EditManager();
	}

	/**
	 * Executa o servidor, ouvindo as conexões do cliente e tratando-as. 
         * Nunca retorna, a menos que uma exceção seja lançada.
	 * 
	 */
	public void serve() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				OurThreadClass t = new OurThreadClass(socket, this);
				threadList.add(t);
				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized Map<String, StringBuffer> getDocumentMap() {
		return documentMap;

	}
	/**
	 * Verifica se um nome de usuário já está na lista de nome de usuário.
	 * @param name
	 * @return
	 */
	public synchronized boolean nameIsAvailable(String name){
		return !usernameList.contains(name);
	}
	
	public synchronized void addUsername(OurThreadClass t, String name){
		usernameList.add(name);
	}

	/**
	 * @return o documentVersionMap, um campo particular do servidor
	 */
	public synchronized Map<String, Integer> getDocumentVersionMap() {
		return documentVersionMap;

	}

	/**
	 * 
	 * @return todos os nomes de documentos no servidor em uma única string
         * por um espaço. Ex: "doc1 doc2"
	 */
	public synchronized String getAllDocuments() {
		String documentNames = "";
		for (String key : documentMap.keySet()) {
			documentNames += " " + key;
		}
		return documentNames;
	}

	/**
	 * Gerencia a edição feita por um cliente
	 * 
	 * @param documentName
	 *            O Nome do documento editado
	 * @param version
	 * o número da versão do documento enviado pelo cliente
	 */
	public synchronized String manageEdit(String documentName, int version,
			int offset) {
		return editManager.manageEdit(documentName, version, offset);
	}

	public synchronized boolean documentMapisEmpty() {
		return documentMap.isEmpty();
	}

	
	public synchronized boolean versionMapisEmpty() {
		return documentVersionMap.isEmpty();
	}

	
	public synchronized void logEdit(Edit edit) {
		editManager.logEdit(edit);
	}

	/**
	 * Remove um thread da lista de threads
	 * 
	 * @param t
	 *            a thread que vai ser removida
	 */
	public synchronized void removeThread(OurThreadClass t) {
		if (DEBUG) {
			System.out.println("removendo thread");
		}
		usernameList.remove(t.getUsername());
		threadList.remove(t);
	}

	/**
	 * Cria um novo documento e o adiciona ao documentMap e ao documentVersionMap
         * com a versão  = 1.
	 * 
	 * @param documentName
	 *            nome do documento
	 */
	public synchronized void addNewDocument(String documentName) {
		documentMap.put(documentName, new StringBuffer());
		documentVersionMap.put(documentName, 1);
		editManager.createNewlog(documentName);

	}

	/**
	 * Atualiza a versão do documentName especificado no documentVersionMap. 
         * Se documentName ainda não for uma chave no documentVersionMap, 
         * um novo par de valores-chave será adicionado ao mapa.
	 * 
	 * @param documentName
	 *            o nome do documento
	 * @param version
	 *           o novo número da versão
	 */
	public synchronized void updateVersion(String documentName, int version) {
		documentVersionMap.put(documentName, version);
	}

	/**
	 *Retorna a versão atual do documento especificado
	 * 
	 */
	public synchronized int getVersion(String documentName) {
		return documentVersionMap.get(documentName);
	}

	/**
	 *Exclui o texto no documento especificado do deslocamento especificado 
         * para a posição final especificada. Se a posição inicial for menor que
         * 0 ou se a posição final for menor que 1, lance uma exceção de
         * tempo de execução
	 * 
	 * @param documentName
	 *            o nome do documento
	 * @param offset
         * a posição inicial do texto  que será excluída
	 * @param endPosition
	 *            a posicao final do texto que sera excluido
	 */
	public synchronized void delete(String documentName, int offset,
			int endPosition) {
		if (offset < 0 || endPosition < 1) {
			throw new RuntimeException("argumentos invalidos");
		}
		documentMap.get(documentName).delete(offset, endPosition);
	}

	/**
	 * Insere o texto no documento especificado no deslocamento especificado
	 * 
	 * @param documentName
	 *            o nome do documento
	 * @param offset
	 *            onde o texto vai ser inserido, posicao inicial
	 * @param text
	 *            o texto que queremos inserir
	 */
	public synchronized void insert(String documentName, int offset, String text) {
		documentMap.get(documentName).insert(offset, text);
	}

	/**
	 * Retorna a string que representa o texto do documento
	 * 
	 * @param documentName
	 *            o nome do documento
	 */
	public synchronized String getDocumentText(String documentName) {
		String document = "";
		document = documentMap.get(documentName).toString();
		return document;
	}

	/**
	 *Retorna o tamanho do documento especificado
	 * 
	 * @param documentName
	 *            o nome do documento
	 */
	public synchronized int getDocumentLength(String documentName) {
		return documentMap.get(documentName).length();
	}

	/**
	 * Envia uma mensagem de todas threads no threadList, exceto a thread 
         * que originalmente enviou a mensagem (sem mensagens duplicadas) e 
         * as threads que já o fecharam (ou seja, quando o cliente desconectou).
	 */
	public void returnMessageToEveryOtherClient(String message,
			OurThreadClass thread) {
		for (OurThreadClass t : threadList) {
			if (!thread.equals(t) && !t.getSocket().isClosed()) {
				PrintWriter out;
				if (t.getSocket().isConnected()) {
					synchronized (t) {
						try {
							out = new PrintWriter(t.getSocket()
									.getOutputStream(), true);
							out.println(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
