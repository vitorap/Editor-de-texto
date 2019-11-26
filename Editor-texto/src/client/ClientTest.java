
package client;

import static org.junit.Assert.*;
import gui.MainWindow;
import org.junit.Test;

public class ClientTest {

    
// estratégia de teste:
//
// ************************************************** ********************************
//
// 1. teste se o cliente manipula as mensagens do servidor corretamente
//
    // Particione o espaço de entrada de acordo com se a entrada corresponde a nossa
    // gramática da mensagem do servidor para o cliente e também a palavra inicial da mensagem.
    // i.e: private final String regex = "(error [123]: .+)|"
    // + "(alldocs .\\+)|(new [\\w|\\d]+)|(open .[\\w|\\d]+)|"
    // + "(change [\\w|\\d]+ (.+))";
	// Test for error, alldocs, new, open, change messages.
//
// Para cada mensagem, teste se o estado do documento subjacente foi alterado.
// Também testando manualmente para diferentes GUIs.

// 2.Verifique também se há conexão válida com o servidor
// e o início de uma nova GUI da janela principal para o lado do cliente.
//
// 3. Também testamos os métodos getText () e getName () para o cliente.
//
	

    
     MainWindow main= new MainWindow();
     Client client= new Client(4444, "localhost", main);
   //fazendo um novo ouvinte de açoes
     ClientActionListener clientActionListener=new ClientActionListener(client, client.getSocket());
      
       
    @Test
    public void testForError1(){
        String input= "Erro: Documento ja existe";
        clientActionListener.handleMessageFromServer(input);
        assertEquals(null, client.getDocumentName());
        assertEquals(null, client.getText());
       
    }
    
    @Test
    public void testForError2(){
        String input= "Erro: Esse documento não existe.";
        clientActionListener.handleMessageFromServer(input);
        assertEquals(null, client.getDocumentName());
        assertEquals(null, client.getText());
       
    }
    
    @Test
    public void testForError3(){
        String input=  "Erro: Ainda não existe documento.";
        clientActionListener.handleMessageFromServer(input);
        assertEquals(null, client.getDocumentName());
        assertEquals(null, client.getText());
    }
    

       @Test
        public void testNew(){
            String input=  "novo abcdario";
            clientActionListener.handleMessageFromServer(input);
            assertEquals("abc", client.getDocumentName()); 
            assertEquals(null, client.getText());
        }
       
       @Test
       public void testOpen(){
           String input=  "abrir abc 4 bacg dgege vg";
           clientActionListener.handleMessageFromServer(input);
           assertEquals("abc", client.getDocumentName()); 
           assertEquals(4, client.getVersion()); 
           assertEquals("bacg dgege vg", client.getText()); 
       }
    
       @Test
       public void testChange(){
           String inputnew=  "novo abc";
           clientActionListener.handleMessageFromServer(inputnew);
           String input=  "mudar abc nome 1 1 1 testando som som";
           clientActionListener.handleMessageFromServer(input);
           assertEquals("abc", client.getDocumentName()); 
           assertEquals(1, client.getVersion()); 
           assertEquals("teste teste", client.getText()); 
       }
    
}
