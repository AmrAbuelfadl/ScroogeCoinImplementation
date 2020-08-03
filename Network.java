import java.util.Hashtable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Network implements KeyListener{
	static JTextField text = new JTextField();
	@Override
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
	    		System.exit(0);
	    } 
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	public static Hashtable<Integer, Integer> generateRandomNumbers(Scrooge scrooge) {
		Hashtable<Integer, Integer> randomNumbers = new Hashtable<Integer, Integer>();
		randomNumbers.put(0, 0);
		randomNumbers.put(1, 0);
		for(;randomNumbers.get(0).equals(randomNumbers.get(1));) {
			randomNumbers.put(0, (int)(Math.random()*(scrooge.users.size())));
			randomNumbers.put(1, (int)(Math.random()*(scrooge.users.size())));
			randomNumbers.keys();
		}
		return randomNumbers;
	}
	
	public static void main(String[]args) throws Exception {
		    int size = 10;
		    JFrame frame = new JFrame("Scrooge Coin Network");
		    JPanel panel = new JPanel();
		    panel.setLayout(new FlowLayout());
		    JLabel label = new JLabel("Terminate the code using spacebar");
		    JButton button = new JButton();
		    button.setText("Press me");
		    frame.addKeyListener(new Network());
		    panel.add(label);
		    frame.add(panel);
		    frame.setSize(300, 300);
		    frame.setLocationRelativeTo(null);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setVisible(true);
		
			Scrooge scrooge = new Scrooge();
			for(;;) {
				Hashtable<Integer, Integer> randomNumbers = generateRandomNumbers(scrooge);
				User sender = scrooge.users.get(randomNumbers.get(0));
				User receiver = scrooge.users.get(randomNumbers.get(1));
				int coinsNeededToBeTransferred = (int)(Math.random()*(size)+1);              
				scrooge.requestTransactionsToScrooge.add(sender.requestTransactionsToScrooge(coinsNeededToBeTransferred, receiver));
				scrooge.checkingUserRequests();
//				scrooge.myWriter.close();
			}
			
	}
}

