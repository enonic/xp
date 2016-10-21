package com.enonic.xp.core.impl.app.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;

public class ApplicationConfigInvalidatorTest
{
    private ApplicationService service;

    private ApplicationConfigInvalidator invalidator;

    private ApplicationKey appKey;

    @Before
    public void setup()
    {
        this.service = Mockito.mock( ApplicationService.class );

        this.invalidator = new ApplicationConfigInvalidator();
        this.invalidator.setApplicationService( this.service );

        this.appKey = ApplicationKey.from( "myapp" );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidate()
    {
        final ServiceReference<ConfigurationAdmin> ref = Mockito.mock( ServiceReference.class );
        final ConfigurationEvent event = new ConfigurationEvent( ref, ConfigurationEvent.CM_UPDATED, null, this.appKey.getName() );

        this.invalidator.configurationEvent( event );
        Mockito.verify( this.service, Mockito.times( 1 ) ).invalidate( this.appKey );
    }
}
