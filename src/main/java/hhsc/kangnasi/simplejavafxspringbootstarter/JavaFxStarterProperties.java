package hhsc.kangnasi.simplejavafxspringbootstarter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "hhsc.kangnasi.simple-javafx-webview")
public class JavaFxStarterProperties {

    private String springBootClassPath;

    private String indexHtmlPath;

    private String startHtmlPath;

    private String icoPath;

    private String appName;

    private boolean resizable=true;
}
