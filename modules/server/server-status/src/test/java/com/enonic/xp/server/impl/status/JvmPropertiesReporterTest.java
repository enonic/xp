package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class JvmPropertiesReporterTest
    extends Base2ReporterTest<JvmPropertiesReporter>
{
    public JvmPropertiesReporterTest()
    {
        super( "jvm.properties", MediaType.JSON_UTF_8 );
    }

    @Override
    protected JvmPropertiesReporter newReporter()
        throws Exception
    {
        return new JvmPropertiesReporter();
    }

    @Test
    public void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        assertNotNull( json );
    }
}
