package com.enonic.xp.core.impl.app.config;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;

final class ApplicationConfigReloader
    implements ManagedService
{
    private final ApplicationKey key;

    private final ApplicationService service;

    ApplicationConfigReloader( final ApplicationKey key, final ApplicationService service )
    {
        this.key = key;
        this.service = service;
    }

    @Override
    public void updated( final Dictionary<String, ?> properties )
        throws ConfigurationException
    {
        if ( properties == null )
        {
            ApplicationConfigMap.INSTANCE.remove( this.key );
        }
        else
        {

            final Configuration config = ConfigBuilder.create().
                addAll( properties ).build();

            ApplicationConfigMap.INSTANCE.put( this.key, config );
        }

        this.service.invalidate( this.key, ApplicationInvalidationLevel.CACHE );
    }
}
