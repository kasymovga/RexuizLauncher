import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class RLGUI extends JFrame {
	private JLabel statusLabel;
	private JLabel infoLabel;
	public RLGUI() {
		this.setTitle("Rexuiz Launcher");
		statusLabel = new JLabel("Preparing to launch...");
		infoLabel = new JLabel("...");
		this.setLayout(new GridLayout(2, 2));
        this.getContentPane().add(statusLabel);
        this.getContentPane().add(infoLabel);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void run() {
		this.setVisible(true);
		this.whenStarted();
	}
	public void whenStarted() {
	}
	public void status(String message) {
		statusLabel.setText(message);
	}
	public void progress(double f) {
		infoLabel.setText(String.format("%3.1f%%", f * 100));
	}
	public boolean ask(String question) {
		JFrame frame = new JFrame();
		int answer = JOptionPane.showConfirmDialog(frame, question, "", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			System.out.println("true");
			return true;
		}
		System.out.println("false");
		return false;
	}
}
