package gradle.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Druid 连接池配置参数
 *
 * @author by JingQ on 2018/2/7
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@PropertySource("classpath:application.properties")
@Data
public class DruidConfigBean {

    private Integer initialSize;

    private Integer minIdle;

    private Integer maxActive;

    private Integer maxWait;

    private Integer timeBetweenEvictionRunsMillis;

    private Integer minEvictableIdleTimeMillis;

    private String validationQuery;

    private Boolean testWhileIdle;

    private Boolean testOnBorrow;

    private Boolean testOnReturn;

    private Boolean poolPreparedStatements;
}
