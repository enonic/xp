package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import com.codahale.metrics.jvm.ThreadDeadlockDetector;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
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
        final Set<String> deadlocks = new ThreadDeadlockDetector().getDeadlockedThreads();
        final String text = deadlocks.isEmpty() ? "No deadlocks detected!" : Joiner.on( "\n\n" ).join( deadlocks );
        outputStream.write( text.getBytes( Charsets.UTF_8 ) );
    }
}
