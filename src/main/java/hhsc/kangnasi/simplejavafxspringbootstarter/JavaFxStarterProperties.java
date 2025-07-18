package hhsc.kangnasi.simplejavafxspringbootstarter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "hhsc.kangnasi.simple-javafx-webview")
public class JavaFxStarterProperties {

    private OuterSpringBoot outerSpringBoot=new OuterSpringBoot();

    private App app=new App();
    @Data
    public static class OuterSpringBoot{
        private String classPath;
    }

    @Data
    public static class App{

        private String appName;

        private String icoPath;

        private String startHtmlPath;

        private String indexHtmlPath;

        private boolean resizable=true;

        private double widthScale=0.8;

        private double heightScale=0.8;
    }
}
