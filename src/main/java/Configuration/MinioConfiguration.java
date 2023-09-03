package Configuration;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class MinioConfiguration {

    // Getter pour endpoint
    @Value("${minio.endpoint}")
    private String endpoint;

    // Getter pour accessKey
    @Value("${minio.accessKey}")
    private String accessKey;

    // Getter pour secretKey
    @Value("${minio.secretKey}")
    private String secretKey;


    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}

