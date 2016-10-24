package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.Assert.*;

public class JvmOsReporterTest
    extends BaseReporterTest
{
    private JvmOsReporter reporter;

    @Override
    protected void initialize()
    {
        this.reporter = new JvmOsReporter();
    }

    @Test
    public void testName()
    {
        assertEquals( "jvm.os", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final JsonNode json = this.reporter.getReport();
        assertNotNull( json );
    }
}
