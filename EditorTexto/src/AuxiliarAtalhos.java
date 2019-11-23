import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Classe auxiliar para atalhos de copiar, colar e cortar.
 * Essa classe gera poupups ao clicar com o botao direito do mouse, podendo selecionar as acoes de copiar, colar e cortar
 * tambem é possivel realizar as acoes via atalhos de teclado
 * Um menu denominado editar entrega 2, tambem pode ser acionado para ativar as acoes
 * @author vitor
 */
public class AuxiliarAtalhos {
  private JMenu jMenu;
  JPopupMenu popupMenu = new JPopupMenu();

    /**
     * inicializa a classe
     */
    public AuxiliarAtalhos() {
      init();
  }

  private void init() {
      jMenu = new JMenu("Editar Entrega 2");
      addAction(new DefaultEditorKit.CutAction(), KeyEvent.VK_X, "Cortar" );
      addAction(new DefaultEditorKit.CopyAction(), KeyEvent.VK_C, "Copiar" );
      addAction(new DefaultEditorKit.PasteAction(), KeyEvent.VK_V, "Colar" );
   //   addAction(EditorTexto.UndoAction(), KeyEvent.VK_Z, "Desfazer" );
  }

  private void addAction(TextAction action, int key, String text) {
      action.putValue(AbstractAction.ACCELERATOR_KEY,
              KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
      action.putValue(AbstractAction.NAME, text);
      jMenu.add(new JMenuItem(action));
      popupMenu.add(new JMenuItem(action));
  }

    /**
     * cria a poupup para quando o botao direito é pressionado
     * @param components
     */
    public void setPopup(JTextComponent... components){
      if(components == null){
          return;
      }
      for (JTextComponent tc : components) {
          tc.setComponentPopupMenu(popupMenu);
      }
  }

    /**
     * retorna o menu. Usado para colocar o menu na barra superior, no programa principal
     * @return
     */
    public JMenu getMenu() {
      return jMenu;
  }
}