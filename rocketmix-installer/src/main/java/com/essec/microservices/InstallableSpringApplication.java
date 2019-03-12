package com.essec.microservices;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.SpringApplication;

/**
 * Spring Boot Application Boostrap which embeds a Linux installer. This is useful if you package your Spring Boot App
 * as a Linux executable. This installer will generates Linux systemd install scripts which help you to install your app
 * as a service. Then, you app is started and monitored by Linux.<br/>
 * <br/>
 * Check Spring Boot docs for details : {@link https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html}<br/>
 * <br/>
 * Usage :<br/>
 * Instead of using SpringApplication.run(MyApp.class, args), just do InstallableSpringApplication.run(MyApp.class, args)
 * <br/>
 * <br/>
 * Then, when you start your application with --help or, -?, it displays install options that you can customize
 * by providing an org.apache.commons.cli.Options instance which match your Spring properties to override. 
 * <br/>
 * <br/>
 * Example :<br/>
 * Options customOptions = new Options();<br/>
 * customOptions.addOption(null, "server.port", true, "Change HTTP port (default: 8080)");<br/>
 * InstallableSpringApplication.run(MyApp.class, customOptions, args);<br/>
 * <br/>
 * 
 * @author Alexandre de Pellegrin
 *
 */
public class InstallableSpringApplication {

	private static HelpFormatter formatter = new HelpFormatter();

	public static void run(Class<?> springBootApplicationClazz, String... args) {
		Options options = new Options();
		run(springBootApplicationClazz, options, args);
	}

	public static void run(Class<?> springBootApplicationClazz, Options options, String... args) {
		// init();
		options = init(options);

		// create the parser
		CommandLineParser parser = new DefaultParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args, true);
			if (line.hasOption("help")) {
				formatter.printHelp(" ", options);
				return;
			}
			if (line.hasOption("install")) {
				InstallScriptParameters params = buildInstallerParams(line);
				InstallScriptGenerator scriptGenerator = new InstallScriptGenerator();
				scriptGenerator.generateAll(params);
				return;
			}
			SpringApplication.run(springBootApplicationClazz, args);
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			formatter.printHelp(" ", options);

		}

	}



	public static Options init(Options options) {
		options.addOption("?", "help", false, "Display this help");
		if (options.getMatchingOptions("name").isEmpty()) {
			options.addOption(null, "name", true, "Force application name");
		}
		if (options.getMatchingOptions("port").isEmpty()) {
			options.addOption(null, "port", true, "Change HTTP port (default: 8080)");
		}
		if (options.getMatchingOptions("managemenServerURL").isEmpty()) {
			options.addOption(null, "managementServerURL", true, "Set URL (with network port) of the management server (default: http://127.0.0.1:8761, used when management server is executed on the same machine)");
		}
		options.addOption(Option.builder().argName("install")
				.desc("Generate install scripts to deploy/undeploy as Linux SystemV service. You need to provide which Linux user/group will be used to run the service. You can combine this option with other options").longOpt("install")
				.hasArgs().optionalArg(true).numberOfArgs(2).argName("user:group").valueSeparator(':').build());
		return options;
	}

	private static InstallScriptParameters buildInstallerParams(CommandLine line) {
		if (!line.hasOption("install")) {
			throw new RuntimeException("Unable to read --install params");
		}
		InstallScriptParameters result = InstallScriptParameters.getInstance();
		for (Option anOption : line.getOptions()) {
			switch (anOption.getLongOpt()) {
			case "name":
				result.setServiceName(line.getOptionValue("name"));
			case "install":
				String[] userParams = line.getOptionValues("install");
				if (userParams == null || (userParams != null && userParams.length != 2)) {
					String username = System.getProperty("user.name");
					result.setUser(username);
					result.setGroup(username);
				}
				if (userParams != null && userParams.length == 2) {
					result.setUser(userParams[0]);
					result.setGroup(userParams[1]);
				}
				break;
			case "managementServerURL":
				result.setManagementServerURL(line.getOptionValue("managementServerURL"));
				break;
			case "port":
				result.setServerPort(Integer.parseInt(line.getOptionValue("port")));
				break;
			default:
				result.addExternalOption(anOption.getLongOpt(), line.getOptionValue(anOption.getLongOpt(), ""));
				break;
			}
		}

		return result;
	}

}
