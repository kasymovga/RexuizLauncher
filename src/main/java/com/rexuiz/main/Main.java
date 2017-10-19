package com.rexuiz.main;

import java.util.logging.Logger;

public class Main extends Runner {

	private static final Logger log = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		log.info("Application start");
		Main rla = new Main();
		rla.run();
	}
}
