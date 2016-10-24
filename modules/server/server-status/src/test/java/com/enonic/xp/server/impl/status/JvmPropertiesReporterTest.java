package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.Assert.*;

public class JvmPropertiesReporterTest
    extends BaseReporterTest
{
    private JvmPropertiesReporter reporter;

    @Override
    protected void initialize()
    {
        this.reporter = new JvmPropertiesReporter();
    }

    @Test
    public void testName()
    {
        assertEquals( "jvm.properties", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final JsonNode json = this.reporter.getReport();
        assertNotNull( json );
    }
}
