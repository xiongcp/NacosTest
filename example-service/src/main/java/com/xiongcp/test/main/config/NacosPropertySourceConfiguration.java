package com.xiongcp.test.main.config;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
import static com.xiongcp.test.main.config.NacosPropertySourceConfiguration.*;
import static org.springframework.core.env.StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
import static org.springframework.core.env.StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME;

/*@Configuration
@NacosPropertySource(name = "first", dataId = FIRST_DATA_ID, first = true, autoRefreshed = true)
//dataId类似于key-values的key，用唯一的dataId可以去注册一个唯一的config信息
@NacosPropertySources({
        @NacosPropertySource(name = "before-os-env", dataId = BEFORE_OS_ENV_DATA_ID, before = SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME),
        @NacosPropertySource(name = "after-system-properties", dataId = AFTER_SYS_PROP_DATA_ID, after = SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)
})*/
public class NacosPropertySourceConfiguration {



    private static final Logger logger = LoggerFactory.getLogger(NacosPropertySourceConfiguration.class);

    public static final String FIRST_DATA_ID = "first-property-source-data-id";

    public static final String BEFORE_OS_ENV_DATA_ID = "before-os-env-property-source-data-id";

    public static final String AFTER_SYS_PROP_DATA_ID = "after-system-properties-property-source-data-id";

    public static final String EXAMPLE = "example";


    static {
        String serverAddr = "192.168.253.128:8848";
        try {
            //创建Nacos的ConfigService
            ConfigService configService = NacosFactory.createConfigService(serverAddr);
            //发布到Nacos
            // Publish for FIRST_DATA_ID
            publishConfig(configService, FIRST_DATA_ID, "user.name = Mercy Ma");

            // Publish for BEFORE_OS_ENV_DATA_ID
            publishConfig(configService, BEFORE_OS_ENV_DATA_ID, "path = /home/my-path");


            // Publish for AFTER_SYS_PROP_DATA_ID
            publishConfig(configService, AFTER_SYS_PROP_DATA_ID, "user.name = mercyblitz");

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注册配置
     * @param configService
     * @param dataId
     * @param propertiesContent
     * @throws NacosException
     */
    private static void publishConfig(ConfigService configService, String dataId, String propertiesContent) throws NacosException {
        configService.publishConfig(dataId, DEFAULT_GROUP, propertiesContent);
    }

    /**
     * "before-os-env" overrides OS Environment variables $PATH
     * @Value() 读取的yml的配置文件的值
     */
    @Value("${path}")
    private String path;

    /**
     * There are three definitions of "user.name" from
     * FIRST_DATA_ID,
     * SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
     * AFTER_SYS_PROP_DATA_ID
     * <p>
     * Thus, "user.name = Mercy Ma" will be loaded from FIRST_DATA_ID, others will be ignored.
     */
    @Value("${user.name}")
    private String userName;

    @PostConstruct
    public void init() {
        logger.info("${path} : {}", path); // -> "home/my-path"
        logger.info("${user.name} : {}", userName); // -> "Mercy Ma"
        logger.info("Java System ${user.name} : {}", System.getProperty("user.name"));
        logger.info("OS Env ${PATH} : {}", System.getenv("PATH"));
    }
}
