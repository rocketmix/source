package com.essec.microservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class InstallScriptGenerator {

	private static final String INSTALL_SCRIPT_TEMPLATE = "template/install-template.sh";
	private static final String UNINSTALL_SCRIPT_TEMPLATE = "template/uninstall-template.sh";
	private static final String SPRING_CONFIGURATION_FILE_TEMPLATE = "template/spring-configuration-template.conf";
	private static final String SYSTEMD_CONFIGURATION_TEMPLATE = "template/systemd-configuration-template.service";
	private static final String BANNER_FILE = "template/banner.txt";

	
	public void generateAll(InstallScriptParameters params) {
		try {
			checkPrerequisite(params);
			generateInstallScript(params);
			generateUninstallScript(params);
			generateConfigFile(params);
			generateSystemdFile(params);
			generateSymbolicLink(params);
			generatePropertiesFile(params);
			showResult(params);
		} catch (Exception e) {
			throw new RuntimeException("Error while generating install scripts", e);
		}
	}

	private void showResult(InstallScriptParameters params) {
		int maxFilenameLength = getMaxFilenameLength(params);
		System.out.println(" ");
		System.out.println("Install scripts generated in " + params.getInstallPath() + " !!!");
		System.out.println(" ");
		System.out.println("You should find in this directory :");
		System.out.println("* " + getFixedLengthString(params.getExecutableFile().getName(), maxFilenameLength) + " <-- main executable file");
		if (params.getExecutableFile() != null && !params.getSymbolicLinkFilename().equals(params.getExecutableFile().getName())) {
			System.out.println("* " + getFixedLengthString(params.getSymbolicLinkFilename(), maxFilenameLength) + " <-- symbolic link to executable file");
		}
		System.out.println("* " + getFixedLengthString(params.getSpringConfigurationFilename(), maxFilenameLength) + " <-- configuration options");
		if (params.isPropertiesFileNeeded()) {
			System.out.println("* " + getFixedLengthString(params.getPropertiesFilename(), maxFilenameLength) + " <-- configuration properties");
		}
		System.out.println("* " + getFixedLengthString(params.getSystemdFilename(), maxFilenameLength) + " <-- systemd service file");
		System.out.println("* " + getFixedLengthString(params.getInstallScriptFilename(), maxFilenameLength) + " <-- install script (run it AS ROOT to deploy this as a Linux service)");
		System.out.println("* " + getFixedLengthString(params.getUninstallScriptFilename(), maxFilenameLength) + " <-- uninstall script (run it AS ROOT to undeploy Linux service)");
		System.out.println(" ");
		System.out.println("You can adapt " + params.getSpringConfigurationFilename() + " and " + params.getSystemdFilename() + " as you need.");
		System.out.println(" ");
		System.out.println("Please run ./" + params.getInstallScriptFilename() + " AS ROOT (sudo or su-) to finish installation.");
		System.out.println(" ");
		System.out.println(" ");
	}

	private int getMaxFilenameLength(InstallScriptParameters params) {
		return Arrays.asList(params.getExecutableFile().getName(), params.getSymbolicLinkFilename(), params.getInstallScriptFilename(), params.getSpringConfigurationFilename(), params.getSystemdFilename()).stream()
				.max(Comparator.comparingInt(String::length)).get().length();
	}

	private void checkPrerequisite(InstallScriptParameters params) {
		File executableFile = params.getExecutableFile();
		if (executableFile == null || (executableFile != null && "".equals(executableFile.getName().trim()))) {
			throw new RuntimeException("Application doesn't seem to run from a war or a jar file. Cannot create scripts if it runs directly from class files");
		}
	}

	private void generateInstallScript(InstallScriptParameters params) throws Exception {
		StringBuilder content1 = new StringBuilder(loadFileTemplate(INSTALL_SCRIPT_TEMPLATE));
		replaceString(content1, "{{servicename}}", params.getServiceName());
		replaceString(content1, "{{installpath}}", params.getInstallPath());
		replaceString(content1, "{{uninstallscript}}", params.getUninstallScriptFilename());
		String banner = loadFileTemplate(BANNER_FILE);
		replaceString(content1, "{{banner}}", banner);
		Path path = Paths.get(params.getInstallScriptFilename());
		Files.write(path, content1.toString().getBytes());
		File file = path.toFile();
		file.setReadable(true, false);
		file.setWritable(true, false);
		file.setExecutable(true, false);
	}

	private void generateUninstallScript(InstallScriptParameters params) throws Exception {
		StringBuilder content1 = new StringBuilder(loadFileTemplate(UNINSTALL_SCRIPT_TEMPLATE));
		replaceString(content1, "{{servicename}}", params.getServiceName());
		replaceString(content1, "{{installpath}}", params.getInstallPath());
		replaceString(content1, "{{installscript}}", params.getInstallScriptFilename());
		String banner = loadFileTemplate(BANNER_FILE);
		replaceString(content1, "{{banner}}", banner);
		Path path = Paths.get(params.getUninstallScriptFilename());
		Files.write(path, content1.toString().getBytes());
		File file = path.toFile();
		file.setReadable(true, false);
		file.setWritable(true, false);
		file.setExecutable(true, false);
	}	
	
	private void generateConfigFile(InstallScriptParameters params) throws Exception {
		String content2 = loadFileTemplate(SPRING_CONFIGURATION_FILE_TEMPLATE);
		content2 = content2.replace("{{options}}", params.getOptionsString());
		Path path = Paths.get(params.getSpringConfigurationFilename());
		Files.write(path, content2.getBytes());
		File file = path.toFile();
		file.setReadable(true, false);
		file.setWritable(true, false);
	}
	
	private void generatePropertiesFile(InstallScriptParameters params) throws Exception {
		if (!params.isPropertiesFileNeeded()) {
			return;
		}
		Map<String, Object> externalOptions = params.getExternalOptions();
		if (externalOptions.isEmpty()) {
			return;
		}
		StringBuilder content = new StringBuilder();
		for (String anExternalOption : externalOptions.keySet()) {
			Object value = externalOptions.get(anExternalOption);
			if (value == null) {
				continue;
			}
			if (value.toString().length() == 0) {
				continue;
			}
			content.append(anExternalOption);
			content.append("=");
			content.append(value.toString());
			content.append("\n");
		}
		Path path = Paths.get(params.getPropertiesFilename());
		Files.write(path, content.toString().getBytes());
		File file = path.toFile();
		file.setReadable(true, false);
		file.setWritable(true, false);
	}
	
	
	
	
	
	
	private void generateSystemdFile(InstallScriptParameters params) throws Exception {
		final StringBuilder content3 = new StringBuilder(loadFileTemplate(SYSTEMD_CONFIGURATION_TEMPLATE));
		params.getUser().ifPresent(value -> replaceString(content3, "{{username}}", value));
		params.getGroup().ifPresent(value -> replaceString(content3, "{{groupname}}", value));
		replaceString(content3, "{{servicename}}", params.getServiceName());
		replaceString(content3, "{{installpath}}", params.getInstallPath());
		Path path = Paths.get(params.getSystemdFilename());
		Files.write(path, content3.toString().getBytes());
		File file = path.toFile();
		file.setReadable(true, false);
		file.setWritable(true, false);
	}

	private void generateSymbolicLink(InstallScriptParameters params) throws Exception {
		File executableFile = params.getExecutableFile();
		if (executableFile == null) {
			return;
		}
		String symbolicLinkFilename = params.getSymbolicLinkFilename();
		if (executableFile.getName().equals(symbolicLinkFilename)) {
			return; // Do not create if filename has no version number suffix
		}
		Path link = Paths.get(symbolicLinkFilename);
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

	/**
	 * Utility method to replace the string from StringBuilder.
	 * 
	 * @param sb
	 *            the StringBuilder object.
	 * @param toReplace
	 *            the String that should be replaced.
	 * @param replacement
	 *            the String that has to be replaced by.
	 * 
	 */
	private StringBuilder replaceString(StringBuilder sb, String toReplace, String replacement) {
		int index = -1;
		while ((index = sb.lastIndexOf(toReplace)) != -1) {
			sb.replace(index, index + toReplace.length(), replacement);
		}
		return sb;
	}

}
