package com.essec.microservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstallScriptGenerator {

	private static final String APP_NAME = "rocketmix-routing-server";
	private static final String EXECUTABLE_FILE_NAME = APP_NAME + ".war";
	private static final String INSTALL_SCRIPT_NAME = APP_NAME + "-install.sh";
	private static final String CONFIG_FILE_NAME = APP_NAME + ".conf";
	private static final String SYSTEMD_FILE_NAME = APP_NAME + ".service";
	private static final int MAX_FILE_NAME_LENGTH = Arrays.asList(EXECUTABLE_FILE_NAME, INSTALL_SCRIPT_NAME, CONFIG_FILE_NAME, SYSTEMD_FILE_NAME).stream().max(Comparator.comparingInt(String::length)).get().length();

	public void generateAll(InstallScriptParameters params) {
		try {
			generateInstallScript(params);
			generateConfigFile(params);
			generateSystemdFile(params);
			generateSymbolicLink(params);
			System.out.println("Install scripts generated in " + params.getInstallPath() + " !!!");
			System.out.println("You should find in this directory :");
			if (params.getExecutableFile() != null) {
				System.out.println("* " + params.getExecutableFile().getName() + " <-- main executable file");
				System.out.println("* " + getFixedLengthString(EXECUTABLE_FILE_NAME, MAX_FILE_NAME_LENGTH) + " <-- symbolic link to executable file");
			}
			System.out.println("* " + getFixedLengthString(CONFIG_FILE_NAME, MAX_FILE_NAME_LENGTH) + " <-- configuration options");
			System.out.println("* " + getFixedLengthString(SYSTEMD_FILE_NAME, MAX_FILE_NAME_LENGTH) + " <-- systemd service file");
			System.out.println("* " + getFixedLengthString(INSTALL_SCRIPT_NAME, MAX_FILE_NAME_LENGTH) + " <-- install script (to execute AS ROOT to finish install)");
			System.out.println("You can adapt " + CONFIG_FILE_NAME + " and " + SYSTEMD_FILE_NAME + " is you need.");
			System.out.println("Please run " + INSTALL_SCRIPT_NAME + " AS ROOT(!) to finish installation.");
		} catch (Exception e) {
			throw new RuntimeException("Error while generating install scripts", e);
		}
	}
	
	private void generateInstallScript(InstallScriptParameters params) throws Exception {
		StringBuilder content1 = new StringBuilder(loadFileTemplate("installer/" + INSTALL_SCRIPT_NAME));
		content1.replace(content1.indexOf("{{installpath}}"), content1.indexOf("{{installpath}}") + "{{installpath}}".length(), params.getInstallPath());
		Files.write(Paths.get(INSTALL_SCRIPT_NAME), content1.toString().getBytes());
	}
	
	private void generateConfigFile(InstallScriptParameters params) throws Exception {
		String content2 = loadFileTemplate("installer/" + CONFIG_FILE_NAME);
		content2 = content2.replace("{{options}}", params.getOptionsString());
		Files.write(Paths.get(CONFIG_FILE_NAME), content2.getBytes());
	}
	
	private void generateSystemdFile(InstallScriptParameters params) throws Exception {
		StringBuilder content3 = new StringBuilder(loadFileTemplate("installer/" + SYSTEMD_FILE_NAME));
		params.user.ifPresent(value -> content3.replace(content3.indexOf("{{username}}"), content3.indexOf("{{username}}") + "{{username}}".length(), value));
		params.group.ifPresent(value -> content3.replace(content3.indexOf("{{groupname}}"), content3.indexOf("{{groupname}}") + "{{groupname}}".length(), value));
		content3.replace(content3.indexOf("{{installpath}}"), content3.indexOf("{{installpath}}") + "{{installpath}}".length(), params.getInstallPath());
		Files.write(Paths.get(SYSTEMD_FILE_NAME), content3.toString().getBytes());
	}
	
	
	private void generateSymbolicLink(InstallScriptParameters params) throws Exception {
		File executableFile = params.getExecutableFile();
		if (executableFile == null) {
			return;
		}
		Path link = Paths.get(params.getInstallPath(),EXECUTABLE_FILE_NAME);
		if (Files.exists(link)) {
			Files.delete(link);
		}
		Files.createSymbolicLink(link, executableFile.toPath());
	}
	

	private String getFixedLengthString(String text, int length) {
		return String.format("%-" + length + "." + length + "s", text);
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
		
		public File getExecutableFile() {
			try {
				ProtectionDomain domain = this.getClass().getProtectionDomain();
				CodeSource codeSource = domain.getCodeSource();
				URL sourceLocation = codeSource.getLocation();
				String path = sourceLocation.getPath();
				URI uri = new URI(path);
				// Case 1 : current class is in a directory
				if (uri.getScheme() == null) {
					return null;
				}
				// Case 2 : current class is in a jar file
				// We get current path and go back from root dir to jar file
				path = uri.getPath();
				path = path.replace("!", "");
				String[] pathElements = path.split(File.separator);
				String jarPath = "";
				for (String aPathElement : pathElements) {
					if (aPathElement == null || "".equals(aPathElement)) {
						continue;
					}
					jarPath = jarPath + File.separator + aPathElement;
					File pathChecker = new File(jarPath);
					if (!pathChecker.exists()) {
						throw new RuntimeException("Unable to locate executable file. Inconsistent path : " + path);
					}
					if (pathChecker.isFile()) {
						return pathChecker;
					}
				}
				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		

		public String getInstallPath() {
			try {
				ProtectionDomain domain = this.getClass().getProtectionDomain();
				CodeSource codeSource = domain.getCodeSource();
				URL sourceLocation = codeSource.getLocation();
				String path = sourceLocation.getPath();
				URI uri = new URI(path);
				// Case 1 : current class is in a directory
				if (uri.getScheme() == null) {
					return path; // Local file
				}
				// Case 2 : current class is in a jar file
				// We get current path and go back from root dir to jar file
				path = uri.getPath();
				path = path.replace("!", "");
				String[] pathElements = path.split(File.separator);
				String jarPath = "";
				for (String aPathElement : pathElements) {
					if (aPathElement == null || "".equals(aPathElement)) {
						continue;
					}
					jarPath = jarPath + File.separator + aPathElement;
					File pathChecker = new File(jarPath);
					if (!pathChecker.exists()) {
						throw new RuntimeException("Unable to locate executable file. Inconsistent path : " + path);
					}
					if (pathChecker.isFile()) {
						return pathChecker.getParent();
					}
				}
				throw new RuntimeException("Unable to locate executable file. Inconsistent path : " + path);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

}
