package com.essec.microservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstallScriptGenerator {

	
	
	public void generateInstallScript(InstallScriptParameters params) {
		try {
			StringBuilder content1 = new StringBuilder(loadFileTemplate("installer/rocketmix-routing-server-install.sh"));
			content1.replace(content1.indexOf("{{installpath}}"), content1.indexOf("{{installpath}}") + "{{installpath}}".length(), params.getInstallPath());
			Files.write(Paths.get("rocketmix-routing-server-install.sh"), content1.toString().getBytes());
			String content2 = loadFileTemplate("installer/rocketmix-routing-server.conf");
			content2 = content2.replace("{{options}}", params.getOptionsString());
			Files.write(Paths.get("rocketmix-routing-server.conf"), content2.getBytes());
			StringBuilder content3 = new StringBuilder(loadFileTemplate("installer/rocketmix-routing-server.service"));
			params.user.ifPresent(value -> content3.replace(content3.indexOf("{{username}}"), content3.indexOf("{{username}}") + "{{username}}".length(), value));
			params.group.ifPresent(value -> content3.replace(content3.indexOf("{{groupname}}"), content3.indexOf("{{groupname}}") + "{{groupname}}".length(), value));
			content3.replace(content3.indexOf("{{installpath}}"), content3.indexOf("{{installpath}}") + "{{installpath}}".length(), params.getInstallPath());
			Files.write(Paths.get("rocketmix-routing-server.service"), content3.toString().getBytes());
			System.out.println("Install scripts generated in the current directory!!!");
			System.out.println("You can adapt rocketmix-routing-server.conf and rocketmix-routing-server.service is you need.");
			System.out.println("Please run (AS ROOT!) rocketmix-routing-server-install.sh to finish installation.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String loadFileTemplate(String filepath) throws IOException {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filepath);
		return convert(inputStream, Charset.defaultCharset());
	}

	private String convert(InputStream inputStream, Charset charset) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}
	
	public static class InstallScriptParameters {

		private Optional<String> user = Optional.empty();
		private Optional<String> group = Optional.empty();
		private Optional<String> companyName = Optional.empty(); 
		private Optional<String> logoURL = Optional.empty();
		private Optional<Integer> serverPort = Optional.empty();
		private Optional<String> managementServerURL = Optional.empty();

		public void setUser(String user) {
			this.user = Optional.of(user);
		}



		public void setGroup(String group) {
			this.group = Optional.of(group);
		}



		public void setCompanyName(String companyName) {
			this.companyName = Optional.of(companyName);
		}



		public void setLogoURL(String logoURL) {
			this.logoURL = Optional.of(logoURL);
		}



		public void setServerPort(Integer serverPort) {
			this.serverPort = Optional.of(serverPort);
		}



		public void setManagementServerURL(String managementServerURL) {
			this.managementServerURL = Optional.of(managementServerURL);
		}



		public String getOptionsString() {
			StringBuilder builder = new StringBuilder();
			companyName.ifPresent(value -> builder.append("-Dportal.companyName=\"").append(value).append("\""));
			logoURL.ifPresent(value -> builder.append("-Dportal.logoURL=\"").append(value));
			serverPort.ifPresent(value -> builder.append("-Dserver.port=").append(value));
			managementServerURL.ifPresent(value -> builder.append("-Deureka.server.uri=").append(value));
			return builder.toString();
		}
		
		
		
		public String getInstallPath() {
			try {
				CodeSource codeSource = InstallScriptGenerator.class.getProtectionDomain().getCodeSource();
				File jarFile = new File(codeSource.getLocation().toURI().getPath());
				String jarDir = jarFile.getParentFile().getPath();
				return jarDir;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}

}
