package config;

import org.apache.commons.cli.Option;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.essec.microservices.InstallBootstrap;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(PropertyPlaceholderAutoConfiguration.class)
public class InstallAutoConfiguration { 

	@Bean
	public InstallBootstrap getInstallTrigger() {
		return new InstallBootstrap();
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
