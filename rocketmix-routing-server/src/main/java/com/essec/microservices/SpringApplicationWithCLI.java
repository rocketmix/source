package com.essec.microservices;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.SpringApplication;


public class SpringApplicationWithCLI {

	private static Options options = new Options();;
	private static HelpFormatter formatter = new HelpFormatter();;
	
	
	public static void run(Class<?> springBootApplicationClazz, String... args) {
		init();
		// create the parser
		CommandLineParser parser = new DefaultParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("help")) {
				formatter.printHelp(" ", options);
				return;
			}
			
			SpringApplication.run(springBootApplicationClazz, args);
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}
		
	}


	public static void init() {
		options = new Options();
		final Option fileOption = Option.builder("f").argName("file").hasArg().desc("File to be analyzed").build();
		options.addOption("?", "help", false, "Display this help");
		options.addOption(null, "portal.companyName", true, "Override de company name display on the API Portal");
		options.addOption(null, "portal.logoURL", true, "Set a logo displayed on the API Portal instead of the rocket logo. Should be a transparent PNG. URL must be absolute. Ex : http://www.acme.com/static/logo.png");
		options.addOption(null, "server.port", true, "Change HTTP port (default: 8080)");
		options.addOption(null, "install", false, "Deploy as Linux SystemV service. Run this command only with root permissions");
		options.addOption(null, "uninstall", false, "Undeploy Linux SystemV service. Run this command only with root permissions");
		options.addOption(fileOption);
	}

}
