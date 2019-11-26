package gui;

import handlers.Encoding;
import handlers.MessageSwingWorker;

import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import client.Client;
import debug.Debug;

/**
 * Classe que representa a interface do editor
 */
public class DocumentView extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG = Debug.DEBUG;
	private JFrame frame;
	private JMenuBar menu;
	private JMenu file, edit;
	private JMenuItem newfile, open, exit, copy, cut, paste; 
	private JLabel documentNameLabel;
	private String documentName, documentText;
	private JTextArea area;
	private JScrollPane scrollpane;
	private DefaultCaret caret;
	private TextDocumentListener documentListener;
	private final Client client;
	private final String username;
	private int currentVersion;
	private boolean sent = false; 
        private UndoManager undoManager = new UndoManager();
        UndoAction undoAction = new UndoAction();
        private RedoAction redoAction = new RedoAction();
	

	/**
	 * Cria um novo DocumentView; Usado para depuração/teste
	 */
	public DocumentView(MainWindow frame) {
		this.frame = frame;
		this.client = null;
		this.username = "";
		documentNameLabel = new JLabel("You are editing document: ");
		createLayout();
	}

	/**
	 * Cria um novo DocumentView com a MainWindow, documentName e o texto 
         * do documento.
	 * 
	 * @param client
	 * @param documentName
	 */
	public DocumentView(MainWindow frame, String documentName, String text) {
		if (DEBUG) {
			System.out.println("Estou criando um documento :]");
		}
		this.frame = frame;
		this.client = frame.getClient();
		this.documentName = documentName;
		this.username = frame.getUsername();
		documentText = Encoding.decode(text);
		documentNameLabel = new JLabel("<html><B>"+documentName+"</B></html>");
		createLayout();
	}

	/**
	 * Inicializa componentes, define o layout e adiciona os ouvintes
	 */
	private void createLayout() {
		
		menu = new JMenuBar();
		file = new JMenu("Arquivo");
		edit = new JMenu("Editar");
		menu.add(file);
		menu.add(edit);

		newfile = new JMenuItem("Novo");
		newfile.addActionListener(new NewFileListener());
		file.add(newfile);
		
		copy = new JMenuItem("Copiar");
		copy.addActionListener(new CopyListener());
		edit.add(copy);
		
		cut = new JMenuItem("Cortar");
		cut.addActionListener(new CutListener());
		edit.add(cut);
		
		paste = new JMenuItem("Colar");
		paste.addActionListener(new PasteListener());
		edit.add(paste);

               
                edit.add(undoAction);
                edit.add(redoAction);
                
    		open = new JMenuItem("Abrir");
		open.addActionListener(new OpenFileListener());
		file.add(open);

		exit = new JMenuItem("Sair");
		exit.addActionListener(new ExitFileListener());
		file.add(exit);
		frame.setJMenuBar(menu);
		
		caret = new DefaultCaret();
		area = new JTextArea(25, 65);
		area.setLineWrap(true);
		area.setText(documentText);
		area.setWrapStyleWord(true);
		
		
		area.setCaret(caret);
		documentListener = new TextDocumentListener();
		area.getDocument().addDocumentListener(documentListener); 
		
                 area.getDocument().addUndoableEditListener(new UndoListener());
                
		scrollpane = new JScrollPane(area);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(documentNameLabel)
				.addComponent(scrollpane));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(documentNameLabel)
				.addComponent(scrollpane));
	}


	/**
	 * Classe que representa o DocumentListener para o documento na GUI.
	 */
	private class TextDocumentListener implements DocumentListener {
		/**
		 * Envia uma mensagem de edição ao servidor para um insertUpdate
		 */
		public void insertUpdate(DocumentEvent e) {
			synchronized (area) {
				int changeLength = e.getLength();
				int offset = e.getOffset();
				int insert = caret.getDot();
				String message;
				try {
					String addedText = area.getDocument().getText(offset,
							changeLength);
					String encodedText = Encoding.encode(addedText);					
					currentVersion=client.getVersion();
					message = "change " + documentName + " "+username+" "+ currentVersion+ " insert " + encodedText
							+ " " + insert;
					if(DEBUG){
						System.out.println(message);
					}
					sent = true;
	            	MessageSwingWorker worker = new MessageSwingWorker(client,
							message, sent);
					worker.execute(); 
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		/**
		 * Envia uma mensagem de edição ao servidor para um removeUpdate
		 */
		public void removeUpdate(DocumentEvent e) {
			synchronized (area) {
				int changeLength = e.getLength();				
				currentVersion=client.getVersion();			
				int offset = e.getOffset();
				int endPosition = offset + changeLength;
				String message = "change " + documentName +" "+username+" " +currentVersion+" remove " + offset
						+ " " + endPosition;

				if(DEBUG){
					System.out.println(message);
				}
				sent = true;
            	MessageSwingWorker worker = new MessageSwingWorker(client,
						message, sent);
				client.updateVersion(currentVersion+1);
				worker.execute();
			}
		}
	
		public void changedUpdate(DocumentEvent e) {
			// Componentes de texto sem formatação não acionam esses eventos
		}
	}


	
	/**
	 * Gerencia o cursor, dada a posição atual do cursor, a posição em que
         * a edição foi feita e a duração da alteração
	 * @param currentPos
	 * @param pivotPosition
	 * @param amount
	 */
	private void manageCursor(int currentPos, int pivotPosition, int amount) {
		if(DEBUG){
			System.out.println("primeira posicao: "+caret.getDot());
			System.out.println("pivot: "+pivotPosition);
			System.out.println("quantidade: "+amount);
		}

		if (currentPos >= pivotPosition) {
			if (currentPos <= pivotPosition + Math.abs(amount)) {
				caret.setDot(pivotPosition);
			} else {
				caret.setDot(amount+currentPos);
			}
		}
		else{
			caret.setDot(currentPos);
		}
		if(DEBUG){
			System.out.println("caret movido para: "+caret.getDot());
		}
	}

	/**
	 * Decodifica e atualiza o documento com o texto do servidor, também 
         * gerencia o cursor usando editPosition e editLength.
	 */
	public void updateDocument(String updatedText, int editPosition,
			int editLength, String username, int version) {
		documentText = Encoding.decode(updatedText);
		int pos = caret.getDot();
		synchronized (area) {
			if(this.username!=null && !this.username.equals(username)){
			area.getDocument().removeDocumentListener(documentListener);
			area.setText(documentText);
			area.getDocument().addDocumentListener(documentListener);
			manageCursor(pos, editPosition, editLength);
			}
			else if(this.username!=null && this.username.equals(username)) {
				//check if version matches up
				if(currentVersion<version-1){
					area.getDocument().removeDocumentListener(documentListener);
					area.setText(documentText);
					area.getDocument().addDocumentListener(documentListener);
					caret.setDot(editPosition+editLength);
				}
				
			}

		}
	}
	
	/**Classe representando um Ouvinte no botão Novo no JMenu*/
	private class NewFileListener implements ActionListener {
		
		/** Envia uma mensagem "nova" para o servidor quando um cliente
                 * cria um novo documento com o documento sendo a entrada do usuário 
		 */
		public void actionPerformed(ActionEvent e) {
			String newDocumentName = JOptionPane.showInputDialog(
					"Diga o nome do novo documento", "");
			// Se o cliente não clicar em "cancelar", precisa enviar a mensagem para o servidor.
			if (newDocumentName !=null){
			    String message = "new " + newDocumentName;
            	MessageSwingWorker worker = new MessageSwingWorker(client,
						message, true);
				worker.execute();
			}
		}
	}

	/** Classe representando um Ouvinte no botão Abrir no JMenu */
	private class OpenFileListener implements ActionListener {
		/**
		 * Envia uma mensagem de "aparência" ao servidor, que responderá
                 * mostrando uma caixa de diálogo com os documentos no servidor.
                 * 
		 */
		public void actionPerformed(ActionEvent e) {
			client.sendMessageToServer("look");
		}
	}

	
	/** Classe representando um Ouvinte no botão Sair no JMenu */
	private class ExitFileListener implements ActionListener {
		/**
		 * Mostra um JOptionPane pedindo ao usuário para confirmar uma saída.
                 * Se ele confirmar, o gui será fechado e o cliente será 
                 * desconectado do servidor.
		 */
		public void actionPerformed(ActionEvent e) {
			int n = JOptionPane.showConfirmDialog(null,
					"Tem certeza que deseja sair?", "Sair",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				if(!client.getSocket().isClosed()) {
					client.sendMessageToServer("bye");
					}
				System.exit(0);
			}
		}
	}
	/** Classe que representa um ouvinte no botão Copiar no JMenu*/
	private class CopyListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			area.copy();
		}
	}
	/** Classe representando um ouvinte no botão Colar no JMenu */
	private class PasteListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			area.paste();
		}
	}
	
	/** Classe que representa um ouvinte no botão Recortar no JMenu */
	private class CutListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			area.cut();
		}
	}
        
          /**
     * Essa funcao fica observando oque acontece, para saber para qual estado
     * mudar quando necessitar desfazer ou refazer
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
     * Essa funcao Desfaz uma ação
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
            this.putValue(Action.NAME, "Desfazer");
            this.setEnabled(undoManager.canUndo());
        }
    }

////////////////////////////////////////////////////////////////////////
    /**
     * Essa funcao Refaz uma ação
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
