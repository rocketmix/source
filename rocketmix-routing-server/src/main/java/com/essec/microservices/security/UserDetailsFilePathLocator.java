package com.essec.microservices.security;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class UserDetailsFilePathLocator {


	private String installPath;
	
	private static final UserDetailsFilePathLocator instance = new UserDetailsFilePathLocator();
	
	private UserDetailsFilePathLocator() {
		// Singleton
	}
	
	public static UserDetailsFilePathLocator getInstance() {
		return instance;
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