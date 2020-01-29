package com.enonic.xp.core.impl.app.config;

import java.util.Dictionary;

import org.osgi.service.cm.ManagedService;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.ApplicationConfigService;

final class ApplicationConfigReloader
    implements ManagedService
{
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
        this.applicationConfigService.setConfiguration( this.key, properties == null ? EMPTY_CONFIG : ConfigBuilder.create().
            addAll( properties ).build() );
    }
}
