package com.enonic.xp.core.impl.app.config;

import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;

@Component(immediate = true)
public final class ApplicationConfigInvalidator
    implements ConfigurationListener
{
    private ApplicationService service;

    @Override
    public void configurationEvent( final ConfigurationEvent event )
    {
        final String pid = event.getPid();
        final ApplicationKey key = ApplicationKey.from( pid );
        this.service.invalidate( key );
    }

    @Reference
    public void setApplicationService( final ApplicationService service )
    {
        this.service = service;
    }
}
