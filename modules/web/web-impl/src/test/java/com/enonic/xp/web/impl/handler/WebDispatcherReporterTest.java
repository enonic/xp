package com.enonic.xp.web.impl.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WebDispatcherReporterTest
{
    @Test
    public void testReport()
    {
        final WebDispatcherImpl dispatcher = new WebDispatcherImpl();
        dispatcher.add( new TestWebHandler() );
        dispatcher.add( new TestWebHandler() );

        final WebDispatcherReporter reporter = new WebDispatcherReporter();
        reporter.setWebDispatcher( dispatcher );

        assertEquals( "http.webHandler", reporter.getName() );
        assertNotNull( reporter.getReport() );
    }
}
