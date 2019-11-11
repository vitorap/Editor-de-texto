
import java.io.File;

/**
 * Classe responsável por gerenciar a passagem do arquivo editado para o servidor e por 
 * atualizar o arquivo do editor de texto.
 * 
 *  
 */
/** Alunos:
*@autor Vitor Albuquerque de Paula -8628220
*@autor Caique Honorio Cardoso -8910222
*/
public class Processo implements Runnable {

    File f;
    Servidor S;

    /**
     * @param file
     * @param servidor 
     */
    public Processo(File file, Servidor servidor) {
        f = file;
        S = servidor;
    }

    @Override
    public void run() {
        S.setF(f);

    }

}
