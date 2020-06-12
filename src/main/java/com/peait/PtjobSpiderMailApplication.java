package com.peait;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
//@EnableEurekaClient
@EnableScheduling
public class PtjobSpiderMailApplication {

    public static void main(String[] args) {
        SpringApplication.run(PtjobSpiderMailApplication.class, args);
    }

}
