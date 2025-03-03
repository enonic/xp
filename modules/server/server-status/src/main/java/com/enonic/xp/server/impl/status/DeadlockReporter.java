package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;

import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class DeadlockReporter
    implements StatusReporter
{
    @Override
    public String getName()
    {
        return "dump.deadlocks";
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.PLAIN_TEXT_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        getDeadlockedThreads(outputStream);
    }

    public static void getDeadlockedThreads( final OutputStream outputStream )
        throws IOException
    {
        final ThreadMXBean threads = ManagementFactory.getThreadMXBean();

        final long[] ids = threads.findDeadlockedThreads();
        if ( ids == null )
        {
            outputStream.write( "No deadlocks detected!".getBytes( StandardCharsets.UTF_8 ) );
            return;
        }

        for ( ThreadInfo info : threads.getThreadInfo( ids, 100 ) )
        {
            outputStream.write( String.format( "%s locked on %s (owned by %s):\n", info.getThreadName(), info.getLockName(), info.getLockOwnerName()).getBytes( StandardCharsets.UTF_8 ) );
            for ( StackTraceElement element : info.getStackTrace() )
            {
                outputStream.write( ( "\t at " + element + "\n" ).getBytes( StandardCharsets.UTF_8 ) );
            }
            outputStream.write( "\n\n".getBytes( StandardCharsets.UTF_8 ) );
        }
    }
}
