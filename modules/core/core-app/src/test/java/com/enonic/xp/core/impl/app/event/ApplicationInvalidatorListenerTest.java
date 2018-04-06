package com.enonic.xp.core.impl.app.event;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.event.Event;

public class ApplicationInvalidatorListenerTest
{
    private ApplicationService service;

    private ApplicationInvalidatorListener listener;

    private ApplicationKey appKey;

    private Event event;

    @Before
    public void setup()
    {
        this.service = Mockito.mock( ApplicationService.class );

        this.listener = new ApplicationInvalidatorListener();
        this.listener.setApplicationService( this.service );

        this.appKey = ApplicationKey.from( "myapp" );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( this.appKey.getName() );
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( "X-Bundle-Type", "application" );
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );

        final BundleEvent bundleEvent = new BundleEvent( BundleEvent.STARTED, bundle );
        this.event = ApplicationEvents.event( bundleEvent );
    }

    @Test
    public void testInvalidate()
    {
        this.listener.onEvent( this.event );
        Mockito.verify( this.service, Mockito.times( 1 ) ).invalidate( this.appKey );
    }
}
