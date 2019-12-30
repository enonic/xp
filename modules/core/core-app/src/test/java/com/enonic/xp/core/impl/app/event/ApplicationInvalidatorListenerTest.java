package com.enonic.xp.core.impl.app.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.event.Event;

public class ApplicationInvalidatorListenerTest
{
    private ApplicationService service;

    private ApplicationInvalidatorListener listener;

    private ApplicationKey appKey;

    private Event event;

    @BeforeEach
    public void setup()
    {
        this.service = Mockito.mock( ApplicationService.class );

        this.listener = new ApplicationInvalidatorListener();
        this.listener.setApplicationService( this.service );

        this.appKey = ApplicationKey.from( "myapp" );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( this.appKey.getName() );
        Mockito.when( bundle.getHeaders() ).thenReturn( Dictionaries.of( "X-Bundle-Type", "application" ) );

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
