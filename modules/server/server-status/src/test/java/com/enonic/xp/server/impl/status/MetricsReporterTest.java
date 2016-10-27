package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class MetricsReporterTest
    extends BaseReporterTest<MetricsReporter>
{
    public MetricsReporterTest()
    {
        super( "metrics", MediaType.JSON_UTF_8 );
    }

    @Override
    protected MetricsReporter newReporter()
        throws Exception
    {
        return new MetricsReporter();
    }

    @Test
    public void testReport()
        throws Exception
    {
        this.params.put( "filter", "" );

        final JsonNode json = jsonReport();
        assertNotNull( json );
    }
}
