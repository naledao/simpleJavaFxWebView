package hhsc.kangnasi.simplejavafxspringbootstarter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JavaFxStarterProperties.class)
@ConditionalOnProperty(prefix = "hhsc.kangnasi.simple-javafx-webview", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JavaFxAutoConfiguration {

    private final JavaFxStarterProperties javaFxprops;

    public JavaFxAutoConfiguration(JavaFxStarterProperties javaFxStarterProperties) {
        this.javaFxprops = javaFxStarterProperties;
    }

}
