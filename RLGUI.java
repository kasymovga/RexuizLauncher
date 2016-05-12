import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class RLGUI extends JFrame {
	private JLabel statusLabel;
	private JProgressBar progressBar;
	public RLGUI() {
		this.setTitle("Rexuiz Launcher");
		statusLabel = new JLabel("Preparing to launch...", SwingConstants.CENTER);
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		this.setLayout(new GridLayout(2, 2));
		this.getContentPane().add(statusLabel);
		this.getContentPane().add(progressBar);
		this.pack();
		this.setSize(320, 120);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void showMainDialog() {
		this.setVisible(true);
	}
	public void status(String message) {
		statusLabel.setText(message);
	}
	public void progress(double f) {
		progressBar.setValue((int)(f * 100));
		progressBar.setString(String.format("%3.1f%%", f * 100));
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
