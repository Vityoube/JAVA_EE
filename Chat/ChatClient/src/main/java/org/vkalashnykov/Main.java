package org.vkalashnykov;

import com.caucho.hessian.client.HessianProxyFactory;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.vkalashnykov.configuration.FrameworkConfiguration;
import org.vkalashnykov.service.UserService;

import java.net.MalformedURLException;

public class Main extends Application {

    private double xOffset=0;
    private double yOffset=0;

    private ConfigurableApplicationContext context;
    private static String[] savedArgs;


    @Override
    public void init() throws Exception {
        context= SpringApplication.run(getClass(),savedArgs);
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    protected static void launchApp(Class<? extends Main> clazz, String[] args) {
        Main.savedArgs = args;
        Application.launch(clazz, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FrameworkConfiguration.configureFrameworks();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset=event.getSceneX();
                yOffset=event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX()-xOffset);
                primaryStage.setY(event.getScreenY()-yOffset);
            }
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launchApp(Main.class, args);
    }



}
