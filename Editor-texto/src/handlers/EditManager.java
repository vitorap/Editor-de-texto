package handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import debug.Debug;

/**
 * Classe que gerencia a fila de edição dos documentos no servidor
 *
 */
public class EditManager {
	private final Map<String, List<Edit>> editLog;
	private static final boolean DEBUG = Debug.DEBUG;
	
	/**
	 * Cria um novo EditManager para o servidor com um novo mapa de 
         * documentNames para listas de Edits
	 * @param documentName
	 */
	public EditManager(){
		editLog = Collections.synchronizedMap(new HashMap<String, List<Edit>>());
	
	}
	
	/**
	 *Cria um novo log para o novo documento
         * chamado quando um novo documento é criado
	 * @param documentName nome do novo documento
	 * 
	 */
	public synchronized void createNewlog(String documentName){
		editLog.put(documentName, new ArrayList<Edit>());
	}
	
	/**
	 *Adiciona a edição à lista do documento. 
         * Os nomes dos documentos são retirados da edição
	 * @param edit a edicao feita
	 */
	public synchronized void logEdit(Edit edit){
		String documentName = edit.getDocumentName();
		editLog.get(documentName).add(edit);
		if (DEBUG){System.out.println(edit.toString());}
	}

	/**
	 * Percorre o versionEditLog e tenta adicionar a edição no índice correto.
         *é chamado quando o cliente atualiza uma versão desatualizada do documento.
         *passa por cada edição feita com uma versão igual ou superior à versão especificada e encontra o índice correto.
	 * @param documentName nome do documento
	 * @param version versao que a edicao foi feita
	 * @param offset o local em que a edição foi inserida
	 * @return o offset correto
	 */
	public synchronized String manageEdit(String documentName, int version,
			int offset) {
		List<Edit> list = editLog.get(documentName);
		int updatedOffset = offset;
		for (Edit edit : list) {
			if (edit.getVersion() >= version) {
				updatedOffset = manageOffset(updatedOffset, edit.getOffset(),
						edit.getLength());
				version = edit.getVersion();
			}
		}
		if (DEBUG){System.out.println("novo offset: "+offset);}
		if (DEBUG){System.out.println("nova versao: "+version);}
		String result = documentName+" "+(version+1)+" "+offset;
		return result;
	}
	

	/**
	 * Pega o deslocamento atual e o compara com o otherOffset, o 
         * deslocamento da edição já concluída e seu comprimento, e corrige o 
         * currentOffset, se necessário.
	 * @param currentOffset    a posicao atual do offset
	 * @param otherOffset     outra posicao do offset
	 * @param length          o tamanho do edit completo
	 */
	private int manageOffset(int currentOffset, int otherOffset, int length) {
		if (currentOffset < otherOffset) {
			return currentOffset;
		} else if (currentOffset < otherOffset + length && currentOffset >= otherOffset) {
			return otherOffset;
		} else {
			return currentOffset + length;
		}
	}

	
	
	

}
