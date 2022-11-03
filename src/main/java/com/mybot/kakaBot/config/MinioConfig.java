package com.mybot.kakaBot.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xun
 * @create 2022/7/28 23:07
 */
@ConfigurationProperties(prefix = "app.minio")
@EnableConfigurationProperties
@Configuration
@Data
public class MinioConfig {
    @Value("${app.minio.endpoint}")
    private String endpoint;
    @Value("${app.minio.accessKey}")
    private String accessKey;
    @Value("${app.minio.secretKey}")
    private String secretKey;
    @Value("${app.minio.bucket}")
    private String bucket;


    /** 初始化 MinIO 客户端 */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    }

}
