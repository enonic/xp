package com.enonic.xp.core.impl.app;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;

public class ApplicationInvalidatorListenerTest
{
    private ApplicationInvalidator invalidator1;

    private ApplicationInvalidator invalidator2;

    private ApplicationInvalidatorListener listener;

    private ApplicationKey appKey;

    private Event event;

    @Before
    public void setup()
    {
        this.invalidator1 = Mockito.mock( ApplicationInvalidator.class );
        this.invalidator2 = Mockito.mock( ApplicationInvalidator.class );

        this.listener = new ApplicationInvalidatorListener();
        this.listener.addListener( this.invalidator1 );
        this.listener.addListener( this.invalidator2 );

        this.appKey = ApplicationKey.from( "myapp" );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( this.appKey.getName() );

        final BundleEvent bundleEvent = new BundleEvent( BundleEvent.STARTED, bundle );
        this.event = ApplicationEvents.event( bundleEvent );
    }

    @Test
    public void testInvalidate()
    {
        this.listener.onEvent( this.event );

        Mockito.verify( this.invalidator1, Mockito.times( 1 ) ).invalidate( this.appKey );
        Mockito.verify( this.invalidator2, Mockito.times( 1 ) ).invalidate( this.appKey );
    }

    @Test
    public void testInvalidate_noListeners()
    {
        this.listener.removeListener( this.invalidator1 );
        this.listener.removeListener( this.invalidator2 );

        this.listener.onEvent( this.event );

        Mockito.verify( this.invalidator1, Mockito.times( 0 ) ).invalidate( this.appKey );
        Mockito.verify( this.invalidator2, Mockito.times( 0 ) ).invalidate( this.appKey );
    }
}
