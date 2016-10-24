package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class JvmInfoReporterTest
    extends Base2ReporterTest<JvmInfoReporter>
{
    public JvmInfoReporterTest()
    {
        super( "jvm.info", MediaType.JSON_UTF_8 );
    }

    @Override
    protected JvmInfoReporter newReporter()
        throws Exception
    {
        return new JvmInfoReporter();
    }

    @Test
    public void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        assertNotNull( json );
    }
}
