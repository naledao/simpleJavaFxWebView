package hhsc.kangnasi.javafxspringbootstarter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hhsc.kangnasi.javafx")
public class JavaFxStarterProperties {

    private String springBootClassPath;

    private String indexHtmlPath;

    private String startHtmlPath;

    private String icoPath;

    private String appName;

}
