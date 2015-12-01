package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.Assert.*;

public class JvmInfoReporterTest
    extends BaseReporterTest
{
    private JvmInfoReporter reporter;

    @Override
    protected void initialize()
    {
        this.reporter = new JvmInfoReporter();
    }

    @Test
    public void testName()
    {
        assertEquals( "jvm.info", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final ObjectNode json = this.reporter.getReport();
        assertNotNull( json );
    }
}
