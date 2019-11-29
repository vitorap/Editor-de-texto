package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import debug.Debug;

/**
 * A classe server_main começará a executar um servidor que escuta uma porta especifica(1234).
 * 
 */
public class Server_main {
	private static final boolean DEBUG = Debug.DEBUG;
	private static final int defaultPort = 1234;

	/**
	 A classe server_main começará a executar um servidor que escuta uma porta especifica (1234).
         * Ponto de entrada para iniciar um Collaborative Editor Server.
        * Inicia o servidor na porta especificada ou na porta padrão (1234)
        * se nenhuma porta for especificada ou se os argumentos não seguirem o uso
        *
        * Uso: Server_main -p PORT
        * PORT = número da porta desejada para o servidor
        *
        *@param args
	 */
	public static void main(String[] args) {
		int port;
		if (args.length == 2 && args[0].equals("-p")
				&& args[1].matches("\\d\\d?\\d?\\d?\\d?")) {
			port = Integer.parseInt(args[1]);
		} else {
			port = defaultPort;
		}
		try {
			runServer(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inicia um servidor na porta especificada. 
         * 
	 * 
	 * @param port
	 *           A porta de rede na qual o servidor deve escutar
	 */

	public static void runServer(int port) throws IOException {
		if (DEBUG) {
			System.out.println("Estou rodando o server.");
		}
		Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
		Map<String, Integer> versions = new HashMap<String, Integer>();
		Server server = new Server(port, map, versions);
		server.serve();
	}
}
