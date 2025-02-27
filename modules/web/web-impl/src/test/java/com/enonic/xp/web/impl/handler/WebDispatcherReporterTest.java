package com.enonic.xp.web.impl.handler;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebDispatcherReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @Test
    void report()
        throws Exception
    {
        final WebDispatcherImpl dispatcher = new WebDispatcherImpl();
        dispatcher.add( new TestWebHandler() );
        dispatcher.add( new TestWebHandler() );

        final WebDispatcherReporter reporter = new WebDispatcherReporter( dispatcher );

        assertEquals( "http.webHandler", reporter.getName() );
        assertJson( "report.json", reporter );
    }

    private void assertJson( final String fileName, final StatusReporter reporter )
        throws Exception
    {
        assertEquals( MediaType.JSON_UTF_8, reporter.getMediaType() );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );

        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( fileName ),
                                         jsonTestHelper.bytesToJson( outputStream.toByteArray() ) );
    }
}
