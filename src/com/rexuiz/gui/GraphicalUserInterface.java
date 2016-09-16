package com.rexuiz.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

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
		setLayout(new GridLayout(3, 1));
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("icon.png");
		if (input !=  null) {
			try {
				BufferedImage icon = ImageIO.read(input);
				if (icon !=  null)
					setIconImage(icon);
			} catch (Exception ex) {
				//ignore
			}
		}
		input = Thread.currentThread().getContextClassLoader().getResourceAsStream("logo.png");
		if (input !=  null) {
			try {
				BufferedImage logo = ImageIO.read(input);
				if (logo !=  null)
					getContentPane().add(new JLabel(new ImageIcon(logo)));
			} catch (Exception ex) {
				//ignore
			}
		}
		getContentPane().add(statusLabel);
		getContentPane().add(progressBar);
		pack();
		setSize(320, 120);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

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
	public void message(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}
}
