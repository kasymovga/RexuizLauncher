package com.rexuiz.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class GraphicalUserInterface extends JFrame {
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JProgressBar subProgressBar;
	private String statusSafeMessage;

	public GraphicalUserInterface() {
		setTitle("Rexuiz Launcher");
		statusLabel = new JLabel("Preparing to launch...", SwingConstants.CENTER);
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		subProgressBar = new JProgressBar(0, 100);
		subProgressBar.setStringPainted(true);
		setLayout(new GridLayout(4, 1));
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
		getContentPane().add(subProgressBar);
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

	public void progress(double f) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				final private double progress = f;
				public void run() {
					progressBar.setValue((int)(progress * 100));
					progressBar.setString(String.format("%3.1f%%", progress * 100));
				}
			});
		} catch (Exception ex) {
		}
	}
	public void subProgress(double f, String subStatus) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				final private double progress = f;
				final private String status = subStatus;
				public void run() {
					subProgressBar.setValue((int)(progress * 100));
					subProgressBar.setString(status);
				}
			});
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
