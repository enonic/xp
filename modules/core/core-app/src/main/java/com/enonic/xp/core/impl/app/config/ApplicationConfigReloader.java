package com.enonic.xp.core.impl.app.config;

import java.util.Dictionary;

import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.ApplicationConfigService;

final class ApplicationConfigReloader
    implements ManagedService
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplicationConfigReloader.class );

    private static final Configuration EMPTY_CONFIG = ConfigBuilder.create().build();

    private final ApplicationKey key;

    private final ApplicationConfigService applicationConfigService;

    ApplicationConfigReloader( final ApplicationKey key, final ApplicationConfigService applicationConfigService )
    {
        this.key = key;
        this.applicationConfigService = applicationConfigService;
    }

    @Override
    public void updated( final Dictionary<String, ?> properties )
    {
        LOG.info( "Configuring application {}", this.key );

        this.applicationConfigService.setConfiguration( this.key, properties == null ? EMPTY_CONFIG : ConfigBuilder.create().
            addAll( properties ).build() );
    }
}
