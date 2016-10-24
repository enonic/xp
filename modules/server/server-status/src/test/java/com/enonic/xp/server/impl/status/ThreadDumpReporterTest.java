package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class ThreadDumpReporterTest
    extends BaseReporterTest<ThreadDumpReporter>
{
    public ThreadDumpReporterTest()
    {
        super( "threads", MediaType.PLAIN_TEXT_UTF_8 );
    }

    @Override
    protected ThreadDumpReporter newReporter()
    {
        return new ThreadDumpReporter();
    }

    @Test
    public void testThreadDump()
        throws Exception
    {
        assertNotNull( textReport() );
    }
}
