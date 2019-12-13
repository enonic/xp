package com.enonic.xp.core.impl.media;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MediaTypeReporterTest
{
    private MediaTypeReporter reporter;

    @BeforeEach
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
