package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.Assert.*;

public class JvmMemoryReporterTest
    extends BaseReporterTest
{
    private JvmMemoryReporter reporter;

    @Override
    protected void initialize()
    {
        this.reporter = new JvmMemoryReporter();
    }

    @Test
    public void testName()
    {
        assertEquals( "jvm.memory", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final JsonNode json = this.reporter.getReport();
        assertNotNull( json );
    }
}
