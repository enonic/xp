package com.enonic.xp.launcher.impl.logging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogService;

public class LogServiceImplTest
{
    private LogServiceImpl service;

    @Before
    public void setup()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "foo.bar" );

        this.service = new LogServiceImpl( bundle );
    }

    @Test
    public void testLog()
    {
        this.service.log( LogService.LOG_DEBUG, "test" );
        this.service.log( LogService.LOG_INFO, "test" );
        this.service.log( LogService.LOG_WARNING, "test" );
        this.service.log( LogService.LOG_ERROR, "test" );
        this.service.log( -1, "test" );
    }

    @Test
    public void testLog_withCause()
    {
        this.service.log( LogService.LOG_DEBUG, "test", new Throwable() );
        this.service.log( LogService.LOG_INFO, "test", new Throwable() );
        this.service.log( LogService.LOG_WARNING, "test", new Throwable() );
        this.service.log( LogService.LOG_ERROR, "test", new Throwable() );
        this.service.log( -1, "test", new Throwable() );
    }

    @Test
    public void testLog_ref()
    {
        this.service.log( null, LogService.LOG_DEBUG, "test" );
        this.service.log( null, LogService.LOG_DEBUG, "test", new Throwable() );
    }
}
