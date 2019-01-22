package com.essec.microservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstallScriptGenerator {

	
	
	public void generateInstallScript(InstallScriptParameters params) {
		try {
			String content1 = loadFileTemplate("/installer/rocketmix-routing-server-install.sh");
			Files.write(Paths.get("rocketmix-routing-server-install.sh"), content1.getBytes());
			String content2 = loadFileTemplate("/installer/rocketmix-routing-server-install.conf");
			content2.replace("{{options}}", params.getOptionsString());
			Files.write(Paths.get("rocketmix-routing-server-install.conf"), content2.getBytes());
			String content3 = loadFileTemplate("/installer/rocketmix-routing-server-install.service");
			params.user.ifPresent(value -> content3.replace("{{username}}", value));
			params.group.ifPresent(value -> content3.replace("{{groupname}}", value));
			Files.write(Paths.get("rocketmix-routing-server-install.service"), content3.getBytes());
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
	
	public class InstallScriptParameters {

		private Optional<String> user;
		private Optional<String> group;
		private Optional<String> companyName;
		private Optional<String> logoURL;
		private Optional<Integer> serverPort;
		private Optional<String> managementServerURL;

		

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
		
	}

}
