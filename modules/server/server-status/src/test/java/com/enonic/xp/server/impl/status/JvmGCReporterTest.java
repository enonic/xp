package com.enonic.xp.server.impl.status;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.BaseReporterTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JvmGCReporterTest
    extends BaseReporterTest<JvmGCReporter>
{
    public JvmGCReporterTest()
    {
        super( "jvm.gc", MediaType.JSON_UTF_8 );
    }

    @Override
    protected JvmGCReporter newReporter()
    {
        return new JvmGCReporter();
    }

    @Test
    void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        assertNotNull( json );
    }
}
