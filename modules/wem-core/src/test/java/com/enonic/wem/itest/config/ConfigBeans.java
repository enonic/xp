package com.enonic.wem.itest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.enonic.cms.core.home.HomeDir;

@Configuration
public class ConfigBeans
{
    @Bean
    public ConfigProperties config( final HomeDir homeDir )
    {
        final ConfigLoader loader = new ConfigLoader( homeDir );
        return loader.load();
    }
}
