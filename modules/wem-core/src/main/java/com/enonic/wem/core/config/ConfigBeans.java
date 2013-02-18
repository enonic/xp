package com.enonic.wem.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.enonic.wem.core.home.HomeDir;

@Configuration
public class ConfigBeans
{
    @Bean
    public ConfigProperties config( final HomeDir homeDir )
    {
        final ConfigLoader loader = new ConfigLoader( homeDir );
        return loader.load();
    }

    @Bean
    public SystemConfig systemConfig( final ConfigProperties properties )
    {
        return new SystemConfigImpl( properties );
    }
}
