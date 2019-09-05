package com.enonic.xp.launcher.impl.framework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

public class FrameworkLoggerTest
{
    private FrameworkLogger logger;

    @BeforeEach
    public void setup()
    {
        this.logger = new FrameworkLogger();
    }

    @Test
    public void testLog()
    {
        this.logger.doLog( null, null, FrameworkLogger.LOG_DEBUG, "test", null );
        this.logger.doLog( null, null, FrameworkLogger.LOG_INFO, "test", null );
        this.logger.doLog( null, null, FrameworkLogger.LOG_WARNING, "test", null );
        this.logger.doLog( null, null, FrameworkLogger.LOG_ERROR, "test", null );
        this.logger.doLog( null, null, -1, "test", null );
    }

    @Test
    public void testLog_bundle()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "foo.bar" );

        this.logger.doLog( bundle, null, FrameworkLogger.LOG_DEBUG, "test", null );
    }
}
