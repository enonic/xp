package com.enonic.xp.core.impl.app.config;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.service.cm.ManagedService;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.ApplicationRegistry;

final class ApplicationConfigReloader
    implements ManagedService
{
    private static final Configuration EMPTY_CONFIG = ConfigBuilder.create().build();

    private final Bundle bundle;

    private final ApplicationRegistry applicationRegistry;

    ApplicationConfigReloader( final Bundle bundle, final ApplicationRegistry applicationRegistry )
    {
        this.bundle = bundle;
        this.applicationRegistry = applicationRegistry;
    }

    @Override
    public void updated( final Dictionary<String, ?> properties )
    {
        applicationRegistry.configure( bundle, properties == null ? EMPTY_CONFIG : ConfigBuilder.create().
            addAll( properties ).build() );
    }
}
