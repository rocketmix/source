package com.essec.microservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
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
			showResult(params);
		} catch (Exception e) {
			throw new RuntimeException("Error while generating install scripts", e);
		}
	}

	private void showResult(InstallScriptParameters params) {
		int maxFilenameLength = getMaxFilenameLength(params);
		System.out.println("Install scripts generated in " + params.getInstallPath() + " !!!");
		System.out.println("You should find in this directory :");
		System.out.println("* " + getFixedLengthString(params.getExecutableFile().getName(), maxFilenameLength) + " <-- main executable file");
		if (params.getExecutableFile() != null && !params.getSymbolicLinkFilename().equals(params.getExecutableFile().getName())) {
			System.out.println("* " + getFixedLengthString(params.getSymbolicLinkFilename(), maxFilenameLength) + " <-- symbolic link to executable file");
		}
		System.out.println("* " + getFixedLengthString(params.getSpringConfigurationFilename(), maxFilenameLength) + " <-- configuration options");
		System.out.println("* " + getFixedLengthString(params.getSystemdFilename(), maxFilenameLength) + " <-- systemd service file");
		System.out.println("* " + getFixedLengthString(params.getInstallScriptFilename(), maxFilenameLength) + " <-- install script (to execute AS ROOT to finish install)");
		System.out.println("You can adapt " + params.getSpringConfigurationFilename() + " and " + params.getSystemdFilename() + " as you need.");
		System.out.println("Please run " + params.getInstallScriptFilename() + " AS ROOT(!) to finish installation.");
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
		String banner = loadFileTemplate(BANNER_FILE);
		replaceString(content1, "{{banner}}", banner);
		Files.write(Paths.get(params.getInstallScriptFilename()), content1.toString().getBytes());
	}

	private void generateUninstallScript(InstallScriptParameters params) throws Exception {
		StringBuilder content1 = new StringBuilder(loadFileTemplate(UNINSTALL_SCRIPT_TEMPLATE));
		replaceString(content1, "{{servicename}}", params.getServiceName());
		replaceString(content1, "{{installpath}}", params.getInstallPath());
		String banner = loadFileTemplate(BANNER_FILE);
		replaceString(content1, "{{banner}}", banner);
		Files.write(Paths.get(params.getUninstallScriptFilename()), content1.toString().getBytes());
	}	
	
	private void generateConfigFile(InstallScriptParameters params) throws Exception {
		String content2 = loadFileTemplate(SPRING_CONFIGURATION_FILE_TEMPLATE);
		content2 = content2.replace("{{options}}", params.getOptionsString());
		Files.write(Paths.get(params.getSpringConfigurationFilename()), content2.getBytes());
	}

	private void generateSystemdFile(InstallScriptParameters params) throws Exception {
		final StringBuilder content3 = new StringBuilder(loadFileTemplate(SYSTEMD_CONFIGURATION_TEMPLATE));
		params.getUser().ifPresent(value -> replaceString(content3, "{{username}}", value));
		params.getGroup().ifPresent(value -> replaceString(content3, "{{groupname}}", value));
		replaceString(content3, "{{servicename}}", params.getServiceName());
		replaceString(content3, "{{installpath}}", params.getInstallPath());
		Files.write(Paths.get(params.getSystemdFilename()), content3.toString().getBytes());
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
