package client;
import gui.MainWindow;

import javax.swing.SwingUtilities;

/**
 * O ponto de entrada do lado do cliente do nosso Editor de Texto Colaborativo.
 *
 */
public class Client_main {
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	MainWindow main = new MainWindow();
				main.setVisible(true);
		    }
		});

	} 
}
