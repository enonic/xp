package com.enonic.xp.server.impl.status;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.BaseReporterTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JvmThreadsReporterTest
    extends BaseReporterTest<JvmThreadsReporter>
{
    public JvmThreadsReporterTest()
    {
        super( "jvm.threads", MediaType.JSON_UTF_8 );
    }

    @Override
    protected JvmThreadsReporter newReporter()
        throws Exception
    {
        return new JvmThreadsReporter();
    }

    @Test
    public void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        assertNotNull( json );
    }
}
