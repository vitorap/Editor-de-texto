/** Alunos:
*@autor Vitor Albuquerque de Paula -8628220
*@autor Caique Honorio Cardoso -8910222
*/




import java.awt.*;
import javax.swing.*;
import java.io.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.MNEMONIC_KEY;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;



/**
 * Classe principal do editor de texto
 * 
 */
public class EditorTexto extends JFrame {

    private JTextArea    _editArea;
    private JFileChooser _fileChooser = new JFileChooser();

    // Aqui criamos as acoes para os menus, de acordo com suas bibliotecas
    private Action _openAction = new OpenAction();
    private Action _saveAction = new SaveAction();
    private Action _exitAction = new ExitAction();
    private UndoManager undoManager = new UndoManager();
    UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();

  /////////////////////////////////////////////////////////////////////////// main

    /**
     * aqui se carrega o texto a ser editado (no caso de nao ser um texto novo)
     * @param args
     */
    public static void main(String[] args) {
        new EditorTexto();
    }

    /////////////////////////////////////////////////////////////////////////// Consrutor

    /**
     * 
     * 
     * Método construtor, aqui criamos uma interface, painel, e a barra de menu
     */
    public EditorTexto() {
        //cria uma interface bonitinha com scroll
        _editArea = new JTextArea(15, 80);
        _editArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        _editArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        _editArea.getDocument().addUndoableEditListener(new UndoListener());
        JScrollPane scrollingText = new JScrollPane(_editArea);

        //cria o painel
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(scrollingText, BorderLayout.CENTER);
        
        AuxiliarAtalhos auxiliar = new AuxiliarAtalhos();
        auxiliar.setPopup(_editArea);
        
        //criando a barra de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = menuBar.add(new JMenu("Arquivo"));
        JMenu editorMenu = menuBar.add(new JMenu("Editar Entrega 1"));
        fileMenu.setMnemonic('F');
        fileMenu.add(_openAction);
        fileMenu.add(_saveAction);
        fileMenu.addSeparator();
        fileMenu.add(_exitAction);
        editorMenu.setMnemonic('E');
        editorMenu.add(undoAction);
        editorMenu.add(redoAction);
        menuBar.add(auxiliar.getMenu());
        //preenchendo a janela com conteudo
        setContentPane(content);
        setJMenuBar(menuBar);

        //alguns detalhezinhos
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("EditorTexto");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * 
     * Açoes referentes a entrega 1: Abrir arquivos
     * Essa funçao abre um arquivo de texto
     * 
     */
    class OpenAction extends AbstractAction {
        /**
         * metodo construtor
         */
        
        public OpenAction() {
            super("Abrir");
            putValue(MNEMONIC_KEY, new Integer('O'));
        }

        /**
         * quando uma açao é realizada
         * 
         */
        
        @Override 
        public void actionPerformed(ActionEvent e) {
            int retval = _fileChooser.showOpenDialog(EditorTexto.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = _fileChooser.getSelectedFile();
                try {
                    FileReader reader = new FileReader(f);
                    _editArea.read(reader, "");
                } catch (IOException ioex) {
                    System.out.println(e);
                    System.exit(1);
                }
            }
        }
    }

    /**
     * 
     * Açoes referentes a entrega 1: Salvar o texto
     * Essa funçao salva o texto quando a acao de salvar o texto é invocada
     * 
     */
    class SaveAction extends AbstractAction {

        SaveAction() {
            super("Salvar");
            putValue(MNEMONIC_KEY, new Integer('S'));
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            int retval = _fileChooser.showSaveDialog(EditorTexto.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = _fileChooser.getSelectedFile();
                try {
                    FileWriter writer = new FileWriter(f);
                    _editArea.write(writer);
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(EditorTexto.this, ioex);
                    System.exit(1);
                }
            }
        }
    }

      /**
     * 
     * Açoes referentes a entrega 1: Sair do programa
     * Essa funçao sai do programa quando a acao de sair invocada
     * 
     */
    class ExitAction extends AbstractAction {


        public ExitAction() {
            super("Encerrar");
            putValue(MNEMONIC_KEY, new Integer('X'));
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

/**
 * 
 * 
 */

   /**
   *  Essa funcao fica observando oque acontece, para saber para qual estado mudar quando necessitar desfazer ou refazer
   * 
   */
    class UndoListener implements UndoableEditListener {
      @Override
      public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
        undoAction.update();
        redoAction.update();
      }
    }

  /**
   *  Essa funcao Desfaz uma ação
   * 
   */
    class UndoAction extends AbstractAction {
      public UndoAction() {
        this.putValue(Action.NAME, "Desfazer");
        this.setEnabled(false);
      }

      @Override
      public void actionPerformed(ActionEvent e) {
        if (this.isEnabled()) {
          undoManager.undo();
          undoAction.update();
          redoAction.update();
        }
      }

      public void update() {
        this.putValue(Action.NAME, undoManager.getUndoPresentationName());
        this.setEnabled(undoManager.canUndo());
      }
    }

////////////////////////////////////////////////////////////////////////

   /**
   *  Essa funcao Refaz uma ação
   * 
   */
    class RedoAction extends AbstractAction {
      public RedoAction() {
        this.putValue(Action.NAME, "Refazer");
        this.setEnabled(false);
      }

      @Override
      public void actionPerformed(ActionEvent e) {
        if (this.isEnabled()) {
          undoManager.redo();
          undoAction.update();
          redoAction.update();
        }
      }

      public void update() {
        this.putValue(Action.NAME, "Refazer");
        this.setEnabled(undoManager.canRedo());
      }
    }



}
