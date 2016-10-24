package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class JvmGCReporterTest
    extends BaseReporterTest<JvmGCReporter>
{
    public JvmGCReporterTest()
    {
        super( "jvm.gc", MediaType.JSON_UTF_8 );
    }

    @Override
    protected JvmGCReporter newReporter()
        throws Exception
    {
        return new JvmGCReporter();
    }

    @Test
    public void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        assertNotNull( json );
    }
}
