package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.Assert.*;

public class JvmGCReporterTest
    extends BaseReporterTest
{
    private JvmGCReporter reporter;

    @Override
    protected void initialize()
    {
        this.reporter = new JvmGCReporter();
    }

    @Test
    public void testName()
    {
        assertEquals( "jvm.gc", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final JsonNode json = this.reporter.getReport();
        assertNotNull( json );
    }
}
