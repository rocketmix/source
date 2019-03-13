package com.essec.microservices;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class InstallConfiguration implements ApplicationRunner {

	@Value("classpath:/help.txt")
	private Resource resourceFile;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Set<String> optionNames = args.getOptionNames();
		List<String> nonOptionArgs = args.getNonOptionArgs();
		if (optionNames.contains("help") || nonOptionArgs.contains("-?")) {
			InputStream inputStream = this.getClass().getResourceAsStream("/help.txt");
			String result = new BufferedReader(new InputStreamReader(inputStream))
					  .lines().collect(Collectors.joining("\n"));
			inputStream.close();
			System.out.println(result);
			System.exit(0);
		}
		if (optionNames.contains("install")) {
			InstallScriptParameters params = buildInstallerParams(args);
			InstallScriptGenerator scriptGenerator = new InstallScriptGenerator();
			scriptGenerator.generateAll(params);
			System.exit(0);
		}
	}

	
	private static InstallScriptParameters buildInstallerParams(ApplicationArguments args) {
		InstallScriptParameters result = InstallScriptParameters.getInstance();
		for (String anOption : args.getOptionNames()) {
			switch (anOption) {
			case "name":
				result.setServiceName(args.getOptionValues("name").get(0));
			case "install":
				List<String> userParams = args.getOptionValues("install");
				if (userParams.size() == 1 && userParams.get(0).contains(":")) {
					String[] split = userParams.get(0).split(":");
					result.setUser(split[0]);
					result.setGroup(split[1]);
					break;
				}
				String username = System.getProperty("user.name");
				result.setUser(username);
				result.setGroup(username);
				break;
			case "managementServerURL":
				List<String> urlParams = args.getOptionValues("managementServerURL");
				if (urlParams.size() == 1) {
					result.addExternalOption("management.server.uri", urlParams.get(0));
				}
				break;
			case "port":
				List<String> portParams = args.getOptionValues("port");
				if (portParams.size() == 1) {
					result.addExternalOption("server.port", portParams.get(0));
				}
				break;
			default:
				result.addExternalOption(anOption, args.getOptionValues(anOption));
				break;
			}
		}
		for (String anOption : args.getNonOptionArgs()) {
			if (!anOption.startsWith("-D")) {
				continue;
			}
			if (!anOption.contains("=")) {
				continue;
			}
			String substring = anOption.substring(0, 2);
			String[] split = substring.split("=");
			result.addExternalOption(split[0], split[1]);
		}
		return result;
	}
	
	
}
