package config;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

class YamlFileApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		addYamlPropertiesToSpringEnvironment(applicationContext);
	}

	private void addYamlPropertiesToSpringEnvironment(ConfigurableApplicationContext applicationContext) {
		try {
			Resource resource = applicationContext.getResource("classpath:default-application.yml");
			YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
			List<PropertySource<?>> yamlProperties = yamlPropertySourceLoader.load("default", resource);
			yamlProperties.forEach(p -> applicationContext.getEnvironment().getPropertySources().addLast(p));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load resource file", e);
		}
	}
}