package com.enonic.xp.server.impl.status;

import org.junit.Test;

import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class DeadlockReporterTest
    extends BaseReporterTest<DeadlockReporter>
{
    public DeadlockReporterTest()
    {
        super( "dump.deadlocks", MediaType.PLAIN_TEXT_UTF_8 );
    }

    @Override
    protected DeadlockReporter newReporter()
    {
        return new DeadlockReporter();
    }

    @Test
    public void testThreadDump()
        throws Exception
    {
        assertNotNull( textReport() );
    }
}
