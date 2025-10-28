package com.enonic.xp.core.impl.media;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaTypeReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    private MediaTypeReporter reporter;

    @BeforeEach
    void setup()
    {
        this.reporter = new MediaTypeReporter();
    }

    @Test
    void testName()
    {
        assertEquals( "mediaTypes", this.reporter.getName() );
    }

    @Test
    void testReport()
        throws Exception
    {
        assertJson( "report.json" );
    }

    private void assertJson( final String fileName )
        throws Exception
    {
        assertEquals( MediaType.JSON_UTF_8, reporter.getMediaType() );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );

        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( fileName ),
                                         jsonTestHelper.bytesToJson( outputStream.toByteArray() ) );
    }
}
