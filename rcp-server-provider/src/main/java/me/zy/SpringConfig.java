package me.zy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring注解的类被Spring容器加载
 * since 1.1
 */
@Configuration
@ComponentScan(basePackages = "me.zy")
public class SpringConfig {
    @Bean(name="zyRpcServer")
    public ZyRpcServer zyRpcServer(){
        return new ZyRpcServer(8080);
    }
}
