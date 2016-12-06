package com.enonic.xp.launcher.impl.logging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogService;

import static org.junit.Assert.*;

public class LogServiceFactoryTest
{
    private LogServiceFactory factory;

    @Before
    public void setup()
    {
        this.factory = LogServiceFactory.INSTANCE;
    }

    @Test
    public void testGetAndUnGet()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "foo.bar" );

        final LogService service = this.factory.getService( bundle, null );
        assertNotNull( service );

        this.factory.ungetService( bundle, null, service );
    }
}
