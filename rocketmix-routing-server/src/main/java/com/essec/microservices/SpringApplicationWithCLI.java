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
		options.addOption("?", "help", false, "Display this help");
		options.addOption(null, "companyName", true, "Override de company name display on the API Portal");
		options.addOption(null, "logoURL", true, "Set a logo displayed on the API Portal instead of the rocket logo. Should be a transparent PNG. URL must be absolute. Ex : http://www.acme.com/static/logo.png");
		options.addOption(null, "port", true, "Change HTTP port (default: 8080)");
		options.addOption(null, "managementServerURL", true, "Set URL (with network port) of the management server (default: http://127.0.0.1:8761, used when management server is executed on the same machine)");
		options.addOption(Option.builder().argName("install").desc("Generate install scripts to deploy/undeploy as Linux SystemV service. You need to provide which Linux user/group will be used to run the service. You can combine this option with other options").longOpt("install").hasArgs().numberOfArgs(2).argName("user:group").valueSeparator(':').build());
	}
	

}
