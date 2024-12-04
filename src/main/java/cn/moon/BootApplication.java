package cn.moon;

import cn.moon.lang.web.SpringTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.*;
import java.net.URI;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableScheduling
public class BootApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BootApplication.class, args);
        String url = "http://127.0.0.1:" + SpringTool.getProperty("server.port");

        log.info("登录地址");
        log.info(url);

        if(Desktop.isDesktopSupported()){
            Desktop.getDesktop().browse(URI.create(url));
        }
    }

}
