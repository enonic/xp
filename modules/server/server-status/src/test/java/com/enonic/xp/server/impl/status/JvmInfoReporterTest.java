package com.enonic.xp.server.impl.status;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.BaseReporterTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JvmInfoReporterTest
    extends BaseReporterTest<JvmInfoReporter>
{
    public JvmInfoReporterTest()
    {
        super( "jvm.info", MediaType.JSON_UTF_8 );
    }

    @Override
    protected JvmInfoReporter newReporter()
    {
        return new JvmInfoReporter();
    }

    @Test
    void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        assertNotNull( json );
    }
}
