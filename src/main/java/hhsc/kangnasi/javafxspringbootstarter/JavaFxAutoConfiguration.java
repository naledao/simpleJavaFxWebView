package hhsc.kangnasi.javafxspringbootstarter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JavaFxAutoConfiguration.class)
@ConditionalOnProperty(prefix = "hhsc.kangnasi.javafx", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JavaFxAutoConfiguration {

    private final JavaFxAutoConfiguration javaFxprops;

    public JavaFxAutoConfiguration(JavaFxAutoConfiguration javaFxprops) {
        this.javaFxprops = javaFxprops;
    }

}
