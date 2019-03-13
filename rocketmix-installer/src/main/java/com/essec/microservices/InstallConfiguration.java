package com.essec.microservices;

import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class InstallConfiguration implements ApplicationRunner {

	
	@Autowired
	private ApplicationContext context;
	
	private static HelpFormatter formatter = new HelpFormatter();
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Map<String, Option> optionBeans = this.context.getBeansOfType(Option.class);
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

	@Bean
	public Option getHelpCommandLineOption() {
		return Option.builder("?").longOpt("help").required(false).hasArg(false).desc("Display this help").build();
	}
	
	
	@Bean
	public Option getInstallCommandLineOption() {
		return Option.builder().longOpt("install")
		.desc("Generate install scripts to deploy/undeploy as Linux SystemV service. You need to provide which Linux user/group will be used to run the service. You can combine this option with other options").longOpt("install")
		.hasArgs().optionalArg(true).numberOfArgs(2).argName("user:group").valueSeparator(':').build();
	}
	
	@Bean
	public Option getPortCommandLineOption() {
		return Option.builder().longOpt("port").argName("port").required(false).hasArg().optionalArg(false).desc("Change HTTP port (default: 8080)").build();
	}
	
	@Bean
	public Option getManagementServerURLCommandLineOption() {
		return Option.builder().longOpt("managementServerURL").argName("url").required(false).hasArg().optionalArg(false).desc("Set URL (with network port) of the management server (default: http://127.0.0.1:8761, used when management server is executed on the same machine)").build();
	}
	
	
}
