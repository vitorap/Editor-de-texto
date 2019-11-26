package handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 *Classe que contém os métodos para codificação e decodificação de texto a serem
  * enviadso pela rede.
  *
 * 
 */
public class Encoding {

	/**
	 *Codifica texto usando o URLEncoder de acordo com o esquema de
         * codificação UTF-8
	 * 
	 * @param text
	 *            o texto que vai ser codificado
	 * @return o texto apos ser codificado
	 */
	public static String encode(String text) {
		String result = "";
		if (text == null) {
			return result;
		}
		try {
			result = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Decodifica o texto usando URLDecoder de acordo com o esquema 
         * de codificação UTF-8
	 * 
	 * @param text
	 *            texto que vai ser decodificado
	 */
	public static String decode(String text) {
		String result = "";
		if (text == null) {
			return result;
		}
		try {
			result = URLDecoder.decode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;

	}

}
