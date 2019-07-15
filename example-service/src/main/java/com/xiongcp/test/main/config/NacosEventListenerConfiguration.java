package com.xiongcp.test.main.config;


import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.context.event.config.NacosConfigListenerRegisteredEvent;
import com.alibaba.nacos.spring.context.event.config.NacosConfigPublishedEvent;
import com.alibaba.nacos.spring.context.event.config.NacosConfigReceivedEvent;
import com.alibaba.nacos.spring.context.event.config.NacosConfigRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
import static com.xiongcp.test.main.config.NacosPropertySourceConfiguration.*;

@Configuration
public class NacosEventListenerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(NacosEventListenerConfiguration.class);


    @NacosInjected
    private ConfigService configService;


    @PostConstruct
    public void init() throws NacosException {

        Listener listener = new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
            }
        };

        // for NacosConfigListenerRegisteredEvent(true)
        //
        configService.addListener(EXAMPLE, DEFAULT_GROUP, listener);
        /*configService.addListener(FIRST_DATA_ID, DEFAULT_GROUP, listener);
        configService.addListener(BEFORE_OS_ENV_DATA_ID, DEFAULT_GROUP, listener);
        configService.addListener(AFTER_SYS_PROP_DATA_ID, DEFAULT_GROUP, listener);*/

        // for NacosConfigListenerRegisteredEvent(false)
        //configService.removeListener("example", DEFAULT_GROUP, listener);
    }


    @Bean
    public ApplicationListener<NacosConfigReceivedEvent> nacosConfigReceivedEventListener() {
        return new ApplicationListener<NacosConfigReceivedEvent>() {
            @Override
            public void onApplicationEvent(NacosConfigReceivedEvent event) {
                logger.info("Listening on NacosConfigReceivedEvent -  dataId : {} , groupId : {} , " + "content : {} , "
                        + "source : {}", event.getDataId(), event.getGroupId(), event.getContent(), event.getSource());
            }
        };
    }

    @Bean
    public ApplicationListener<NacosConfigRemovedEvent> nacosConfigRemovedEventListener() {
        return new ApplicationListener<NacosConfigRemovedEvent>() {
            @Override
            public void onApplicationEvent(NacosConfigRemovedEvent event) {
                logger.info("Listening on NacosConfigRemovedEvent -  dataId : {} , groupId : {} , " + "removed : {} , "
                        + "source : {}", event.getDataId(), event.getGroupId(), event.isRemoved(), event.getSource());
            }
        };
    }

    @Bean
    public ApplicationListener<NacosConfigListenerRegisteredEvent> nacosConfigListenerRegisteredEventListener() {
        return new ApplicationListener<NacosConfigListenerRegisteredEvent>() {
            @Override
            public void onApplicationEvent(NacosConfigListenerRegisteredEvent event) {
                logger.info("Listening on NacosConfigListenerRegisteredEvent -  dataId : {} , groupId : {} , " + "registered : {} , "
                        + "source : {}", event.getDataId(), event.getGroupId(), event.isRegistered(), event.getSource());
            }
        };
    }

    @Bean
    public ApplicationListener<NacosConfigPublishedEvent> nacosConfigPublishedEvent() {
        return new ApplicationListener<NacosConfigPublishedEvent>() {
            @Override
            public void onApplicationEvent(NacosConfigPublishedEvent event) {
                logger.info("Listening on NacosConfigPublishedEvent -  dataId : {} , groupId : {} , " + "published : {} , "
                        + "source : {}", event.getDataId(), event.getGroupId(), event.isPublished(), event.getSource());
            }
        };
    }
}
