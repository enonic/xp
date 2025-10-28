package com.enonic.xp.server.impl.status;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.status.BaseReporterTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ThreadDumpReporterTest
    extends BaseReporterTest<ThreadDumpReporter>
{
    public ThreadDumpReporterTest()
    {
        super( "dump.threads", MediaType.PLAIN_TEXT_UTF_8 );
    }

    @Override
    protected ThreadDumpReporter newReporter()
    {
        return new ThreadDumpReporter();
    }

    @Test
    void testThreadDump()
        throws Exception
    {
        assertNotNull( textReport() );
    }
}
