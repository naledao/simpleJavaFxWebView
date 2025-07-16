package hhsc.kangnasi.javafxspringbootstarter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Component
public class JavaFXApplication extends Application {

    @Autowired
    private JavaFxStarterProperties javaFxStarterProperties;

    private Stage splashStage;

    private Class<?> springBootClass;

    private String indexHtmlPath;

    private String startHtmlPath;

    private String icoPath;

    private String appName;

    @Override
    public void init() throws Exception {
        super.init();
        springBootClass=Class.forName(javaFxStarterProperties.getSpringBootClassPath());
        indexHtmlPath=javaFxStarterProperties.getIndexHtmlPath();
        startHtmlPath=javaFxStarterProperties.getStartHtmlPath();
        icoPath=javaFxStarterProperties.getIcoPath();
        appName=javaFxStarterProperties.getAppName();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 创建并显示启动窗口
        createSplashStage();
        splashStage.show();

        // 在后台线程启动 Spring Boot
        new Thread(() -> {
            try {
                SpringApplication app = new SpringApplication(springBootClass); // 我的需求：这边我想可以根据传进来的参数来确定
                // 添加监听器，当 Spring Boot 启动完成后执行
                app.addListeners((ApplicationListener<ApplicationReadyEvent>) event -> {
                    // 在 JavaFX 线程中关闭启动窗口并显示主窗口
                    Platform.runLater(() -> {
                        splashStage.close();
                        try {
                            configureMainStage(primaryStage);
                            primaryStage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                app.run(getParameters().getRaw().toArray(new String[0]));
            } catch (Exception e) {
                e.printStackTrace();
                // 启动失败时显示错误信息
                Platform.runLater(() -> {
                    splashStage.close();
                    showErrorAlert("Spring Boot 启动失败: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * 创建并显示启动窗口（Splash Screen）
     */
    private void createSplashStage() {
        splashStage = new Stage();

        // 创建 WebView 加载 HTML
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        try {
            engine.load(startHtmlPath);
        } catch (Exception e) {
            e.printStackTrace();
            webView.getEngine().loadContent(
                    "<h1 style='color:red;text-align:center'>启动界面加载失败</h1>"
            );
        }

        // 配置启动窗口 Scene 和 Stage
        Scene splashScene = new Scene(webView, 400, 300);
        splashStage.setScene(splashScene);
        splashStage.setTitle("系统启动中");
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setResizable(false);

        // 关键配置：窗口行为控制
        splashStage.setAlwaysOnTop(true); // 始终保持最前
        splashStage.setIconified(false);  // 初始非最小化状态

        // 禁用最小化事件
        splashStage.iconifiedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Platform.runLater(() -> splashStage.setIconified(false));
            }
        });

        // 禁用窗口失焦
        splashStage.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                Platform.runLater(() -> splashStage.requestFocus());
            }
        });

        // 设置图标
        try {
            URL iconUrl = getClass().getResource(icoPath);
            if (iconUrl != null) {
                Image icon = new Image(iconUrl.openStream());
                splashStage.getIcons().add(icon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 窗口位置和显示配置
        splashStage.centerOnScreen();
        splashStage.setOnShown(event -> {
            splashStage.toFront();      // 确保显示在最前
            splashStage.requestFocus(); // 保持焦点
        });
    }

    /**
     * 配置主窗口，并在运行期间实时监听分辨率 / 缩放变化，动态调整大小
     */
    private void configureMainStage(Stage mainStage) throws IOException {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load(indexHtmlPath);
        BorderPane root = new BorderPane();
        root.setCenter(webView);

        // 第一次获取屏幕“可见区域”
        Rectangle2D[] lastBounds = new Rectangle2D[1];
        lastBounds[0] = Screen.getPrimary().getVisualBounds();

        // 根据当前 bounds 按 80% 计算并设置舞台大小
        adjustStageSize(mainStage, lastBounds[0]);

        Scene scene = new Scene(root,
                lastBounds[0].getWidth() * 0.8,
                lastBounds[0].getHeight() * 0.8);
        mainStage.setTitle(appName);
        mainStage.setScene(scene);
        mainStage.setResizable(false);

        // 定时任务：每秒检查一次屏幕可见区域是否变化
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            Rectangle2D current = Screen.getPrimary().getVisualBounds();
            // 如果宽度或高度与上次不同，就说明分辨率或缩放改变了
            if (current.getWidth() != lastBounds[0].getWidth()
                    || current.getHeight() != lastBounds[0].getHeight()) {
                lastBounds[0] = current;
                Platform.runLater(() -> adjustStageSize(mainStage, current));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // 设置主窗口图标
        URL iconUrl = getClass().getResource(icoPath);
        if (iconUrl != null) {
            Image icon = new Image(iconUrl.openStream());
            mainStage.getIcons().add(icon);
        }

        // 关闭确认逻辑
        mainStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("请选择");
            alert.setHeaderText("你确定要退出吗？");

            // 设置对话框图标
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            URL dialogIconUrl = getClass().getResource(icoPath);
            if (dialogIconUrl != null) {
                try {
                    dialogStage.getIcons().add(new Image(dialogIconUrl.openStream()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            // 自定义“是的 / 取消”按钮
            ButtonType yesButton = new ButtonType("是的", ButtonBar.ButtonData.YES);
            ButtonType noButton  = new ButtonType("取消", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                System.exit(0);
            } else {
                event.consume(); // 取消关闭
            }
        });
    }

    /**
     * 根据给定的可视区域 bounds，按 80% 计算并调整舞台大小，最后居中窗口
     */
    private void adjustStageSize(Stage stage, Rectangle2D bounds) {
        double newWidth  = bounds.getWidth() * 0.8;
        double newHeight = bounds.getHeight() * 0.8;
        stage.setWidth(newWidth);
        stage.setHeight(newHeight);
        stage.centerOnScreen();
    }

    /**
     * 显示启动失败的错误对话框
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("启动失败");
        alert.setContentText(message);
        // 设置对话框图标
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        URL dialogIconUrl = getClass().getResource(icoPath);
        if (dialogIconUrl != null) {
            try {
                dialogStage.getIcons().add(new Image(dialogIconUrl.openStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        alert.showAndWait();
    }
}
