package com.rexuiz.gui;

import javax.swing.*;
import java.awt.*;

public class GraphicalUserInterface extends JFrame {
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private String statusSafeMessage;
	private double progressSafeValue;

	public GraphicalUserInterface() {
		setTitle("Rexuiz Launcher");
		statusLabel = new JLabel("Preparing to launch...", SwingConstants.CENTER);
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		setLayout(new GridLayout(2, 2));
		getContentPane().add(statusLabel);
		getContentPane().add(progressBar);
		pack();
		setSize(320, 120);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void showMainDialog() {
		this.setVisible(true);
	}

	private final Runnable statusSafe = new Runnable() {
		public void run() {
			statusLabel.setText(statusSafeMessage);
		}
	};

	public void status(String message) {
		statusSafeMessage = message;
		try {
			SwingUtilities.invokeAndWait(statusSafe);
		} catch (Exception ex) {
		}
	}

	private final Runnable progressSafe = new Runnable() {
		public void run() {
			progressBar.setValue((int)(progressSafeValue * 100));
			progressBar.setString(String.format("%3.1f%%", progressSafeValue * 100));
		}
	};
	public void progress(double f) {
		progressSafeValue = f;
		try {
			SwingUtilities.invokeAndWait(progressSafe);
		} catch (Exception ex) {
		}
	}
	public boolean ask(String question) {
		JFrame frame = new JFrame();
		int answer = JOptionPane.showConfirmDialog(frame, question, "", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}
}
