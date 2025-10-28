package com.enonic.xp.server.impl.status;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.status.BaseReporterTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeadlockReporterTest
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
    void testThreadDump()
        throws Exception
    {
        assertNotNull( textReport() );
    }
}
