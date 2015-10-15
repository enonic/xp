package com.enonic.xp.app;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import static org.junit.Assert.*;

public class ApplicationUpdatedTest
{

    private BundleEvent bundleEvent;

    private Bundle myBundle;

    @Before
    public void setup()
        throws Exception
    {
        bundleEvent = Mockito.mock( BundleEvent.class );
        myBundle = Mockito.mock( Bundle.class );

        Mockito.when( bundleEvent.getType() ).thenReturn( 0x00000001 );
        Mockito.when( myBundle.getSymbolicName() ).thenReturn( "myapplication" );
        Mockito.when( bundleEvent.getBundle() ).thenReturn( myBundle );
    }

    @Test
    public void getEventType()
    {
        final ApplicationEvent event = new ApplicationEvent( bundleEvent );
        assertEquals( event.getState(), ApplicationEvent.INSTALLED );
    }

    @Test
    public void getApplicationKey()
    {
        final ApplicationEvent event = new ApplicationEvent( bundleEvent );
        assertEquals( event.getKey().toString(), "myapplication" );
    }

    @Test
    public void testToString()
    {
        ApplicationEvent event = new ApplicationEvent( bundleEvent );
        assertEquals( event.toString(), "ApplicationEvent{state=INSTALLED, applicationKey=myapplication}" );
    }
}
