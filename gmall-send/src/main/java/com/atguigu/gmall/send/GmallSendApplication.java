package com.atguigu.gmall.send;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GmallSendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallSendApplication.class, args);
    }

}
