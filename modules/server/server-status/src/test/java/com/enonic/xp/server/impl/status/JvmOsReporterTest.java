package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class JvmOsReporterTest
    extends BaseReporterTest<JvmOsReporter>
{
    public JvmOsReporterTest()
    {
        super( "jvm.os", MediaType.JSON_UTF_8 );
    }

    @Override
    protected JvmOsReporter newReporter()
        throws Exception
    {
        return new JvmOsReporter();
    }

    @Test
    public void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        assertNotNull( json );
    }
}
