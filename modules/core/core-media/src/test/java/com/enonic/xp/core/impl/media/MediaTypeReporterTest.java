package com.enonic.xp.core.impl.media;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MediaTypeReporterTest
{
    private MediaTypeReporter reporter;

    @Before
    public void setup()
    {
        this.reporter = new MediaTypeReporter();
    }

    @Test
    public void testName()
    {
        assertEquals( "mediaTypes", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        assertNotNull( this.reporter.getReport() );
    }
}
