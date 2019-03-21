package com.essec.microservices;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InstallScriptParameters {

	private static final String SYMBOLIC_LINK_FILENAME = "{0}.war";
	private static final String INSTALL_SCRIPT_FILENAME = "{0}-install.sh";
	private static final String UNINSTALL_SCRIPT_FILENAME = "{0}-uninstall.sh";
	private static final String START_SCRIPT_FILENAME = "{0}-start.sh";
	private static final String STOP_SCRIPT_FILENAME = "{0}-stop.sh";
	private static final String STATUS_SCRIPT_FILENAME = "{0}-status.sh";
	private static final String SPRING_CONFIGURATION_FILENAME = "{0}.conf";
	private static final String SYSTEMD_FILENAME = "{0}.service";
	private static final String PROPERTIES_FILENAME = "{0}.properties";
	
	private String serviceName;
	private Optional<String> user = Optional.empty();
	private Optional<String> group = Optional.empty();
	private Integer debugPort;
	private Map<String, List<String>> programOptions = new HashMap<>();
	private List<String> jvmOptions = new ArrayList<>();
	
	private String installPath;
	private File executableFile;
	
	private static final InstallScriptParameters instance = new InstallScriptParameters();
	
	private InstallScriptParameters() {
		// Singleton
	}
	
	public static InstallScriptParameters getInstance() {
		return instance;
	}
	
	public String getServiceName() {
		if (this.serviceName == null) {
			File executableFile = getExecutableFile();
			if (executableFile == null || (executableFile != null && "".equals(executableFile.getName().trim()))) {
				throw new RuntimeException("Application doesn't seem to run from a war or a jar file. Cannot create scripts if it runs directly from class files");
			}
			String name = executableFile.getName();
			name = name.replaceFirst("(?i)-([0-9.\\-]+)(-SNAPSHOT|-RELEASE)*\\.(war|jar)$", "");
			name = name.trim();
			this.serviceName = name;
		}
		return this.serviceName;
	}

	public String getInstallScriptFilename() {
		return MessageFormat.format(INSTALL_SCRIPT_FILENAME, getServiceName());
	}

	public String getUninstallScriptFilename() {
		return MessageFormat.format(UNINSTALL_SCRIPT_FILENAME, getServiceName());
	}

	public String getStartScriptFilename() {
		return MessageFormat.format(START_SCRIPT_FILENAME, getServiceName());
	}
	
	public String getStopScriptFilename() {
		return MessageFormat.format(STOP_SCRIPT_FILENAME, getServiceName());
	}
	
	public String getStatusScriptFilename() {
		return MessageFormat.format(STATUS_SCRIPT_FILENAME, getServiceName());
	}
	
	public String getSpringConfigurationFilename() {
		return MessageFormat.format(SPRING_CONFIGURATION_FILENAME, getServiceName());
	}

	public String getSystemdFilename() {
		return MessageFormat.format(SYSTEMD_FILENAME, getServiceName());
	}

	public String getPropertiesFilename() {
		return MessageFormat.format(PROPERTIES_FILENAME, getServiceName());
	}
	
	public String getExecutableLinkFilename() {
		return MessageFormat.format(SYMBOLIC_LINK_FILENAME, getServiceName());
	}
	
	public void setUser(String user) {
		this.user = Optional.of(user);
	}
	
	public Optional<String> getUser() {
		return user;
	}

	public void setGroup(String group) {
		this.group = Optional.of(group);
	}
	
	public Optional<String> getGroup() {
		return group;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public void addProgramOption(String argName, List<String> values) {
		this.programOptions.put(argName, values);
	}
	
	public void addProgramOption(String argName, String value) {
		this.programOptions.put(argName, Arrays.asList(value));
	}
	
	public void addJvmOption(String... jvmOption) {
		this.jvmOptions.addAll(Arrays.asList(jvmOption));
	}
	
	public void setDebugPort(Integer debugPort) {
		this.debugPort = debugPort;
	}
	
	public String getJvmOptionsString() {
		StringBuilder builder = new StringBuilder();
		for (String option : jvmOptions) {
			builder.append(option);
			builder.append(" ");
		}
		builder.append("-Dspring.config.additional-location=file:./").append(getServiceName()).append(".properties");
		if (this.debugPort != null) {
			builder.append(" -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=").append(this.debugPort);
		}
		return builder.toString();
	}
	
	public boolean isPropertiesFileNeeded() {
		return !this.programOptions.isEmpty();
	}
	
	public Map<String, List<String>> getProgramOptions() {
		return this.programOptions;
	}
	
	
	public File getExecutableFile() {
		if (this.executableFile == null) {
			try {
				ProtectionDomain domain = this.getClass().getProtectionDomain();
				CodeSource codeSource = domain.getCodeSource();
				URL sourceLocation = codeSource.getLocation();
				String path = sourceLocation.getPath();
				URI uri = new URI(path);
				// Case 1 : current class is in a directory
				if (uri.getScheme() == null) {
					this.executableFile = new File(sourceLocation.toURI());
					return this.executableFile;
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
						this.executableFile = pathChecker;
						return this.executableFile;
					}
				}
				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return this.executableFile;
	}
	

	public String getInstallPath() {
		if (this.installPath == null) {
			try {
				ProtectionDomain domain = this.getClass().getProtectionDomain();
				CodeSource codeSource = domain.getCodeSource();
				URL sourceLocation = codeSource.getLocation();
				String path = sourceLocation.getPath();
				URI uri = new URI(path);
				// Case 1 : current class is in a directory
				if (uri.getScheme() == null) {
					this.installPath = path; // Local file
					return this.installPath;
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
						this.installPath = pathChecker.getParent();
						return this.installPath;
					}
				}
				throw new RuntimeException("Unable to locate executable file. Inconsistent path : " + path);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return this.installPath; 
	}

}