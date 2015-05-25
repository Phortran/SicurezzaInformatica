package ssi.santi_valenti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApkSslTester {
	private static final Logger log = LoggerFactory.getLogger(ApkSslTester.class);

	public static void main(String[] args) {
		log.debug("D:valenti sbaglia");
		log.info("I:valenti sbaglia");
		System.out.println(log.isDebugEnabled());
		log.trace("T:valenti sbaglia");
		log.error("E:valenti sbaglia");
		log.warn("W:valenti sbaglia");
	}

}
