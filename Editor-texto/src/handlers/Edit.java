package handlers;

/**
 * Classe que representa uma edição no documento. 
 * Armazena o tipo de edição,o texto adicionado (se for do tipo Inserir), 
 * o comprimento da edição, o deslocamento e a versão do documento em que a edição foi feita.
  *
 */
public class Edit {
	
	/**
	 * os tipos que uma edição pode ser
	 */
	public static enum Type {INSERT, REMOVE}

	private final String documentName;
	private final Type type;
	private final String text;
	private final int length;
	private final int offset;
	private final int version;


	/**
	 * Cria uma nova edição
	 * @param documentName
	 * @param editType
	 * @param text
	 * @param version
	 * @param offset
	 * @param length
	 */
	public Edit(String documentName, Type editType, String text, int version,
			int offset, int length) {
		this.documentName = documentName;
		this.type = editType;
		this.text = text;
		this.offset = offset;
		this.length = length;
		this.version = version;
		checkRep();
	}
    
	/**
	 */
	private void checkRep() {
		assert documentName != null;
		assert type != null;
	}

	/** @return o tipo da edicao*/
	public Type getType() {
		return type;
	}

	/**
	 * 
	 * @return o texto da edicao. o texto eh "" se o tipo da edicao for
         * insert
	 */
	public String getText() {
		return text;
	}

	/** @return o offset da edicao */
	public int getOffset() {
		return offset;
	}

	/** @return o tamanho da edicao */
	public int getLength() {
		return length;
	}

	/** @return a versao da edicao */
	public int getVersion() {
		return version;
	}

	/** @return o nome do doc da edicao. */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * Cria uma string que representa a edição
	 */
	public String toString() {
		return "Edit: " + documentName + " type: " + type + " v: " + version
				+ " offset: " + offset + " length: " + length + " text: " + text;
	}

}
