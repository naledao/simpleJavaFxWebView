package hhsc.kangnasi.javafxspringbootstarter;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavafxSpringBootStarterApplication {

    public static void main(String[] args) {
        Application.launch(JavaFXApplication.class,args);
    }
}
