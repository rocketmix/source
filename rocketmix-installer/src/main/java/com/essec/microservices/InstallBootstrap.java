package com.essec.microservices;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class InstallBootstrap {

	private @Autowired ApplicationArguments args;
	private @Autowired ApplicationContext context;
	
	
	private static HelpFormatter formatter = new HelpFormatter();
	
	@PostConstruct
	public void run() throws Exception {
		Map<String, Option> optionBeans = context.getBeansOfType(Option.class);
		Options options = new Options();
		optionBeans.values().stream().forEach(o -> options.addOption(o));
		
		// create the parser
		CommandLineParser parser = new DefaultParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args.getSourceArgs(), true);
			if (line.hasOption("help")) {
				formatter.printHelp(" ", options);
				System.exit(0);
				return;
			}
			if (line.hasOption("install")) {
				InstallScriptParameters params = buildInstallerParams(args, line);
				InstallScriptGenerator scriptGenerator = new InstallScriptGenerator();
				scriptGenerator.generateAll(params);
				System.exit(0);
				return;
			}
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			formatter.printHelp(" ", options);
		}
	}
	
	

	
	private static InstallScriptParameters buildInstallerParams(ApplicationArguments args, CommandLine line) {
		InstallScriptParameters result = InstallScriptParameters.getInstance();
		// Step 1 : parse with Apache command line
		for (Option anOption : line.getOptions()) {
			switch (anOption.getLongOpt()) {
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
				String urlParam = line.getOptionValue("managementServerURL");
				if (StringUtils.hasText(urlParam)) {
					result.addExternalOption("management.server.uri", urlParam);
				}
				break;
			case "port":
				String portParam = line.getOptionValue("port");
				if (StringUtils.hasText(portParam)) {
					result.addExternalOption("server.port", portParam);
				}
				break;
			default:
				break;
			}
		}
		// Step 2 : add additional options 
		for (String anOption : args.getOptionNames()) {
			if ("name".equals(anOption)) {
				result.setServiceName(args.getOptionValues("name").get(0));
				continue;
			}
			if ("install".equals(anOption)) {
				continue;
			}
			if ("managementServerURL".equals(anOption)) {
				continue;
			}
			if ("port".equals(anOption)) {
				continue;
			}
			result.addExternalOption(anOption, args.getOptionValues(anOption));
		}
		// Step 3 : fill with jvm args
		for (String anOption : args.getNonOptionArgs()) {
			if (!anOption.startsWith("-D")) {
				continue;
			}
			if (!anOption.contains("=")) {
				continue;
			}
			String substring = anOption.substring(2);
			String[] split = substring.split("=");
			result.addExternalOption(split[0], split[1]);
		}
		return result;
	}
	
	

}
